package edu.depauw.emulator_ide.verilog_compiler.interpreter;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import edu.depauw.emulator_ide.common.Pointer;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.gui.Main;
import edu.depauw.emulator_ide.verilog_compiler.OpUtil;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ArrayVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.BoolVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.RealVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.UnsignedIntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.VectorVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry.Multiplexer;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.RegVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.AndGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NandGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NotGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.OrGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.XnorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.XorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.Web;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.VerilogFile;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.EmptyExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.PortConnection;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.BinaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.UnaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.BinaryNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.DecimalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.HexadecimalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.OctalNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Slice;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ContinuousAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.EmptyModItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.AndGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.GateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NandGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NotGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.OrGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.XnorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.XorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstance;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstantiation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.ProcedureDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.AllwaysProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.InitialProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.ProcessBase;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.ArrayDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.IdentDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Input;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Int;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Output;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Real;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Unidentified;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Wire;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Reg;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.EmptyStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.SeqBlockStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.WaitStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.CaseStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.CaseXStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.CaseZStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.CaseItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.DefCaseItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.ExprCaseItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.Assignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.BlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.NonBlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForeverStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.RepeatStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.WhileStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching._if_.IfElseStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching._if_.IfStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.SystemTaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.TaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor.Preprocessor;

/**
 * The Follwowing Class can be utilized to Interpret Verilog Modules
 * It will Inturn call the Interpret Methods on the AstNodes after they are Parsed
 * 
 * The Interpreter used to be a visitor but It was in aggreement that an Interpeter with the Interpreter Design Pattern will perform
 * better.
 */

public class Interpreter {

    /**
     * Below are the Fields of the Interpreter
     * Each Interpreter contains an Environment which conntains all the run tume information that the
     * program has access too and an ErrorLog
     */
    private final ErrorLog errorLog;
    private Environment environment;

    public Interpreter(ErrorLog errorLog){
        this.errorLog = errorLog;
        this.environment = new Environment();
    }

    /**
     * Brlow are the private methods that an interpreter can call
     * 
     * @param Expression
     * @return
     */

    public Value interpretExpression(String Expression){
        Source source = new Source(new StringReader(Expression));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        Expression Exp = parse.parseExpression();
        return interpretShallowExpression(Exp);
    }

    public IntVal interpretStatement(String Statement){
        Source source = new Source(new StringReader(Statement));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        Statement Stat = parse.parseStatement();
        interpretShallowStatement(Stat);

        if(errorLog.size() > 0){
            errorLog.printLog();
            return OpUtil.errorOccured();
        } else {
            return OpUtil.success();
        }
    }

    public IntVal interpretModule(String Module){
        Source source = new Source(new StringReader(Module));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        ModuleDeclaration Decl = parse.parseModuleDeclaration();
        interpretModule(Decl);

        if(errorLog.size() > 0){
            errorLog.printLog();
            return OpUtil.errorOccured();
        } else {
            return OpUtil.success();
        }
    }

    public IntVal interpretFile(String FileName){
        try{
			FileReader Reader = new FileReader(FileName);
			return interpretFile(Reader);
		} catch(Exception exc) {
			errorLog.addItem(new ErrorItem("Could not make file stream exception thrown" + exc.toString()));
		}

		return OpUtil.errorOccured();
    }

	public IntVal interpretFile(FileReader Reader){
		Source Source = new Source(Reader);
		Lexer lex = new Lexer(Source, errorLog);

		/**
		 * When Interpereting a File we need to 
		 * run a preprocessor because include statemnts can also occur
		 */
        
		List<Token> tokens = lex.tokenize();
        Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		Parser P = new Parser(tokens, errorLog);
		VerilogFile File = P.parseVerilogFile();

		return interpretFile(File);
	}

	public IntVal interpretFile(FileInputStream Stream){
		Source Source = new Source(Stream);
		Lexer lex = new Lexer(Source, errorLog);
        
		List<Token> tokens = lex.tokenize();
        Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		 Parser P = new Parser(tokens, errorLog);
		 VerilogFile File = P.parseVerilogFile();


		return interpretFile(File);
	}

	private IntVal interpretFile(VerilogFile File){
		for(ModuleDeclaration Decl : File.modules){
			interpretModule(Decl);
		}

		return OpUtil.success();
	}

	private IntVal interpretModule(ModuleDeclaration mod){
		String modName = mod.moduleName;

		if (environment.moduleExists(modName)) {
			OpUtil.errorAndExit("Redeclaration of Module " + modName + "found at " + '[' + mod.position + "] declared !!!", environment.lookupModule(modName).position);
		} else {
			environment.addModule(modName, mod);
		}

		for (ModuleItem modItem : mod.moduleItemList) { 
			interpretModuleItem(modItem);
		}

		return OpUtil.success();
	}

	/*
	 * Below is the code for visiting ModItem constructs
	 */

	 private IntVal interpretModuleItem(ModuleItem Item){
		if(Item instanceof GateDeclaration) return interpretGateDeclaration((GateDeclaration)Item);
		else if (Item instanceof IdentDeclaration) return interpretIdentDeclaration((IdentDeclaration)Item);
		else if (Item instanceof Unidentified.Declaration) return interpretUnidentifiedDeclaration((Unidentified.Declaration)Item);
		else if (Item instanceof ModuleInstance) return interpretModuleInstance((ModuleInstance)Item);
		else if (Item instanceof ModuleInstantiation) return interpretModInstantiation((ModuleInstantiation)Item);
		else if (Item instanceof ProcedureDeclaration) return interpretProcedureDeclaration((ProcedureDeclaration)Item);
		else if (Item instanceof Process) return interpretProcess((ProcessBase)Item);
		else if (Item instanceof ContinuousAssignment) return interpretContinuousAssignment((ContinuousAssignment)Item);
		else if (Item instanceof EmptyModItem) return interpretEmptyModItem((EmptyModItem)Item);
		else {
			OpUtil.errorAndExit("Error: Invalid Module Item Type ~ " + Item.getClass().getName());
			return OpUtil.errorOccured();
		}
	 }

	 private IntVal interpretGateDeclaration(GateDeclaration Item){
		if(Item instanceof AndGateDeclaration) return interpretAndGate((AndGateDeclaration)Item);
		else if (Item instanceof NandGateDeclaration) return interpretNandGate((NandGateDeclaration)Item);
		else if (Item instanceof NorGateDeclaration) return interpretNorGate((NorGateDeclaration)Item);
		else if(Item instanceof NotGateDeclaration) return interpretNotGate((NotGateDeclaration)Item);
		else if(Item instanceof OrGateDeclaration) return interpretOrGate((OrGateDeclaration)Item);
		else if (Item instanceof XnorGateDeclaration) return interpretXnorGate((XnorGateDeclaration)Item);
		else if (Item instanceof XorGateDeclaration) return interpretXorGate((XorGateDeclaration)Item);
		else {
			OpUtil.errorAndExit("Error: Invalid Gate Type ~" + Item.getClass().getName());
			return OpUtil.errorOccured();
		}
	 }

	 /**
	 * This is used to visit any andgate declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretAndGate(AndGateDeclaration decl){

	   List<Expression> inputs = decl.gateConnections;
	   Expression output = inputs.remove(0);
	   Expression input1 = inputs.remove(0);
	   Expression input2 = inputs.remove(0);
	   List<Expression> inputsRest = inputs;

	   Web outputResult = (Web)interpretDeepExpression(output);
	   Web input1Result = (Web)interpretDeepExpression(input1);
	   Web input2Result = (Web)interpretDeepExpression(input2);

	   Web[] inputsResultsRest = new Web[inputsRest.size()];

	   for(int i = 0; i < inputsResultsRest.length; i++){
		 Expression InputRest = inputsRest.remove(0);
		 inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
	   }


		new AndGate(outputResult, input1Result, input2Result, inputsResultsRest);

		return OpUtil.success();
	}

	/**
	 * This is used to visit any orgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretOrGate(OrGateDeclaration decl){
	   List<Expression> inputs = decl.gateConnections;
	   Expression output = inputs.remove(0);
	   Expression input1 = inputs.remove(0);
	   Expression input2 = inputs.remove(0);
	   List<Expression> inputsRest = inputs;

	   Web outputResult = (Web)interpretDeepExpression(output);
	   Web input1Result = (Web)interpretDeepExpression(input1);
	   Web input2Result = (Web)interpretDeepExpression(input2);

	   Web[] inputsResultsRest = new Web[inputsRest.size()];

	   for(int i = 0; i < inputsResultsRest.length; i++){
		 Expression InputRest = inputsRest.remove(0);
		 inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
	   }
	   
	   new OrGate(outputResult, input1Result, input2Result, inputsResultsRest);

	   return OpUtil.success();
	}

	/**
	 * This is used to visit any nandgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	 private IntVal interpretNandGate(NandGateDeclaration decl){

		List<Expression> inputs = decl.gateConnections;
		Expression output = inputs.remove(0);
		Expression input1 = inputs.remove(0);
		Expression input2 = inputs.remove(0);
		List<Expression> inputsRest = inputs;
 
		Web outputResult = (Web)interpretDeepExpression(output);
		Web input1Result = (Web)interpretDeepExpression(input1);
		Web input2Result = (Web)interpretDeepExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
		}
 
 
		 new NandGate(outputResult, input1Result, input2Result, inputsResultsRest);
 
		 return OpUtil.success();
	 }

	/**
	 * This is used to visit any norgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	 private IntVal interpretNorGate(NorGateDeclaration decl){

		List<Expression> inputs = decl.gateConnections;
		Expression output = inputs.remove(0);
		Expression input1 = inputs.remove(0);
		Expression input2 = inputs.remove(0);
		List<Expression> inputsRest = inputs;
 
		Web outputResult = (Web)interpretDeepExpression(output);
		Web input1Result = (Web)interpretDeepExpression(input1);
		Web input2Result = (Web)interpretDeepExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  	Expression InputRest = inputsRest.remove(0);
		  	inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
		}
 
 
		 new NorGate(outputResult, input1Result, input2Result, inputsResultsRest);
 
		 return OpUtil.success();
	 }

	/**
	 * This is used to visit any xorgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	 private IntVal interpretXorGate(XorGateDeclaration decl){

		List<Expression> inputs = decl.gateConnections;
		Expression output = inputs.remove(0);
		Expression input1 = inputs.remove(0);
		Expression input2 = inputs.remove(0);
		List<Expression> inputsRest = inputs;
 
		Web outputResult = (Web)interpretDeepExpression(output);
		Web input1Result = (Web)interpretDeepExpression(input1);
		Web input2Result = (Web)interpretDeepExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
		}
 
 
		 new XorGate(outputResult, input1Result, input2Result, inputsResultsRest);
 
		 return OpUtil.success();
	 }

	 private IntVal interpretXnorGate(XnorGateDeclaration decl){

		List<Expression> inputs = decl.gateConnections;
		Expression output = inputs.remove(0);
		Expression input1 = inputs.remove(0);
		Expression input2 = inputs.remove(0);
		List<Expression> inputsRest = inputs;
 
		Web outputResult = (Web)interpretDeepExpression(output);
		Web input1Result = (Web)interpretDeepExpression(input1);
		Web input2Result = (Web)interpretDeepExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretDeepExpression(InputRest);
		}
 
 
		 new XnorGate(outputResult, input1Result, input2Result, inputsResultsRest);
 
		 return OpUtil.success();
	 }

	/**
	 * This is used to visit any notgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretNotGate(NotGateDeclaration decl){
		List<Expression> connections = decl.gateConnections;

		Expression outputConnection = connections.remove(0);
		Expression inputConnection = connections.remove(0);
		Web output = (Web)interpretDeepExpression(outputConnection);
		Web input = (Web)interpretDeepExpression(inputConnection);

		new NotGate(output, input);

		return OpUtil.success();
	}

	private IntVal interpretModuleInstance(ModuleInstance instance){
		List<Expression> Expression = instance.expList;

		OpUtil.errorAndExit("Error: module instances have not been handled by the interpreter up to this point...");

		return OpUtil.errorOccured();
	}

	private IntVal interpretModInstantiation(ModuleInstantiation modList){
		List<ModuleInstance> modInstances = modList.modList;

		for(ModuleInstance Instance : modInstances){
			interpretModuleInstance(Instance);
		}

		return OpUtil.errorOccured();
	}

	/**
	 * Now we will defined the Procedure Declarations and where those statements reside
	 */

	 private IntVal interpretProcedureDeclaration(ProcedureDeclaration Procedure){
		if(Procedure instanceof FunctionDeclaration) return interpretTaskDeclaration((TaskDeclaration) Procedure);
		else if (Procedure instanceof TaskDeclaration) return interpretFunctionDeclaration((FunctionDeclaration) Procedure);
		else {
			OpUtil.errorAndExit("Unknown Procedure Declaration SubType " + Procedure.getClass().getName());
			return OpUtil.errorOccured();
		}
	 }

	 /**
	 * This is used to visit a task declaration in verilog
	 * 
	 * @param task
	 */

	private IntVal interpretTaskDeclaration(TaskDeclaration task){
		String taskName = task.taskName;

		if (environment.functionExists(taskName)) {
			OpUtil.errorAndExit("Task declaration by the name of " + taskName + " found at [" + task.position
				+ "] already exists at " + environment.lookupTask(taskName).position.toString());
		} else {
			environment.addTask(taskName, task);
		}

		return null;
	}

	private IntVal interpretFunctionDeclaration(FunctionDeclaration function){
		StrVal functionName = OpUtil.fetchFunctionName(function.functionName);
		// May need to finish this later

		if(environment.functionExists(functionName.toString())){
			OpUtil.errorAndExit("Error: no function with the name of " + functionName + " was found");
			return OpUtil.errorOccured();
		} else {
			environment.addFunction(functionName.toString(), function);
			return OpUtil.success();
		}
	}

	private IntVal interpretProcess(ProcessBase process){
		if(process instanceof AllwaysProcess) return interpretAllwaysProcess((AllwaysProcess)process);
		else if(process instanceof InitialProcess) return interpretInitialProcess((InitialProcess)process);
		else {
			OpUtil.errorAndExit("No valid process found with the class name " + process.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	/**
	 * Now we will go through and visit all of the ModuleItem classes
	 */

	private IntVal interpretAllwaysProcess(AllwaysProcess process){
		boolean True = true;
		while(True){
			interpretShallowStatement(process.statement);
		}

		return OpUtil.success();
	}

	/**
	 * Now we will go through and visit all of the ModuleItem classes
	 */

	private IntVal interpretInitialProcess(InitialProcess process){
		return interpretShallowStatement(process.statement);
	}

	/**
	 * This is the code to visit a Continuous Assignment in Verilog.
	 * 
	 * @param assign
	 */

	private IntVal interpretContinuousAssignment(ContinuousAssignment assign){
		for (BlockingAssignment amnt : assign.assignmentList){
			interpretDeepAssignment(amnt);
		}

		return OpUtil.success();
	}

	private IntVal interpretDeepAssignment(BlockingAssignment Assignment){
		Value ExpressionResult = interpretDeepExpression(Assignment.rightHandSide);

		if(Assignment.leftHandSide instanceof Element){
			Element leftHandElement = (Element)Assignment.leftHandSide;
			Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);

			Pointer<Value> Ptr = environment.lookupVariable(leftHandElement.labelIdentifier);
			if(Ptr.deRefrence() instanceof VectorVal){
				OpUtil.deepAssign((VectorVal)Ptr.deRefrence(), leftHandIndex.intValue(), ExpressionResult);
			} else if(Ptr.deRefrence() instanceof ArrayVal){
				OpUtil.deepAssign((ArrayVal<Value>)Ptr.deRefrence(), leftHandIndex.intValue(), ExpressionResult);
			} else {
				OpUtil.errorAndExit("Error: Could not exit the program because the right side is of an invalid type " + ExpressionResult.getClass().getName());
			}
		} else if (Assignment.leftHandSide instanceof Slice){
			Slice Ident = (Slice)Assignment.leftHandSide;
			String Name = Ident.labelIdentifier;

			Value Begin = interpretShallowExpression(Ident.index1.expression);
			Value End = interpretShallowExpression(Ident.index2.expression);

			Pointer<Value> Ptr = environment.lookupVariable(Name);
			Value Vector = Ptr.deRefrence();

			if(Vector instanceof VectorVal){
				VectorVal Elems = (VectorVal)Vector;
				OpUtil.deepAssign(Elems, Begin.intValue(), End.intValue(), Vector);
			} else {
				OpUtil.errorAndExit("Error: Invalid Type for slice expression " + ExpressionResult.getClass().getName());
			}
		} else if (Assignment.leftHandSide instanceof Identifier){
			Identifier Ident = (Identifier)Assignment.leftHandSide;
			String Name = Ident.labelIdentifier;

			Pointer<Value> Ptr = environment.lookupVariable(Name);
			Ptr.assign(ExpressionResult);
		} else {
			OpUtil.errorAndExit("Invalid LValue of Type " + Assignment.leftHandSide.getClass());
			return OpUtil.errorOccured();
		}
		
		return OpUtil.success();
	}

	/**
	 * Below is the code for doing a Deep Pass on Expressions
	 * This means code like +, - are interpreted into Adder/Subtractor circuitry
	 * The Assignment is also a deep assignment so the Values should be assigned wires to connect them.
	 */

	/**
	 * This is used to visit a empty mod item in verilog
	 * 
	 * @param task
	 */

	private IntVal interpretEmptyModItem(EmptyModItem macro){
		return null; // this class is just for completeness
	}

	private IntVal interpretIdentDeclaration(IdentDeclaration declaration){
		if(declaration instanceof ArrayDeclaration) return interpretArrayDeclaration((ArrayDeclaration)declaration);
		else if(declaration instanceof Input.Wire.Vector.Ident) return interpretDeclaration((Input.Wire.Scalar.Ident)declaration);
		else if(declaration instanceof Input.Reg.Vector.Ident) return interpretDeclaration((Input.Reg.Scalar.Ident)declaration);
		else if(declaration instanceof Input.Wire.Scalar.Ident) return interpretDeclaration((Input.Wire.Scalar.Ident)declaration);
		else if(declaration instanceof Input.Reg.Scalar.Ident) return interpretDeclaration((Input.Reg.Scalar.Ident)declaration);
		else if(declaration instanceof Output.Wire.Vector.Ident) return interpretDeclaration((Output.Wire.Vector.Ident)declaration);
		else if(declaration instanceof Output.Reg.Vector.Ident) return interpretDeclaration((Output.Reg.Vector.Ident)declaration);
		else if(declaration instanceof Output.Wire.Scalar.Ident) return interpretDeclaration((Output.Wire.Scalar.Ident)declaration);
		else if(declaration instanceof Output.Reg.Scalar.Ident) return interpretDeclaration((Output.Reg.Scalar.Ident)declaration);
		else if(declaration instanceof Wire.Vector.Ident) return interpretDeclaration((Wire.Vector.Ident)declaration);
		else if(declaration instanceof Reg.Vector.Ident) return interpretDeclaration((Reg.Vector.Ident)declaration);
		else if(declaration instanceof Wire.Scalar.Ident) return interpretDeclaration((Wire.Scalar.Ident)declaration);
		else if(declaration instanceof Reg.Scalar.Ident) return interpretDeclaration((Reg.Scalar.Ident)declaration);
		else if(declaration instanceof Int.Ident) return interpretDeclaration((Int.Ident)declaration);
		else {
			OpUtil.errorAndExit("Error Could not find Ident Declaration with the following type " + declaration.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	private IntVal interpretArrayDeclaration(ArrayDeclaration declaration){
		if(declaration instanceof Reg.Scalar.Array) return interpretDeclaration((Reg.Scalar.Array)declaration);
		else if(declaration instanceof Reg.Vector.Array) return interpretDeclaration((Reg.Vector.Array)declaration);
		else if(declaration instanceof Int.Array) return interpretDeclaration((Int.Array)declaration);
		else {
			OpUtil.errorAndExit("No Array Type found of type " + declaration.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	private IntVal interpretDeclaration(Input.Wire.Vector.Ident decl){
		Expression exp1 = decl.GetIndex1();
		Expression exp2 = decl.GetIndex2();

		Value val1 = interpretShallowExpression(exp1);
		Value val2 = interpretShallowExpression(exp2);

		if (!environment.variableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(val1.intValue(), val2.intValue()));
		} else {
			OpUtil.errorAndExit("Error identifier allready exists...");
		}

		return OpUtil.success();
	}

	private IntVal interpretDeclaration(Reg.Scalar.Array decl){
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new ArrayVal<RegVal>(ArraySize));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	private IntVal interpretDeclaration(Reg.Vector.Array decl){
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new ArrayVal<VectorVal>(ArraySize));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	private IntVal interpretDeclaration(Int.Array decl){
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new ArrayVal<IntVal>(ArraySize));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. 
	 * Ex. input a, b, c ...;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Input.Reg.Vector.Ident decl){
		Expression exp1 = decl.GetIndex1();
		Expression exp2 = decl.GetIndex2();

		Value exp1Val = interpretShallowExpression(exp1);
		Value exp2Val = interpretShallowExpression(exp2);

		if (!environment.variableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(exp1Val.intValue(), exp2Val.intValue()));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input VectorVal declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Input.Wire.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new WireVal());
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input VectorVal declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Input.Reg.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Wire.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new WireVal());
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is used to visit any wire VectorVal declaration in verilog. Ex. wire [31:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Wire.Vector.Ident decl){
		Expression index1 = decl.GetIndex1();
		 // check whether the expressions return ints
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if (!environment.variableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Reg.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Reg.Vector.Ident decl){
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return null;
	}

	/**
	 * This is used to visit any output scalar declaration in Verilog. Ex. output a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Output.Wire.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new WireVal());
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is where I will declare the output Register Scalar declaration
	 * 
	 * @param Jacob Bauer
	 */

	private IntVal interpretDeclaration(Output.Reg.Scalar.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	private IntVal interpretDeclaration(Output.Wire.Vector.Ident decl){
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Output.Reg.Vector.Ident decl, Object... argv){
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Int.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new IntVal(0));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretDeclaration(Real.Ident decl){
		if(!environment.variableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RealVal(0));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any Unidentified declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	private IntVal interpretUnidentifiedDeclaration(Unidentified.Declaration decl){
		String Current = decl.declaration;

		if(!environment.variableExists(Current)){
			environment.addVariable(Current, null);
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + Current);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}
	
	/**
	 * Below we will place the code for assignment types in verilog
	 * @param assign
	 * @param argv
	 * @return
	 */

	private IntVal interpretShallowStatement(Statement Stat){
		if(Stat instanceof CaseStatement) return interpretCaseStatement((CaseStatement)Stat);
		else if (Stat instanceof Assignment) return interpretShallowAssignment((Assignment)Stat);
		else if (Stat instanceof IfStatement) return interpretIfStatement((IfStatement)Stat);
		else if (Stat instanceof ForeverStatement) return interpretForeverLoop((ForeverStatement)Stat);
		else if (Stat instanceof ForStatement) return interpretForLoop((ForStatement)Stat);
		else if (Stat instanceof RepeatStatement) return interpretRepeatLoop((RepeatStatement)Stat);
		else if (Stat instanceof WhileStatement)  return interpretWhileLoop((WhileStatement)Stat);
		else if (Stat instanceof TaskStatement) return interpretTaskCall((TaskStatement)Stat);
		else if (Stat instanceof SeqBlockStatement) return interpretShallowBlockOfStatements((SeqBlockStatement)Stat);
		else {
			OpUtil.errorAndExit("Error: Invalid Statement Node Found");
			return OpUtil.errorOccured();
		}
	}

	private IntVal interpretDeepStatement(Statement Stat){
		if(Stat instanceof Assignment) return interpretDeepAssignment((Assignment)Stat);
		else if(Stat instanceof SeqBlockStatement) return interpretDeepBlockOfStatements((SeqBlockStatement)Stat);
	    else {
			OpUtil.errorAndExit("Invalid Type Node");
			return OpUtil.errorOccured();
		}
	}

	

	private IntVal interpretShallowAssignment(Assignment assign){
		if(assign instanceof BlockingAssignment) return interpretShallowBlockingAssignment((BlockingAssignment)assign);
		else if (assign instanceof NonBlockingAssignment) return interpretShallowNonBlockingAssignment((NonBlockingAssignment)assign);
		else {
			OpUtil.errorAndExit("Invalid Assingment type found at position " + assign.position);
			return OpUtil.errorOccured();
		}
	}

	private IntVal interpretDeepAssignment(Assignment assign){
		if(assign instanceof BlockingAssignment) return interpretDeepBlockingAssignment((BlockingAssignment)assign);
		else if (assign instanceof NonBlockingAssignment) return interpretDeepNonBlockingAssignment((NonBlockingAssignment)assign);
		else {
			OpUtil.errorAndExit("Invalid Assingment type found at position " + assign.position);
			return OpUtil.errorOccured();
		}
	}
	
	private IntVal interpretShallowBlockingAssignment(BlockingAssignment assign) {
		 Expression exp = assign.rightHandSide;
		 Value expVal = interpretShallowExpression(exp);
		 
		 if(assign.leftHandSide instanceof Element){
			Element leftHandElement = (Element)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
			if(leftHandDeref instanceof ArrayVal){
				ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
				leftHandArray.SetElemAtIndex(leftHandIndex.intValue(), expVal);
			} else if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;
				CircuitElem elem = leftHandVector.getValue(leftHandIndex.intValue());
				if(elem instanceof RegVal){
					RegVal elemReg = (RegVal)elem;
					elemReg.setSignal(expVal.boolValue());
				} else {
					OpUtil.errorAndExit("Error: Invalid Type for soft assignment " + elem.getClass().getName());
				}
			} else {
				OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
			}
		 } else if(assign.leftHandSide instanceof Slice){
			Slice leftHandSlice = (Slice)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
			Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);

			if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;

				OpUtil.shallowAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), expVal.longValue());
			} else {
				OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
				return OpUtil.errorOccured();
			}
		 } else if(assign.leftHandSide instanceof Identifier){
			Identifier leftHandIdent = (Identifier)assign.leftHandSide;
			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);
			leftHandPtr.assign(expVal);
		 } else {
			OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
			return OpUtil.errorOccured();
		 }

		 return OpUtil.success();
	}

	private IntVal interpretDeepBlockingAssignment(BlockingAssignment assign){
		 Expression exp = assign.rightHandSide;
		 Value expVal = interpretDeepExpression(exp);
		 
		 if(assign.leftHandSide instanceof Element){
			Element leftHandElement = (Element)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
			if(leftHandDeref instanceof ArrayVal){
				ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
				OpUtil.deepAssign(leftHandArray, leftHandIndex.intValue(), expVal);
			} else if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;
				OpUtil.deepAssign(leftHandVector, leftHandIndex.intValue(), expVal);
			} else {
				OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
			}
		 } else if(assign.leftHandSide instanceof Slice){
			Slice leftHandSlice = (Slice)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
			Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);

			if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;
				OpUtil.deepAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), expVal);
			} else {
				OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
				return OpUtil.errorOccured();
			}
		 } else if(assign.leftHandSide instanceof Identifier){
			Identifier leftHandIdent = (Identifier)assign.leftHandSide;
			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);
			if(leftHandPtr.deRefrence() instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandPtr.deRefrence();
				if(expVal instanceof CircuitElem){
					CircuitElem expCircuitElem = (CircuitElem)expVal;
					OpUtil.deepAssign(leftHandVector, expCircuitElem);
				} else if (expVal instanceof VectorVal) {
					VectorVal expVectorVal = (VectorVal)expVal;
					OpUtil.deepAssign(leftHandVector, expVectorVal);
				} else {
					OpUtil.errorAndExit("Error: Cannot Exit the program because ");
				}
			} else if (leftHandPtr.deRefrence() instanceof CircuitElem){
				CircuitElem leftHandVector = (CircuitElem)leftHandPtr.deRefrence();
				if(expVal instanceof CircuitElem){
					CircuitElem expCircuitElem = (CircuitElem)expVal;
					OpUtil.deepAssign(leftHandVector, expCircuitElem);
				} else if (expVal instanceof VectorVal) {
					VectorVal expVectorVal = (VectorVal)expVal;
					OpUtil.deepAssign(leftHandVector, expVectorVal);
				} else {
					OpUtil.errorAndExit("Error: Cannot Exit the program because ");
				}
			} else {
				OpUtil.errorAndExit("Error: Cannot perform assingment with types " + expVal.getClass().toString());
			}
		 } else {
			OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
			return OpUtil.errorOccured();
		 }

		 return OpUtil.success();
	}

	private IntVal interpretShallowNonBlockingAssignment(NonBlockingAssignment assign){
		List<Value> resultList = new LinkedList<Value>();
		for(Expression exp: assign.rightHandSide){
		 	Value rhsVal = interpretShallowExpression(exp);
			resultList.add(rhsVal);
		}

		for(int i = 0; i < assign.leftHandSide.size(); i++){
			if(assign.leftHandSide.get(i) instanceof Element){
				Element leftHandElement = (Element)assign.leftHandSide.get(i);
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
				if(leftHandDeref instanceof ArrayVal){
					ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
					leftHandArray.SetElemAtIndex(leftHandIndex.intValue(), resultList.get(i));
				} else if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
					CircuitElem elem = leftHandVector.getValue(leftHandIndex.intValue());
					if(elem instanceof RegVal){
						RegVal elemReg = (RegVal)elem;
						elemReg.setSignal(resultList.get(i).boolValue());
					} else {
						OpUtil.errorAndExit("Error: Invalid Type for soft assignment " + elem.getClass().getName());
					}
				} else {
					OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
				}
			 } else if(assign.leftHandSide instanceof Slice){
				Slice leftHandSlice = (Slice)assign.leftHandSide;
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
				Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);
	
				if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
	
					OpUtil.shallowAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), resultList.get(i).longValue());
				} else {
					OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
					return OpUtil.errorOccured();
				}
			 } else if(assign.leftHandSide instanceof Identifier){
				Identifier leftHandIdent = (Identifier)assign.leftHandSide;
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);
				leftHandPtr.assign(resultList.get(i));
			 } else {
				OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
				return OpUtil.errorOccured();
			 }
		}

		return OpUtil.success();
	}

	private IntVal interpretDeepNonBlockingAssignment(NonBlockingAssignment assign){
		List<Value> resultList = new LinkedList<Value>();
		for(Expression exp: assign.rightHandSide){
		 	Value rhsVal = interpretDeepExpression(exp);
			resultList.add(rhsVal);
		}

		for(int i = 0; i < assign.leftHandSide.size(); i++){
			if(assign.leftHandSide.get(i) instanceof Element){
				Element leftHandElement = (Element)assign.leftHandSide.get(i);
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
				if(leftHandDeref instanceof ArrayVal){
					ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
					OpUtil.deepAssign(leftHandArray, leftHandIndex.intValue(), resultList.get(i));
				} else if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
					OpUtil.deepAssign(leftHandVector, leftHandIndex.intValue(), resultList.get(i));
				} else {
					OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
				}
			 } else if(assign.leftHandSide instanceof Slice){
				Slice leftHandSlice = (Slice)assign.leftHandSide;
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
				Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);
	
				if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
					OpUtil.deepAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), resultList.get(i));
				} else {
					OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
					return OpUtil.errorOccured();
				}
			 } else if(assign.leftHandSide.get(i) instanceof Identifier){
				Identifier leftHandIdent = (Identifier)assign.leftHandSide.get(i);
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);
				if(leftHandPtr.deRefrence() instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandPtr.deRefrence();
					if(resultList.get(i) instanceof CircuitElem){
						CircuitElem expCircuitElem = (CircuitElem)resultList.get(i);
						OpUtil.deepAssign(leftHandVector, expCircuitElem);
					} else if (resultList.get(i) instanceof VectorVal) {
						VectorVal expVectorVal = (VectorVal)resultList.get(i);
						OpUtil.deepAssign(leftHandVector, expVectorVal);
					} else {
						OpUtil.errorAndExit("Error: Cannot Exit the program because ");
					}
				} else if (leftHandPtr.deRefrence() instanceof CircuitElem){
					CircuitElem leftHandVector = (CircuitElem)leftHandPtr.deRefrence();
					if(resultList.get(i) instanceof CircuitElem){
						CircuitElem expCircuitElem = (CircuitElem)resultList.get(i);
						OpUtil.deepAssign(leftHandVector, expCircuitElem);
					} else if (resultList.get(i) instanceof VectorVal) {
						VectorVal expVectorVal = (VectorVal)resultList.get(i);
						OpUtil.deepAssign(leftHandVector, expVectorVal);
					} else {
						OpUtil.errorAndExit("Error: Cannot Exit the program because ");
					}
				} else {
					OpUtil.errorAndExit("Error: Cannot perform assingment with types " + resultList.get(i).getClass().toString());
				}
			 } else {
				OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
				return OpUtil.errorOccured();
			 }
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit case statements in verilog
	 * 
	 * @param assign
	 */

	private IntVal interpretCaseStatement(CaseStatement stat){
		if(stat instanceof CaseXStatement) return interpretCaseXStatement((CaseXStatement) stat);
		else if (stat instanceof CaseZStatement) return interpretCaseZStatement((CaseZStatement) stat);
		else {
			Value switchExpVal = interpretShallowExpression(stat.exp);

			loop: for (CaseItem item : stat.itemList){
				if (item instanceof ExprCaseItem) {
					ExprCaseItem exprItem = (ExprCaseItem)item;

					for (Expression CaseExp : exprItem.expList) {
						Value exprValue = interpretShallowExpression(CaseExp);

						if (OpUtil.caseBoolean(switchExpVal, exprValue)) {
							interpretShallowStatement(exprItem.statement);
							break loop;
						}
					}
				} else {
					DefCaseItem exprItem = (DefCaseItem)item;
					interpretShallowStatement(exprItem.statement);
					break;
				}
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit case statements in verilog
	 * 
	 * @param assign
	 */

	private IntVal interpretCaseXStatement(CaseXStatement stat){
		Value switchExp = interpretShallowExpression(stat.exp);

		loop: for (CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (Expression CaseExp : exprItem.expList) {
					Value exprValue = interpretShallowExpression(CaseExp);

					if (OpUtil.caseBoolean(switchExp, exprValue)) {
						interpretShallowStatement(exprItem.statement);
						break loop;
					}
				}
			} else {
				DefCaseItem exprItem = (DefCaseItem)item;
				interpretShallowStatement(exprItem.statement);
				break;
			}
		}

		return OpUtil.success();
	}

	private IntVal interpretCaseZStatement(CaseZStatement stat){
		Value switchExp = interpretShallowExpression(stat.exp);

		loop: for (CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (Expression CaseExp : exprItem.expList) {
					Value exprValue = interpretShallowExpression(CaseExp);

					if (OpUtil.caseBoolean(switchExp, exprValue)) {
						interpretShallowStatement(exprItem.statement);
						break loop;
					}
				}
			} else {
				DefCaseItem exprItem = (DefCaseItem)item;
				interpretShallowStatement(exprItem.statement);
				break;
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a for loop in verilog
	 * 
	 * @param forLoop
	 */

	private IntVal interpretForLoop(ForStatement forLoop){
		for (interpretShallowBlockingAssignment(forLoop.init); interpretShallowExpression(forLoop.exp).boolValue(); interpretShallowStatement(forLoop.change)) {
			interpretShallowStatement(forLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a forever loop in verilog
	 * 
	 * @param foreverLoop
	 */

	private IntVal interpretForeverLoop(ForeverStatement foreverLoop){
		boolean tf = true;

		while(tf) {
			interpretShallowStatement(foreverLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	private IntVal interpretIfElseStatement(IfElseStatement ifElseStatement){
		Value expr = interpretShallowExpression(ifElseStatement.condition);
		if (expr.boolValue()) {
			return interpretShallowStatement(ifElseStatement.trueStatement);
		} else {
		    return interpretShallowStatement(ifElseStatement.falseStatement);
		}
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	private IntVal interpretIfStatement(IfStatement ifStatement){
		if(ifStatement instanceof IfElseStatement) return interpretIfElseStatement((IfElseStatement)ifStatement);
		else {
			Value expr = interpretShallowExpression(ifStatement.condition);

			if (expr.boolValue()) { 
				return interpretShallowStatement(ifStatement.trueStatement); 
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a repeat statement in verilog
	 * 
	 * @param stat
	 */

	private IntVal interpretRepeatLoop(RepeatStatement stat){
		Value expr = interpretShallowExpression(stat.exp);

		if (expr.isLongValue() || expr.isVector()) {
			long amount = expr.longValue();

			for (long i = 0; i < amount; i++) {
				interpretShallowStatement(stat.stat);
			}
		} else if (expr.isWire() || expr.isRegister() || expr.isBoolValue()) {
			if (expr.boolValue()) {
				interpretShallowStatement(stat.stat);
			}
		} else {
			OpUtil.errorAndExit("Error Unknown type for Repeat Statement");
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a seq block in verilog
	 * 
	 * @param stat
	 */

	private IntVal interpretShallowBlockOfStatements(SeqBlockStatement stat){
		for (Statement stmt : stat.statementList) {
			interpretShallowStatement(stat);
		}

		return OpUtil.success();
	}

	private IntVal interpretDeepBlockOfStatements(SeqBlockStatement stat){
		for (Statement stmt : stat.statementList) {
			interpretDeepStatement(stat);
		}

		return OpUtil.success();
	}



	/**
	 * This is used to visit a taskcall in verilog
	 * 
	 * @param stat
	 */

	private IntVal interpretTaskCall(TaskStatement task){
		String tname = task.taskName;

		if (environment.taskExists(tname)) {
			// Collect symbol table data from the function
			TaskDeclaration funcData = environment.lookupTask(tname);
			environment.addStackFrame(tname);
			environment.BeginParamaterDeclarations();

			List<String> paramaterNames = new LinkedList<String>();

			for(ModuleItem Decl : funcData.paramaters){
				String paramaterName = OpUtil.getParamaterName(Decl);
				paramaterNames.add(paramaterName);
			}

			environment.EndParamaterDeclarations();
			environment.removeStackFrame();

			if (task.argumentList.size() == funcData.paramaters.size() && task.argumentList.size() == funcData.paramaters.size()) {
				// Assign parameter value
				for(int i = 0; i < paramaterNames.size(); i++){
					String paramaterName = paramaterNames.get(i);
					Pointer<Value> paramaterValue = environment.lookupVariable(paramaterName);
					Expression argExpr = task.argumentList.get(i);
					Value argValue = interpretShallowExpression(argExpr);
					paramaterValue.assign(argValue);
				}
			} else {
				OpUtil.errorAndExit("Argument amount mismatch " + tname + " [Expected -> " + paramaterNames.size()
					+ " | Got -> " + task.argumentList.size() + " ]");
				return OpUtil.errorOccured();
			}

			interpretShallowStatement(funcData.stat);
		} else {
			OpUtil.errorAndExit("Function Entry " + tname + " Doesnt Exist", task.position);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a system task statement in verilog
	 * 
	 * @param stat
	 */

	private IntVal visit(SystemTaskStatement task){
		String taskName = task.taskName;

		if (taskName.equals("fclose")) {
			Value fileDescriptor = interpretShallowExpression(task.argumentList.get(0));
			FileReader Scanner = environment.getFileReader(fileDescriptor.intValue());
			try{
				Scanner.close();
				environment.clearFileReader(fileDescriptor.intValue());
			} catch (Exception exp){
				OpUtil.errorAndExit(exp.toString());
				return OpUtil.errorOccured();
			}
		} else if (taskName.equals("display")) {
			if (task.argumentList.size() == 2) {
			 	Value fString = interpretShallowExpression(task.argumentList.get(0));
				Value  fData = interpretShallowExpression(task.argumentList.get(1));

				PrintStream Stream = new PrintStream(Main.getByteOutputStream());
				Stream.printf(fString.toString(), fData.toString());
				Stream.flush();
			} else if (task.argumentList.size() == 1) {
				Value data = interpretShallowExpression(task.argumentList.get(0));				
				Main.getByteOutputStream().writeBytes(("\t" + data.toString() + '\n').getBytes()); // write to standard output in the gui
			} else {
				OpUtil.errorAndExit("Unknown number of print arguments in " + task.taskName, task.position);
			}

		} else if (taskName.equals("finish")) {
			OpUtil.errorAndExit("Program is finished!!! Program exited successfully!!!");
		} else if (taskName.equals("setMemory")) {
			Value arg1 = interpretShallowExpression(task.argumentList.get(0));
			Value arg2 = interpretShallowExpression(task.argumentList.get(1));
			
		} else if (taskName.equals("setRegister")) {
			Value arg1 = interpretShallowExpression(task.argumentList.get(0));
			Value arg2 = interpretShallowExpression(task.argumentList.get(1));
			
		} else if (taskName.equals("setStatus")) {
			Value arg1 = interpretShallowExpression(task.argumentList.get(0));
			Value arg2 = interpretShallowExpression(task.argumentList.get(1));
			
		} else {
			OpUtil.errorAndExit("Unknown system task declaration " + taskName, task.position);
		}

		return null;
	}

	private IntVal interpretWaitStatement(WaitStatement wait){
		Expression expr = wait.exp;
		while(interpretShallowExpression(expr).boolValue());
		return interpretShallowStatement(wait.stat);
	}

	/**
	 * This is used to visit a while loop in verilog
	 * 
	 * @param whileLoop
	 */

	private IntVal interpretWhileLoop(WhileStatement whileLoop){

		while(interpretShallowExpression(whileLoop.exp).boolValue()) {
			interpretShallowStatement(whileLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is the code for visiting empty statements this is here just for completion
	 * 
	 * @param none
	 */

	private IntVal visit(EmptyStatement stat){
		// this is empty it is just a placeholder
		return OpUtil.success();
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	private Value interpretShallowExpression(Expression exp){
		if(exp instanceof BinaryOperation) return interpretShallowBinaryOperation((BinaryOperation)exp);
		else if (exp instanceof UnaryOperation) return interpretShallowUnaryOperation((UnaryOperation)exp);
		else if (exp instanceof Concatenation) return interpretShallowConcatenation((Concatenation)exp);
		else if (exp instanceof FunctionCall) return interpretShallowFunctionCall((FunctionCall)exp);
		else if (exp instanceof TernaryOperation) return interpretShallowTernaryOperation((TernaryOperation)exp);
		else if (exp instanceof BinaryNode) return interpretBinaryNode((BinaryNode)exp);
		else if (exp instanceof DecimalNode) return interpretDecimalNode((DecimalNode)exp);
		else if (exp instanceof HexadecimalNode) return interpretHexadecimalNode((HexadecimalNode)exp);
		else if (exp instanceof OctalNode) return interpretOctalNode((OctalNode)exp);
		else if (exp instanceof StringNode) return interpretStringNode((StringNode)exp);
		else if (exp instanceof ConstantExpression) return interpretConstantExpression((ConstantExpression)exp);
		else {
			OpUtil.errorAndExit("Error: Could not find an expression of type");
			return OpUtil.errorOccured();
		}
	}

	private Value interpretDeepExpression(Expression exp){
		if(exp instanceof BinaryOperation) return interpretDeepBinaryOperation((BinaryOperation)exp);
		else if (exp instanceof UnaryOperation) return interpretDeepUnaryOperation((UnaryOperation)exp);
		else if (exp instanceof Concatenation) return interpretDeepConcatenation((Concatenation)exp);
		else if (exp instanceof FunctionCall) return interpretDeepFunctionCall((FunctionCall)exp);
		else if (exp instanceof TernaryOperation) return interpretDeepTernaryOperation((TernaryOperation)exp);
		else if (exp instanceof PortConnection) return interpretDeepPortConnection((PortConnection)exp);
		else {
			OpUtil.errorAndExit("Error: Could not find an expression of type");
			return OpUtil.errorOccured();
		}
	}

	private Value interpretDeepBinaryOperation(BinaryOperation op){
		Value left = interpretDeepExpression(op.left);
		Value right = interpretDeepExpression(op.right);

		
		switch(op.Op){
			case PLUS: return OpUtil.createAdder(left, right);
			case MINUS: return OpUtil.createSubtractor(left, right);
			case BAND: return OpUtil.bitwiseAndCircuit(left, right);
			case BOR: return OpUtil.bitwiseOrCircuit(left, right);
			case BXOR: return OpUtil.bitwiseXorCircuit(left, right);
			case BXNOR: return OpUtil.bitwiseXnorCircuit(left, right);
			default:
				OpUtil.errorAndExit("Invalid deep operation in verilog of type " + left.getClass() + " "
					+ op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}
	}

	private Value interpretShallowBinaryOperation(BinaryOperation op){
		Value left = interpretShallowExpression(op.left);
		Value right = interpretShallowExpression(op.right);

		switch(op.Op){
			case PLUS: return OpUtil.add(left, right);
			case MINUS: return OpUtil.minus(left, right);
			case TIMES: return OpUtil.times(left, right);
			case DIV: return OpUtil.div(left, right);
			case MOD: return OpUtil.mod(left, right);
			case EQ2: return OpUtil.lazyEquality(left, right);
			case EQ3: return OpUtil.strictEquality(left, right);
			case NE1: return OpUtil.lazyInequality(left, right);
			case NE2: return OpUtil.strictInequality(left, right);
			case LAND: return OpUtil.logicalAnd(left, right);
			case LOR: return OpUtil.logicalOr(left, right);
			case LE: return OpUtil.lessThanOrEqualTo(left, right);
			case LT: return OpUtil.lessThan(left, right);
			case GE: return OpUtil.greaterThanOrEqualTo(left, right);
			case GT: return OpUtil.greaterThan(left, right);
			case BAND: return OpUtil.bitwiseAnd(left, right);
			case BOR: return OpUtil.bitwiseOr(left, right);
			case BXOR: return OpUtil.exclusiveOr(left, right);
			case BXNOR: return OpUtil.exclusiveNor(left, right);
			case LSHIFT: return OpUtil.leftShift(left, right);
			case RSHIFT: return OpUtil.rightShift(left, right);
			default:
				OpUtil.errorAndExit("Invalid operation in verilog of type " + left.getClass() + " "
					+ op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	private Value interpretShallowUnaryOperation(UnaryOperation op){
		Value right = interpretShallowExpression(op);

		switch(op.Op){
			case PLUS: return right;
			case MINUS: return OpUtil.negation(right);
			case LNEG: return OpUtil.logicalNegation(right);
			case BNEG: return OpUtil.bitwiseNegation(right);
			default: OpUtil.errorAndExit("Unknown unary operation in verilog of type " + op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	 private Value interpretDeepUnaryOperation(UnaryOperation op){
		Value right = interpretDeepExpression(op);

		switch(op.Op){
			case BNEG: 
				return OpUtil.notGateCircuit(right);
			default: 
				OpUtil.errorAndExit("Unknown unary operation in verilog of type " + op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	private Value interpretDeepConcatenation(Concatenation concat){
		int size = 0;

		for(Expression expr : concat.circuitElementExpressionList){
			Value Res = interpretDeepExpression(expr);

			if (Res.isVector()) {
				size += ((VectorVal)Res).getSize();
			} else {
				size++; // it is a CircuitElem
			}

		}

		VectorVal  newVec = new VectorVal(size - 1, 0);
		int total = size - 1;

		for(Expression exp : concat.circuitElementExpressionList){
			Value valExp = interpretDeepExpression(exp);

			if (valExp.isVector()) {
				VectorVal vec = (VectorVal)valExp;
				if (vec.getIndex1() <= vec.getIndex2()) {

					for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
						newVec.setValue(total, vec.getValue(v));
					}

				} else {

					for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
						newVec.setValue(total, vec.getValue(v));
					}

				}

			} else {
				CircuitElem circ = (CircuitElem)valExp;
				newVec.setValue(total, circ);
				total--;
			}
		}

		return newVec;
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	 private Value interpretShallowConcatenation(Concatenation concat){
		int size = 0;

		for(Expression expr : concat.circuitElementExpressionList){
			Value Res = interpretDeepExpression(expr);

			if (Res.isVector()) {
				size += ((VectorVal)Res).getSize();
			} else {
				size++; // it is a CircuitElem
			}
		}

		VectorVal  newVec = new VectorVal(size - 1, 0);
		int total = size - 1;

		for(Expression exp : concat.circuitElementExpressionList){
			Value valExp = interpretShallowExpression(exp);

			if (valExp.isVector()) {
				VectorVal vec = (VectorVal)valExp;
				if (vec.getIndex1() <= vec.getIndex2()) {

					for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
						newVec.setValue(total, new RegVal(vec.getValue(v).getStateSignal()));
					}

				} else {

					for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
						newVec.setValue(total, new RegVal(vec.getValue(v).getStateSignal()));
					}

				}

			} else {
				CircuitElem circ = (CircuitElem)valExp;
				newVec.setValue(total, circ);
				total--;
			}
		}

		return newVec;
	}

	/**
	 * This is the code for visiting Constant Expressions
	 * 
	 * @param expr
	 */

	private Value interpretConstantExpression(ConstantExpression expr){ 
		return interpretShallowExpression(expr.expression);
	}

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	private Value interpretEmptyExpression(EmptyExpression expr){ 
		return OpUtil.success(); 
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	private Value interpretShallowFunctionCall(FunctionCall call){
		if(call instanceof SystemFunctionCall) return interpretSystemFunctionCall((SystemFunctionCall)call);
		else {
		String tname = call.functionName;

		if (environment.functionExists(tname)) {
			// Collect symbol table data from the function
			FunctionDeclaration funcData = environment.lookupFunction(tname);

			environment.addScope();
			environment.addStackFrame(tname);
			List<String> paramaterNames = new LinkedList<String>();
			for(ModuleItem Parameter : funcData.paramaters){
				String parameterName = OpUtil.getParamaterName(Parameter);
				paramaterNames.add(parameterName);
			} // declare the return variable for the function

			Pointer<Value> returnData = environment.lookupVariable(tname); // get return object

			if (call.argumentList.size() == funcData.paramaters.size() && call.argumentList.size() == paramaterNames.size()) {
				for(int i = 0; i < call.argumentList.size(); i++){
					String paramaterName = paramaterNames.get(i);
					Pointer<Value> paramValue = environment.lookupVariable(paramaterName);
					Expression paramExp = call.argumentList.get(i);
					Value argVal = interpretShallowExpression(paramExp);
					paramValue.assign(argVal);
				}
			} else {
				OpUtil.errorAndExit("Argument amount mismatch " + tname + " [Expected -> " + funcData.paramaters.size()
					+ " | Got -> " + call.argumentList.size() + " ]", call.position);
				return OpUtil.errorOccured();
			}

			environment.BeginFunctionBody();
			interpretShallowStatement(funcData.stat);
			environment.EndFunctionBody();

			environment.removeScope();
			environment.removeStackFrame();
			return returnData.deRefrence();
		} else {
			OpUtil.errorAndExit("Function Entry " + tname + " Doesnt Exist", call.position);
			return OpUtil.errorOccured();
		}
	}
	}

	private Value interpretDeepFunctionCall(FunctionCall call){
		if(!(call instanceof SystemFunctionCall)){
			String tname = call.functionName;

			if (environment.functionExists(tname)) {
				// Collect symbol table data from the function
				FunctionDeclaration funcData = environment.lookupFunction(tname);

				environment.addScope();
				environment.addStackFrame(tname);
				List<String> paramaterNames = new LinkedList<String>();
				for(ModuleItem Parameter : funcData.paramaters){
					String parameterName = OpUtil.getParamaterName(Parameter);
					paramaterNames.add(parameterName);
				} // declare the return variable for the function

				Pointer<Value> returnData = environment.lookupVariable(tname); // get return object

				if (call.argumentList.size() == funcData.paramaters.size() && call.argumentList.size() == paramaterNames.size()) {
					for(int i = 0; i < call.argumentList.size(); i++){
						String paramaterName = paramaterNames.get(i);
						Pointer<Value> paramValue = environment.lookupVariable(paramaterName);
						Expression paramExp = call.argumentList.get(i);
						Value argVal = interpretDeepExpression(paramExp);
						paramValue.assign(argVal);
					}
				} else {
					OpUtil.errorAndExit("Argument amount mismatch " + tname + " [Expected -> " + funcData.paramaters.size()
						+ " | Got -> " + call.toString() + " ]", call.position);
					return OpUtil.errorOccured();
				}

				environment.BeginFunctionBody();
				interpretDeepStatement(funcData.stat);
				environment.EndFunctionBody();

				environment.removeScope();
				environment.removeStackFrame();
				return returnData.deRefrence();
			} else {
				OpUtil.errorAndExit("Function Entry " + tname + " Doesnt Exist", call.position);
				return OpUtil.errorOccured();
			}
		} else {
			OpUtil.errorAndExit("Error System Functions can not be utilized in this context");
			return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	private Value interpretSystemFunctionCall(SystemFunctionCall call){
		String functionName = call.functionName;

		if (functionName.equals("fopen")) {
			StrVal fname = (StrVal)interpretShallowExpression(call.argumentList.get(0));
			String basePath = OpUtil.GetRuntimeDir();
			StrVal access = (StrVal)interpretShallowExpression(call.argumentList.get(1));
			String fullPath = basePath + '/' + fname;

			if (access.toString().equals("r")) {
				int fileDescriptor = environment.createReadOnlyFileDescriptor(fullPath);
				return new IntVal(fileDescriptor);
			} else if (access.equals("w")) {
				int fileDescriptor = environment.createWritableFileDescriptor(fullPath);
				return new IntVal(fileDescriptor);
			} else {
				OpUtil.errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.position);
			} 
		} else if (functionName.equals("feof")) {
			Value fileDescriptor = interpretShallowExpression(call.argumentList.get(0));
			FileReader reader = environment.getFileReader(fileDescriptor.intValue());

			try{
				return new BoolVal(reader.ready());
			} catch(Exception exp){
				return OpUtil.errorOccured();
			}
		} else if (functionName.equals("fscanf")) {
			Value fileDescriptor = interpretShallowExpression(call.argumentList.get(0));
			Value fString = interpretShallowExpression(call.argumentList.get(1));
			Value location = interpretShallowExpression(call.argumentList.get(2));
			
			FileReader fReader = environment.getFileReader(fileDescriptor.intValue());
			Scanner fScanner = new Scanner(fReader);

			String Data = fScanner.findInLine(fString.toString());
			StrVal DataVal = new StrVal(Data);
			
			return OpUtil.errorOccured(); // allways true just for consistency with verilog
		} else if (functionName.equals("getMemory")) {
			Value arg1 = interpretShallowExpression(call.argumentList.get(0));
			return OpUtil.errorOccured();
		} else if (functionName.equals("getRegister")) {
			Value arg1 = interpretShallowExpression(call.argumentList.get(0));
			return OpUtil.errorOccured();
		} else if (functionName.equals("getStatus")) {
			Value arg1 = interpretShallowExpression(call.argumentList.get(0));
			return OpUtil.errorOccured();
		} else {
			OpUtil.errorAndExit("Could not find a systemcall with the name " + functionName, call.position);
		}

		return OpUtil.errorOccured();
	}

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 */

	private Value interpretShallowIdentifier(Identifier ident){
		if (environment.functionExists(ident.labelIdentifier)) {
			Pointer<Value> data = environment.lookupVariable(ident.labelIdentifier);
			return data.deRefrence();
		} else {
			OpUtil.errorAndExit("Variable Entry " + ident.labelIdentifier + " Doesnt Exist", ident.position);
			return OpUtil.errorOccured();
		}
	}

	private Value interpretDeepIdentifier(Identifier ident){
		if (environment.functionExists(ident.labelIdentifier)) {
			Pointer<Value> data = environment.lookupVariable(ident.labelIdentifier);
			return data.deRefrence();
		} else {
			OpUtil.errorAndExit("Variable Entry " + ident.labelIdentifier + " Doesnt Exist", ident.position);
			return OpUtil.errorOccured();
		}
	}


  /**
	* This is the code for visiting a port connection in verilog
	* 
	* @param connection
	*/

	private Value interpretDeepPortConnection(PortConnection connection){
		Expression exp = connection.connectingFrom;
		String connectTo = connection.connectingTo;

		Value expVal = interpretDeepExpression(exp);

		if(environment.variableExists(connectTo)){
			Pointer<Value> Ptr = environment.lookupVariable(connectTo);
			Ptr.assign(expVal);
		} else {
			OpUtil.errorAndExit("Variable doesnt exist and was assinged to in port expression");
			return OpUtil.errorOccured();
		}

		return null;
	}

	/**
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	private Value interpretStr(StringNode string){
		return new StrVal(string.lexeme);
	}

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	private Value interpretShallowTernaryOperation(TernaryOperation expr){
		if (interpretShallowExpression(expr.condition).boolValue()) {
			return interpretShallowExpression(expr.ifTrue);
		} else {
			return interpretShallowExpression(expr.ifFalse);
		}
	}

	private Value interpretDeepTernaryOperation(TernaryOperation expr){
		Expression Condition = expr.condition;
		Expression IfTrue = expr.ifTrue;
		Expression IfFalse = expr.ifFalse;

		Value CondValue = interpretDeepExpression(Condition);
		Value trueVal = interpretDeepExpression(IfTrue);
		Value falseVal = interpretDeepExpression(IfFalse);

		if(CondValue.isWire() && trueVal.isWire() && falseVal.isWire()){
			WireVal condWire = (WireVal)CondValue;
			WireVal trueWire = (WireVal)trueVal;
			WireVal falseWire = (WireVal)falseVal;
			
			WireVal OutputWire = new WireVal();
			new Multiplexer(OutputWire, trueWire, falseWire, condWire);

			return OutputWire;
		} else if(CondValue.isWire() && trueVal.isVector() && falseVal.isVector()){
			WireVal condWire = (WireVal)CondValue;
			VectorVal trueVector = (VectorVal)trueVal;
			VectorVal falseVector = (VectorVal)falseVal;

			if(trueVector.getSize() == falseVector.getSize()){
				int startTrue = trueVector.getStart();
				int startFalse = falseVector.getStart();

				int endTrue = trueVector.getEnd();
				int endFalse = falseVector.getEnd();

				VectorVal OutputVector = new VectorVal(0, trueVector.getSize() - 1);
				int startOutput = 0;
				int  endOutput = trueVector.getEnd();

				while(startTrue != endTrue){
					WireVal trueWire = new WireVal();
					trueWire.assignInput(trueVector.getValue(startTrue));

					WireVal falseWire = new WireVal();
					falseWire.assignInput(falseVector.getValue(startFalse));

					WireVal outWire = new WireVal();
					falseWire.addOutput(OutputVector.getValue(startOutput));

					new Multiplexer(outWire, trueWire, falseWire, condWire);

					startTrue++;
					endTrue++;
					startOutput++;
				}

				return OutputVector;
			} else {
				OpUtil.errorAndExit("Cant place vectors into Multiplexer because they are of different Sizes");
				return OpUtil.errorOccured();
			}
		} else {
			OpUtil.errorAndExit("Invalid Operand types for Deep Ternary Operator");
			return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting a VectorVal in verilog
	 * 
	 * @param string
	 */

	private Value interpretShallowElement(Element Elem){
		String ident = Elem.labelIdentifier;
		Value expr = interpretShallowExpression(Elem.index1);

		if (environment.variableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof ArrayVal && dataObject.isVector()) {
				ArrayVal<VectorVal> arr = (ArrayVal<VectorVal>)dataObject;
				VectorVal vec = arr.ElemAtIndex(expr.intValue());
				return vec;
			} else if (dataObject instanceof VectorVal) {
				return ((VectorVal)dataObject).getValue(expr.intValue());
			} else if (dataObject instanceof ArrayVal && dataObject.isIntValue()) {
				return ((ArrayVal<IntVal>)dataObject).ElemAtIndex(expr.intValue());
			} else {
				OpUtil.errorAndExit("Unkown array type for " + ident + " [ Type -> " + dataObject.getClass() + " ]",
					Elem.position);
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or VectorVal " + ident + " not found", Elem.position);
			return OpUtil.errorOccured();
		}
	}

	private Value interpretShallowSlice(Slice vector){
		String ident = vector.labelIdentifier;
		Value startIndex = interpretShallowExpression(vector.index1);
		Value endIndex = interpretShallowExpression(vector.index2);

		if (environment.localVariableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof VectorVal) {
				VectorVal toRet = ((VectorVal)dataObject).getShallowSlice(startIndex.intValue(), endIndex.intValue());
				return toRet;
			} else {
				OpUtil.errorAndExit("Unkown array type for " + ident + " [ Type -> " + dataObject.getClass() + " ]");
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or VectorVal " + ident + " not found");
			return OpUtil.errorOccured();
		}
	}

	private Value interpretBinaryNode(BinaryNode Bin){
		return new UnsignedIntVal(Integer.parseUnsignedInt(Bin.lexeme));
	}

	private Value interpretHexadecimalNode(HexadecimalNode Hex){
		return new UnsignedIntVal(Integer.parseUnsignedInt(Hex.lexeme));
	}

	private Value interpretDecimalNode(DecimalNode Dec){
		return new UnsignedIntVal(Integer.parseUnsignedInt(Dec.lexeme));
	}

	private Value interpretOctalNode(OctalNode Oct){
		return new UnsignedIntVal(Integer.parseUnsignedInt(Oct.lexeme));
	}

	private Value interpretStringNode(StringNode Str){
		return new StrVal(Str.toString());
	}
}
