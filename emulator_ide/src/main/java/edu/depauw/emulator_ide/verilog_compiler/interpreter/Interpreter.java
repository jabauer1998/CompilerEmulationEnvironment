package edu.depauw.emulator_ide.verilog_compiler.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.lang.model.util.ElementScanner14;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.gui.Main;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.AndGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.Web;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.LValue;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.IdentDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Input;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Unidentified.Declaration;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.TaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor.Preprocessor;
import edu.depauw.emulator_ide.verilog_compiler.util.OpUtil;

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
     * Brlow are the public methods that an interpreter can call
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
        return interpretExpression(Exp);
    }

    public IntVal interpretStatement(String Statement){
        Source source = new Source(new StringReader(Statement));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        Statement Stat = parse.parseStatement();
        interpretStatement(Stat);

        if(errorLog.size() > 0){
            errorLog.printLog();
            return new IntVal(-1);
        } else {
            return new IntVal(0);
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
            return new IntVal(-1);
        } else {
            return new IntVal(0);
        }
    }

    public IntVal interpretFile(String FileName){
        try{
			FileReader Reader = new FileReader(FileName);
			return interpretFile(Reader);
		} catch(Exception exc) {
			errorLog.addItem(new ErrorItem("Could not make file stream exception thrown" + exc.toString()));
		}

		return new IntVal(-1);
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

		return new IntVal(0);
	}

	private IntVal interpretModule(ModuleDeclaration mod){
		String modName = mod.moduleName;

		if (environment.moduleExists(modName)) {
			OpUtil.errorAndExit("Redeclaration of Module " + modName + "found at " + '[' + mod.position + "] declared !!!", env.lookupModule(modName));
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
		else if (Item instanceof ModuleInstance) return interpretModuleInstance((ModuleInstance)Item);
		else if (Item instanceof ModuleInstantiation) return interpretModuleInstantiation((ModuleInstantiation)Item);
		else if (Item instanceof ProcedureDeclaration) return interepretProcedureDeclaration((ProcedureDeclaration)Item));
		else if (Item instanceof Process) return interpretProcess((Process)Item);
		else if (Item instanceof IdentDeclaration) return interpretIdentDeclaration((IdentDeclaration)Item);
		else if (Item instanceof ContinuousAssignment) return interpretContinuousAssignment((ContinuousAssignment)Item);
		else if (Item instanceof EmptyModItem) return interpretEmptyModItem((EmptyModItem)Item);
		else {
			OpUtil.errorAndExit("Error: Invalid Module Item Type ~ " + Item.getClass().getName());
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
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

	   Web outputResult = (Web)interpretExpression(output);
	   Web input1Result = (Web)interpretExpression(input1);
	   Web input2Result = (Web)interpretExpression(input2);

	   Web[] inputsResultsRest = new Web[inputsRest.size()];

	   for(int i = 0; i < inputsResultsRest.length; i++){
		 Expression InputRest = inputsRest.remove(0);
		 inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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

	   Web outputResult = (Web)interpretExpression(output);
	   Web input1Result = (Web)interpretExpression(input1);
	   Web input2Result = (Web)interpretExpression(input2);

	   Web[] inputsResultsRest = new Web[inputsRest.size()];

	   for(int i = 0; i < inputsResultsRest.length; i++){
		 Expression InputRest = inputsRest.remove(0);
		 inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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
 
		Web outputResult = (Web)interpretExpression(output);
		Web input1Result = (Web)interpretExpression(input1);
		Web input2Result = (Web)interpretExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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
 
		Web outputResult = (Web)interpretExpression(output);
		Web input1Result = (Web)interpretExpression(input1);
		Web input2Result = (Web)interpretExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  	Expression InputRest = inputsRest.remove(0);
		  	inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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
 
		Web outputResult = (Web)interpretExpression(output);
		Web input1Result = (Web)interpretExpression(input1);
		Web input2Result = (Web)interpretExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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
 
		Web outputResult = (Web)interpretExpression(output);
		Web input1Result = (Web)interpretExpression(input1);
		Web input2Result = (Web)interpretExpression(input2);
 
		Web[] inputsResultsRest = new Web[inputsRest.size()];
 
		for(int i = 0; i < inputsResultsRest.length; i++){
		  Expression InputRest = inputsRest.remove(0);
		  inputsResultsRest[i] = (Web)interpretExpression(InputRest);
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
		Web output = (Web)interpretExpression(outputConnection);
		Web input = (Web)interpretExpression(inputConnection);

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


		return OpUtil.success();
	 }

	 /**
	 * This is used to visit a task declaration in verilog
	 * 
	 * @param task
	 */

	public IntVal interpretTaskDeclaration(TaskDeclaration task){
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

		String functionName = fetchFunctionName(function.functionName);
		// May need to finish this later

		if(environment.functionExists(functionName)){
			OpUtil.errorAndExit("Error: no function with the name of " + functionName + " was found");
			return OpUtil.errorOccured();
		} else {

			funcEnv.addEntry(functionName, new InterpreterFunctionData(function, function.getPosition()));
			return OpUtil.success();
		}
	}

	public IntVal interpretProcess(Process process){
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

	public IntVal interpretAllwaysProcess(AllwaysProcess process){
		while(true){
			interpretStatement(process.statement);
		}

		return OpUtil.success();
	}

	/**
	 * Now we will go through and visit all of the ModuleItem classes
	 */

	public IntVal interpretInitialProcess(InitialProcess process){
		return interpretStatement(process);
	}

	/**
	 * This is the code to visit a Continuous Assignment in Verilog.
	 * 
	 * @param assign
	 */

	public IntVal interpretContinuousAssignment(ContinuousAssignment assign){
		for (BlockingAssignment amnt : assign.assignmentList){
			interpretDeepAssignment(amnt);
		}
	}

	/**
	 * This is used to visit a empty mod item in verilog
	 * 
	 * @param task
	 */

	public IntVal interpretEmptyModItem(EmptyModItem macro){
		return null; // this class is just for completeness
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. Ex. input a, b, c ...
	 * ;
	 * 
	 * @param decl
	 */

	public IntVal visit(Input.Wire.Vector.Ident decl){
		Expression exp1 = decl.vectorIndex1;
		Expression exp2 = decl.vectorIndex2;

		if (!environment.variableExists(decl.declarationIdentifier)) {
			varEnv.addEntry(decl.declarationIdentifier, new InterpreterVariableData(new Wire(), decl.position));
		} else {
			OpUtil.errorAndExit("Error identifier allready exists...");
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. Ex. input a, b, c ...
	 * ;
	 * 
	 * @param decl
	 */

	public Void visit(InputRegScalarDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (!varEnv.inScope(current.getLexeme())) {
				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Register(false), current.getPosition()));
			}

			InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

			if (inFunctionParam) {
				funcEnv.getEntry(getcallStackName()).addParameter(entryData); // add paramter to function
			}

		}

		return null;
	}

	/**
	 * This is used to visit any input vector declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(InputWireVectorDeclaration decl, Object... argv){

		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.getObject() == null) {
					Vector<CircuitElem> vec = new Vector<>(index1, index2);

					if (index1 <= index2) {

						for (int x = index1; x <= index2; x++) { vec.setValue(x, new Wire()); }

					} else {

						for (int x = index2; x <= index1; x++) { vec.setValue(x, new Wire()); }

					}

					entryData.setObject(vec);
				} else {
					errorLog.addItem(
						new ErrorItem("Variable named " + current.getLexeme() + "allready exists and cannot be converted to "
							+ entryData.getObject().getClass(), current.getPosition()));
				}

			} else {
				Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);

				if (index1 <= index2) {

					for (int x = index1; x <= index2; x++) { vec.setValue(x, new Wire()); }

				} else {

					for (int x = index2; x <= index1; x++) { vec.setValue(x, new Wire()); }

				}

				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
			}

			InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

			if (inFunctionParam) {
				funcEnv.getEntry(getcallStackName()).addParameter(entryData); // add paramter to function
			}

		}

		return null;
	}

	/**
	 * This is used to visit any input vector declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(InputRegVectorDeclaration decl, Object... argv){
		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.getObject() == null) {
					entryData.setObject(new Vector<CircuitElem>(index1, index2));
				} else {
					errorLog.addItem(new ErrorItem(
						"Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector",
						current.getPosition()));
				}

			} else {
				Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);

				if (index1 <= index2) {

					for (int x = index1; x <= index2; x++) { vec.setValue(x, new Register(false)); }

				} else {

					for (int x = index2; x <= index1; x++) { vec.setValue(x, new Register(false)); }

				}

				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
			}

			InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

			if (inFunctionParam) {
				funcEnv.getEntry(getcallStackName()).addParameter(entryData); // add paramter to function
			}

		}

		return null;
	}

	/**
	 * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(WireScalarDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
			} else {
				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Wire(), current.getPosition()));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any wire vector declaration in verilog. Ex. wire [31:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(WireVectorDeclaration decl, Object... argv){
		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.getObject() == null) {
					entryData.setObject(new Vector(index1, index2));
				} else {
					errorLog.addItem(new ErrorItem(
						"Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector",
						current.getPosition()));
				}

			} else {
				Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);

				if (index1 <= index2) {

					for (int x = index1; x != index2; x++) { vec.setValue(x, new Wire()); }

				} else {

					for (int x = index1; x != index2; x--) { vec.setValue(x, new Wire()); }

				}

				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(RegScalarDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this); }

		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(RegVectorDeclaration decl, Object... argv){

		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this, index1, index2); }

		return null;
	}

	/**
	 * This is used to visit any output scalar declaration in Verilog. Ex. output a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(OutputWireScalarDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.entryExists(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
			} else {
				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Wire(), current.getPosition()));
			}

		}

		return null;
	}

	/**
	 * This is where I will declare the output Register Scalar declaration
	 * 
	 * @param Jacob Bauer
	 */

	public Void visit(OutputRegScalarDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this); }

		return null;
	}

	public Void visit(OutputWireVectorDeclaration decl, Object... argv){

		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.getObject() == null) {
					entryData.setObject(new Vector(index1, index2));
				} else {
					errorAndExit("Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector",
						current.getPosition());
				}

			} else {
				Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);

				if (index1 <= index2) {

					for (int x = index1; x != index2; x++) { vec.setValue(x, new Wire()); }

				} else {

					for (int x = index1; x != index2; x--) { vec.setValue(x, new Wire()); }

				}

				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(OutputRegVectorDeclaration decl, Object... argv){

		int index1 = (int)longValue(decl.getExpression1().accept(this)); // check whether the expressions return ints
		int index2 = (int)longValue(decl.getExpression2().accept(this));

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this, index1, index2); }

		return null;
	}

	/**
	 * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(IntegerDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this); }

		return null;
	}

	/**
	 * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(RealDeclaration decl, Object... argv){

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.inScope(current.getLexeme())) {
				InterpreterVariableData dataType = varEnv.getEntry(current.getLexeme());
				errorLog.addItem(new ErrorItem(
					"Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ",
					current.getPosition()));
			} else {
				varEnv.addEntry(current.getLexeme(), new InterpreterVariableData((double)0, current.getPosition()));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any Unidentified declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(UnidentifiedDeclaration decl, Object... argv){
		Identifier current = decl.getIdentifier();

		if (varEnv.inScope(current.getLexeme())) {
			InterpreterVariableData dataType = varEnv.getEntry(current.getLexeme());
			errorLog.addItem(new ErrorItem(
				"Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ",
				current.getPosition()));
		} else {
			varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(null, current.getPosition()));
		}

		return null;
	}
	
	/**
	 * Below we will place the code for assignment types in verilog
	 * @param assign
	 * @param argv
	 * @return
	 */

	public IntVal interpretStatement(Statement Stat){
		if(Stat instanceof CaseStatement) return interpretCaseStatement((CaseStatement)Stat);
		else if (Stat instanceof Assignment) return interpretAssignment((Assignment)Stat);
		else if (Stat instanceof IfStatement) return interpretIfStatement((IfStatement)Stat);
		else if (Stat instanceof ForeverStatement) return interpretForeverLoop((ForeverStatement)Stat);
		else if (Stat instanceof ForStatement) return interpretForLoop((ForStatement)Stat);
		else if (Stat instanceof RepeatStatement) return interpretRepeatLoop((RepeatStatement)Stat);
		else if (Stat instanceof WhileStatement)  return interpretWhileLoop((WhileStatement)Stat);
		else if (Stat instanceof TaskStatement) return interpretTask((TaskStatement)Stat);
		else if (Stat instanceof SeqBlockStatement) return interpretStatementBlock((SeqBlockStatement)Stat);
		else {
			OpUtil.errorAndExit("Error: Invalid Statement Node Found");
			return OpUtil.errorOccured();
		}
	}

	

	public IntVal interpretAssignment(Assignment assign){
		if(assign instanceof BlockingAssignment) return interpretBlockingAssignment((BlockingAssignment)assign);
		else if (assign instanceof NonBlockingAssignment) return interpretNonBlockingAssingnment((NonBlockingAssignment)assign);
		else {
			OpUtil.errorAndExit("Invalid Assingment type found at position " + assign.position);
			return OpUtil.errorOccured();
		}
	}
	
	public String interpretLValue(LValue leftHandSide) {
		if (lValue instanceof Vector) return ((VectorElement)leftHandSide);
		else if (lValue instanceof Identifier) return ((Identifier)leftHandSide);
	  else if (lValue instanceof VectorSlice) return ((VectorSlice)lValue);
		} else {
			OpUtil.errorAndExit("Unexpected Type for LValue");
			return null;
		}
	}
	
	public IntVal interpretBlockingAssingment(BlockingAssignment assign) {
		 Pointer<Value> Ptr = interpretLValue(assign.leftHandSide);
		 Expression exp = assign.rightHandSide;
		 
		 Value val = interpretExpression(exp);
		 OpUtil.shallowAssign(Ptr, Val);

		 return OpUtil.success();
	}

	public IntVal interpretNonBlockingAssignment(NonBlockingAssignment assign){
		List<LValue> leftSide = assign.leftHandSide;
		List<Expression> expressions = assign.rightHandSide;

		List<Value> Results = new LinkedList<Value>();
		List<Pointer<Value>> lValues = new LinkedList<Pointer<Value>>();
		//First go through and interpret All of the Expressions
		for(Expression expr : expressions){
			Value answer = interpretExpression(expr);
			Results.add(answer);
		}

		for(LValue Value : leftSide){
			Pointer<LValue> lValue = interpretLValue(Value);
			lValues.add(lValue);
		}
		
		for(int i = 0; i < Results.length; i++){
			Value rightHandSide = Results.remove(0);
			Pointer<Value> leftHandSide = Results.remove(0);
			OpUtil.shallowAssign(leftHandSide, rightHandSide);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit case statements in verilog
	 * 
	 * @param assign
	 */

	public Void interpretCaseStatement(CaseStatement stat){
		if(stat instanceof CaseXStatement) return interpretCaseXStatement((CaseXStatement) stat);
		else if (stat instanceof CaseZStatement) return interpretCaseZStatement((CaseZStatement) stat);
		else {
			Value switchExpVal = interpretExpression(stat.exp);

			for (CaseItem item : stat.itemList){
				if (item instanceof ExprCaseItem) {
					ExprCaseItem exprItem = (ExprCaseItem)item;

					for (Expression CaseExp : exprItem.expList) {
						Object exprValue = interpretExpression(Exp);

						if (OpUtil.caseBoolean(switchExpVal, exprValue)) {
							interpretStatement(exprItem.statement);
							break loop;
						}
					}
				} else {
					DefCaseItem exprItem = (DefCaseItem)item;
					interpretStatement(exprItem.statement);
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

	public IntVal interpretCaseXStatement(CaseXStatement stat){
		Value switchExp = interpretExpression(stat.exp);

		for (CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (Expression CaseExp : exprItem.expList) {
					Value exprValue = interpretExpression(Exp);

					if (OpUtil.caseBoolean(switchExp, exprValue)) {
						interpretStatement(exprItem.statement);
						break loop;
					}
				}
			} else {
				DefCaseItem exprItem = (DefCaseItem)item;
				interpretStatement(exprItem.statement);
				break;
			}
		}

		return OpUtil.success();
	}

	public Void interpretCaseZStatement(CaseZStatement stat){
		Value switchExp = interpretExpression(stat.exp);

		for (CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (Expression CaseExp : exprItem.expList) {
					Value exprValue = interpretExpression(Exp);

					if (OpUtil.caseBoolean(switchExp, exprValue)) {
						interpretStatement(exprItem.statement);
						break loop;
					}
				}
			} else {
				DefCaseItem exprItem = (DefCaseItem)item;
				interpretStatement(exprItem.statement);
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

	public IntVal interpretForLoop(ForStatement forLoop){
		for (interpretBlockingAssingment(forLoop.init); interpretExpression(forLoop.exp); interpretStatement(forLoop.change)) {
			interpretStatement(forLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a forever loop in verilog
	 * 
	 * @param foreverLoop
	 */

	public IntVal interpretForeverLoop(ForeverStatement foreverLoop){
		boolean tf = true;

		while(tf) {
			interpretStatement(foreverLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public IntVal interpretIfElseStatement(IfElseStatement ifElseStatement){
		Value expr = interpretExpression(ifElseStatement.condition);
		if (expr.boolValue()) {
			interpretStatement(ifElseStatement.trueStatement);
		} else {
		    interpretStatement(ifElseStatement.falseStatement);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public IntVal interpretIfStatement(IfStatement ifStatement){
		if(ifStatement instanceof IfElseStatement) return interpretIfElseStatement((IfElseStatement)ifStatement);
		else {
			Value expr = interpretExpression(ifStatement.condition);

			if (expr.boolValue()) { 
				interpretStatement(ifStatement.trueStatement); 
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a repeat statement in verilog
	 * 
	 * @param stat
	 */

	public IntVal visit(RepeatStatement stat){
		Value expr = interpretExpression(stat.exp);

		if (expr.islongValue() || expr.isVector()) {
			long amount = expr.longValue();

			for (long i = 0; i < amount; i++) {
				interpretStatement(stat.stat);
			}
		} else if (expr.isWire() || expr.isRegister() || expr.isBoolValue()) {
			if (expr.boolValue()) {
				interpretStatement(stat.stat);
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

	public IntVal interpretBlockOfStatements(SeqBlockStatement stat){

		for (Statement stmt : stat.statementList) {
			interpretStatement(stat);
		}

		return null;
	}

	/**
	 * This is used to visit a taskcall in verilog
	 * 
	 * @param stat
	 */

	public IntVal InterpretTaskCall(TaskStatement task){
		String tname = task.taskName;

		if (environment.taskExists(tname)) {
			// Collect symbol table data from the function
			TaskDeclaration funcData = environment.lookupTask(tname);
			environment.addStackFrame(tname);
			environment.BeginParamaterDeclarations();

			List<String> parmaterNames = new LinkedList<String>();

			for(ModuleItem Decl : funcData.paramaters){
				String paramaterName = interpretParamater(Decl);
				paramaterNames.add(paramaterName);
			}

			environment.EndParamaterDeclarations();
			environment.removeStackFrame();

			if (task.argumentList.size() == funcData.paramaters.size() && task.argumentList.size() == parmaterNames.size()) {
				// Assign parameter value
				for(int i = 0; i < parmaterNames.size(); i++){
					String paramaterName = paramaterNames.get(i);
					Pointer<Value> paramaterValue = environment.lookupVariable(paramaterName);
					Expression argExpr = task.argumentList.get(i);
					Value argValue = interpretExpression(argExpr);
					paramaterValue.assign(argValue);
				}
			} else {
				OpUtil.errorAndExit("Argument amount mismatch " + tname.getLexeme() + " [Expected -> " + funcData.numParameters()
					+ " | Got -> " + task.numExpressions() + " ]");
				return OpUtil.errorOccured();
			}

			interpretStatement(funcData.stat);
		} else {
			OpUtil.errorAndExit("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition());
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a system task statement in verilog
	 * 
	 * @param stat
	 */

	public IntVal visit(SystemTaskStatement task){
		Identifier taskName = task.getSystemTaskName();

		if (taskName.getLexeme().equals("fclose")) {
			Scanner fReader = (Scanner)task.getExpression(0).accept(this);
			fReader.close();
		} else if (taskName.getLexeme().equals("display")) {

			if (task.numExpressions() == 2) {
				String fString = (String)task.getExpression(0).accept(this);
				Object data = task.getExpression(1).accept(this);
				Main.getByteOutputStream().writeBytes((fString + " " + data + "\n").getBytes()); // write to standard output in
																									// the gui
			} else if (task.numExpressions() == 1) {
				Object data = task.getExpression(0).accept(this);
				Main.getByteOutputStream().writeBytes(("\t" + data.toString() + '\n').getBytes()); // write to standard output in
																									// the gui
			} else {
				errorAndExit("Unknown number of print arguments in " + taskName.getLexeme(), taskName.getPosition());
			}

		} else if (taskName.getLexeme().equals("finish")) {
			System.out.println("Program is finished!!!");
		} else if (taskName.getLexeme().equals("setMemory")) {
			long arg1 = longValue(task.getExpression(0).accept(this));
			long arg2 = longValue(task.getExpression(1).accept(this));
			GuiMemory.setMemory(arg1, arg2);
		} else if (taskName.getLexeme().equals("setRegister")) {
			long arg1 = longValue(task.getExpression(0).accept(this));
			long arg2 = longValue(task.getExpression(1).accept(this));
			GuiRegister.setRegister(arg1, arg2);
		} else if (taskName.getLexeme().equals("setStatus")) {
			String arg1 = (String)task.getExpression(0).accept(this);
			long arg2 = longValue(task.getExpression(1).accept(this));
			GuiStatusBit.setStatus(arg1, arg2);
		} else {
			errorAndExit("Unknown system task declaration " + taskName.getLexeme(), taskName.getPosition());
		}

		return null;
	}

	public IntVal interpretWaitStatement(WaitStatement wait){
		Expression expr = wait.exp;
		while(interpretExpression(expr).boolValue());
		return interpretStatement(wait.stat);
	}

	/**
	 * This is used to visit a while loop in verilog
	 * 
	 * @param whileLoop
	 */

	public IntVal interpretWhileLoop(WhileStatement whileLoop){

		while(interpretExpression(whileLoop.exp).boolValue()) {
			interpretStatement(whileLoop.stat);

			if (inFunctionReturn) { if (getTopExit()) { return null; } }

		}

		return OpUtil.success();
	}

	/**
	 * This is the code for visiting empty statements this is here just for completion
	 * 
	 * @param none
	 */

	public IntVal visit(EmptyStatement stat){
		// this is empty it is just a placeholder
		return OpUtil.success();
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value interpretExpression(Expression exp){
		if(exp instanceof BinaryOperation) return interpretBinaryOperation((BinaryOperation)exp);
		else if (exp instanceof UnaryOperation) interpretUnaryOperation((UnaryOperation)exp);
		else if (exp instanceof Concatenation) interpretConcatenation((Concatenation)exp);
		else if (exp instanceof FunctionCall) interpretFunctionCall((FunctionCall)exp);
		else if (exp instanceof TernaryOperation) interpretTernaryOperation((TernaryOperation)exp);
		else if (exp instanceof BinaryNode) interpretBinaryNode((BinaryNode)exp);
		else if (exp instanceof DecimalNode) interpretDecimalNode((DecimalNode)exp);
		else if (exp instanceof HaxadecimalNode) interpretHexadecimalNode((HexadecimalNode)exp);
		else if (exp instanceof OctalNode) interpretOctalNode((OctalNode)exp);
		else if (exp instanceof StringNode) interpretStringNode((StringNode)exp);
		else if (exp instanceof ConstantExpression) interpretConstantExpression((ConstantExpression)exp);
		else if (exp instanceof PortConnection) interpretPortConnection((PortConnection)exp);
		else {
			OpUtil.errorAndExit("Error: Could not find an expression of type");
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	public Value interpretBinaryOperation(BinaryOperation op){
		Value left = interpretExpression(op.left);
		Value right = interpretExpression(op.right);

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
			case LAND: OpUtil.logicalAnd(left, right);
			case LOR: OpUtil.logicalOr(left, right);
			case LE: OpUtil.lessThanOrEqualTo(left, right);
			case LT: OpUtil.lessThan(left, right);
			case GE: OpUtil.greaterThanOrEqualTo(left, right);
			case GT: OpUtil.greaterThan(left, right);
			case BAND: OpUtil.bitwiseAnd(left, right);
			case BOR: OpUtil.bitwiseOr(left, right);
			case BXOR: OpUtil.exclusiveOr(left, right);
			case BXNOR: OpUtil.exclusiveNor(left, right);
			case LSHIFT: OpUtil.leftShift(left, right);
			case RSHIFT: OpUtil.rightShift(left, right);
			default:
				OpUtil.errorAndExit("Invalid operation in verilog of type " + left.getClass() + " "
					+ op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}

		return OpUtil.success();

	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Value interpretUnaryOperation(UnaryOperation op){
		Value right = interpretExpression(op);

		switch(op.Op){
			case PLUS: return right;
			case MINUS: OpUtil.negation(right);
			case LNEG: OpUtil.logicalNegation(right);
			case BNEG: OpUtil.bitwiseNegation(right);
			default: OpUtil.errorAndExit("Unknown unary operation in verilog of type " + op.Op + " " + right.getClass());
				return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	public Value interpretConcatenation(Concatenation concat){
		int size = 0;

		for(Expression expr : concat.circuitElementExpressionList){
			Value Res = interpretExpression(expr);

			if (Res.isVector()) {
				size += ((Vector)Res).getSize();
			} else {
				size++; // it is a CircuitElem
			}

		}

		Vector  newVec = new Vector(size - 1, 0);
		int total = size - 1;

		for(Expression exp : concat.circuitElementExpressionList){
			Value valExp = interpretExpression(exp);

			if (valExp.isVector()) {
				Vector vec = (Vector)valExp;
				if (vec.getIndex1() <= vec.getIndex2()) {

					for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
						newVec.setValue(total, new Register(vec.getValue(v).getStateSignal()));
					}

				} else {

					for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
						newVec.setValue(total, new Register(vec.getValue(v).getStateSignal()));
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

	public Value interpretConstantExpression(ConstantExpression expr){ 
		return interpretExpression(expr.expression);
	}

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	public Value interpretEmptyExpression(EmptyExpression expr){ 
		return OpUtil.errorOccured(); 
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	boolean inFunctionReturn = false;

	public Value interpretFunctionCall(FunctionCall call){
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
				String parameterName = interpretParamater(Parameter);
				paramaterNames.add(parameterName);
			} // declare the return variable for the function

			Pointer<Value> returnData = varEnv.getEntry(tname); // get return object

			inFunctionParam = false; // Params are declared if any

			if (call.argumentList.size() == funcData.numParameters() && call.argumentList.size() == paramaterNames.size()) {
				for(int i = 0; i < call.argumentList.size(); i++){
					String paramaterName = paramaterNames.get(i);
					Pointer<Value> paramValue = environment.lookupVariable(paramaterName);
					Expression paramExp = argumentList.get(i);
					Value argVal = interpretExpression(argVal);
					paramValue.assign(argVal);
				}

			} else {
				OpUtil.errorAndExit("Argument amount mismatch " + tname.getLexeme() + " [Expected -> " + funcData.numParameters()
					+ " | Got -> " + call.numExpressions() + " ]", tname.getPosition());
				return OpUtil.errorOccured();
			}

			environment.BeginFunctionBody();
			interpretStatement(funcData.stat);
			environment.EndFunctionBody();

			varEnv.removeScope();
			environment.removeStackFrame();
			return returnData.deRefrence();
		} else {
			errorAndExit("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition());
			return OpUtil.errorOccured();
		}
	}
}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public Value visit(SystemFunctionCall call){
		String functionName = call.functionName;

		if (functionName.equals("fopen")) {
			StrVal fname = (StrVal)interpretExpression(call.argumentList.get(0));
			String basePath = OpUtil.GetRuntimeDir();
			StrVal access = (StrVal)interpretExpression(call.argumentList.get(1));
			File filename = new File(basePath + '/' + fname);

			if (access.equals("r")) {
				filename.setReadOnly();

				Scanner ref = null;

				try {
					ref = new Scanner(filename);
				} catch (FileNotFoundException exp) {
					exp.printStackTrace();
					System.exit(1);
				}

				return ref;
			} else if (access.equals("w")) {
				filename.setWritable(true, false);

				FileWriter ref = null;

				try {
					ref = new FileWriter(filename);
				} catch (IOException exp) {
					exp.printStackTrace();
					System.exit(1);
				}

				return ref;
			} else {
				OpUtil.errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.getPosition());
			} 
		} else if (functionName.getLexeme().equals("feof")) {
			Scanner fReader = (Scanner)call.getExpression(0).accept(this);
			boolean hasNextLine = fReader.hasNextLine();
			return !hasNextLine;
		} else if (functionName.getLexeme().equals("fscanf")) {
			Scanner fReader = (Scanner)call.getExpression(0).accept(this);
			String fString = (String)call.getExpression(1).accept(this);
			Vector<CircuitElem> location = (Vector<CircuitElem>)call.getExpression(2).accept(this);
			String data = fReader.nextLine();
			// data = String.format(fString, data);
			OpUtil.shallowAssign(location, data);
			return (long)0; // allways true just for consistency with verilog
		} else if (functionName.getLexeme().equals("getMemory")) {
			long arg1 = longValue(call.getExpression(0).accept(this));
			return GuiMemory.getMemory(arg1);
		} else if (functionName.getLexeme().equals("getRegister")) {
			long arg1 = longValue(call.getExpression(0).accept(this));
			return GuiRegister.getRegister(arg1);
		} else if (functionName.getLexeme().equals("getStatus")) {
			String arg1 = (String)call.getExpression(0).accept(this);
			return GuiStatusBit.getStatus(arg1);
		} else {
			OpUtil.errorAndExit("Could not find a systemcall with the name " + functionName.getLexeme(), call.getPosition());
		}

		return null;
	}

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 */

	public Value interpretIdentifier(Identifier ident){
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

	public Value interpretPortConnection(PortConnection connection){ 
		return null;
	}

	/**
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	public Value interpretStr(StringNode string){
		return new StrVal(string.lexeme);
	}

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	public Value interpretTernaryOperation(TernaryOperation expr){

		if (interpretExpression(expr.condition).boolValue()) {
			return interpretExpression(expr.ifTrue);
		} else {
			return interpretExpression(expr.ifFalse);
		}

	}

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public Value interpretElement(Element Elem){
		String ident = Elem.labelIdentifier;
		Value expr = interpretExpression(Elem.index1);

		if (environment.variableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof Vector[]) {
				Vector[] arr = (Vector[])dataObject;
				Vector vec = arr[index];
				return vec;
			} else if (dataObject instanceof Vector) {
				return ((Vector)dataObject).getValue(index);
			} else if (dataObject instanceof Long[]) {
				return ((Long[])dataObject)[index];
			} else {
				OpUtil.errorAndExit("Unkown array type for " + ident.getLexeme() + " [ Type -> " + dataObject.getClass() + " ]",
					ident.getPosition());
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or Vector " + ident.getLexeme() + " not found", ident.getPosition());
			return OpUtil.errorOccured();
		}
	}

	public Value interpretSlice(Slice vector, Object... argv){
		String ident = vector.labelIdentifier;
		Value startIndex = interpretExpression(vector.index1);
		Value endIndex = interpretExpression(vector.index2);

		if (environment.localVariableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof Vector) {
				Vector toRet = ((Vector)dataObject).getShallowSlice(startIndex.intValue(), endIndex.intValue());
				return toRet;
			} else {
				OpUtil.errorAndExit("Unkown array type for " + ident + " [ Type -> " + dataObject.getClass() + " ]");
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or Vector " + ident + " not found");
			return OpUtil.errorOccured();
		}
	}

	/*
	 * Below are RegValue visitors. These are used in the declarations of Integers and
	 * Registers
	 */

	public Void visit(RegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();
		int start = (int)argv[0];
		int end = (int)argv[1];

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Vector vec = new Vector(start, end);

			if (start <= end) {

				for (int i = start; i <= end; i++) { vec.setValue(i, new Register(false)); }

			} else {

				for (int i = end; i <= start; i++) { vec.setValue(i, new Register(false)); }

			}

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(vec);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(RegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Register reg = new Register(false);

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(reg);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(reg, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(OutputRegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();
		int start = (int)argv[0];
		int end = (int)argv[1];

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Vector vec = new Vector(start, end);

			if (start <= end) {

				for (int i = start; i <= end; i++) { vec.setValue(i, new Register(false)); }

			} else {

				for (int i = end; i <= start; i++) { vec.setValue(i, new Register(false)); }

			}

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(vec);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(OutputRegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Register reg = new Register(false);

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(reg);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(reg, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(IntegerIdent intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject((long)0);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData((long)0, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(RegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		int index1 = (int)argv[0];
		int index2 = (int)argv[1];

		int aIndex1 = (int)(long)regVector.getExpression1().accept(this);
		int aIndex2 = (int)(long)regVector.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Vector<CircuitElem>[] arr = new Vector[aSize];

		for (int i = 0; i < aSize; i++) {
			arr[i] = new Vector<CircuitElem>(index1, index2);

			if (index1 <= index2) {

				for (int x = index1; x <= index2; x++) { arr[i].setValue(x, new Register(false)); }

			} else {

				for (int x = index2; x <= index1; x++) { arr[i].setValue(x, new Register(false)); }

			}

		}

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(RegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		int aIndex1 = (int)(long)regScalar.getExpression1().accept(this);
		int aIndex2 = (int)(long)regScalar.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		CircuitElem[] arr = new CircuitElem[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = new Register(false); }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		int index1 = (int)argv[0];
		int index2 = (int)argv[1];

		int aIndex1 = (int)(long)regVector.getExpression1().accept(this);
		int aIndex2 = (int)(long)regVector.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Vector<CircuitElem>[] arr = new Vector[aSize];

		for (int i = 0; i < aSize; i++) {
			arr[i] = new Vector<CircuitElem>(index1, index2);

			if (index1 <= index2) {

				for (int x = index1; x <= index2; x++) { arr[i].setValue(x, new Register(false)); }

			} else {

				for (int x = index2; x <= index1; x++) { arr[i].setValue(x, new Register(false)); }

			}

		}

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		int aIndex1 = (int)(long)regScalar.getExpression1().accept(this);
		int aIndex2 = (int)(long)regScalar.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		CircuitElem[] arr = new CircuitElem[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = new Register(false); }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(IntegerArray intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		int aIndex1 = (int)(long)intIdent.getExpression1().accept(this);
		int aIndex2 = (int)(long)intIdent.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Long[] arr = new Long[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = (long)0; }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
    }
}
