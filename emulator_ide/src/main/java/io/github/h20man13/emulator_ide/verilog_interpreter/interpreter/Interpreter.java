package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ArrayVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.RealVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.StrVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry.Multiplexer;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.AndGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NandGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NorGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NotGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.OrGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XnorGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XorGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.Web;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern.BinaryPattern;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern.HexadecimalPattern;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern.OctalPattern;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.VerilogFile;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.ConstantExpression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.EmptyExpression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.PortConnection;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.FunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.BinaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.Concatenation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.TernaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.UnaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.BinaryNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.DecimalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.HexadecimalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.OctalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.StringNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ContinuousAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.EmptyModItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.AndGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.GateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NandGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NotGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.OrGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.XnorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.XorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstance;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstantiation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.ProcedureDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process.AllwaysProcess;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process.InitialProcess;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process.ProcessBase;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.ArrayDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.IdentDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Int;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Output;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Real;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Unidentified;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Wire;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.EmptyStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.SeqBlockStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.WaitStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseXStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseZStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.CaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.DefCaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.ExprCaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.Assignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.NonBlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForeverStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.RepeatStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.WhileStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfElseStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.TaskStatement;

/**
 * The Follwowing Class can be utilized to Interpret Verilog Modules
 * It will Inturn call the Interpret Methods on the AstNodes after they are Parsed
 * 
 * The Interpreter used to be a visitor but It was in aggreement that an Interpeter with the Interpreter Design Pattern will perform
 * better.
 */

public abstract class Interpreter {

    /**
     * Below are the Fields of the Interpreter
     * Each Interpreter contains an Environment which conntains all the run tume information that the
     * program has access too and an ErrorLog
     */
    protected ErrorLog errorLog;
    protected Environment environment;

    protected Interpreter(ErrorLog errorLog){
        this.errorLog = errorLog;
        this.environment = new Environment();
    }

	protected IntVal interpretFile(VerilogFile File) throws Exception{
		for(ModuleDeclaration Decl : File.modules){
			interpretModule(Decl);
		}

		//Now after the Module Declaration has been interpreted we need to 
		//run all the saved processes associated with the Module
		int numberOfExitTickets = environment.getNumberOfProcesses();
		Semaphore exitTickets = new Semaphore(-numberOfExitTickets + 1);

		for(int i = 0; i < numberOfExitTickets; i++){
			ProcessBase Process = environment.getProcess(i);
			Process.initEnvironment(this, errorLog, exitTickets);
			Thread Tr = new Thread(Process);
			Tr.run();
		}

		try {
			exitTickets.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			errorLog.addItem(new ErrorItem("Could Not Aquire the Semaphore"));
		}

		return OpUtil.success();
	}

	protected IntVal interpretModule(ModuleDeclaration mod) throws Exception{
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

	 protected IntVal interpretModuleItem(ModuleItem Item) throws Exception{
		if(Item instanceof GateDeclaration) return interpretGateDeclaration((GateDeclaration)Item);
		else if (Item instanceof IdentDeclaration) return interpretIdentDeclaration((IdentDeclaration)Item);
		else if (Item instanceof Unidentified.Declaration) return interpretUnidentifiedDeclaration((Unidentified.Declaration)Item);
		else if (Item instanceof ModuleInstance) return interpretModuleInstance((ModuleInstance)Item);
		else if (Item instanceof ModuleInstantiation) return interpretModInstantiation((ModuleInstantiation)Item);
		else if (Item instanceof ProcedureDeclaration) return interpretProcedureDeclaration((ProcedureDeclaration)Item);
		else if (Item instanceof ProcessBase) return interpretProcess((ProcessBase)Item);
		else if (Item instanceof ContinuousAssignment) return interpretContinuousAssignment((ContinuousAssignment)Item);
		else if (Item instanceof EmptyModItem) return interpretEmptyModItem((EmptyModItem)Item);
		else {
			OpUtil.errorAndExit("Error: Invalid Module Item Type ~ " + Item.getClass().getName());
			return OpUtil.errorOccured();
		}
	 }

	 protected IntVal interpretGateDeclaration(GateDeclaration Item) throws Exception{
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
	 * @throws Exception
	 */

	protected IntVal interpretAndGate(AndGateDeclaration decl) throws Exception{

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
	 * @throws Exception
	 */

	protected IntVal interpretOrGate(OrGateDeclaration decl) throws Exception{
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
	 * @throws Exception
	 */

	 protected IntVal interpretNandGate(NandGateDeclaration decl) throws Exception{

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
	 * @throws Exception
	 */

	 protected IntVal interpretNorGate(NorGateDeclaration decl) throws Exception{

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
	 * @throws Exception
	 */

	 protected IntVal interpretXorGate(XorGateDeclaration decl) throws Exception{

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

	 protected IntVal interpretXnorGate(XnorGateDeclaration decl) throws Exception{

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
	 * @throws Exception
	 */

	protected IntVal interpretNotGate(NotGateDeclaration decl) throws Exception{
		List<Expression> connections = decl.gateConnections;

		Expression outputConnection = connections.remove(0);
		Expression inputConnection = connections.remove(0);
		Web output = (Web)interpretDeepExpression(outputConnection);
		Web input = (Web)interpretDeepExpression(inputConnection);

		new NotGate(output, input);

		return OpUtil.success();
	}

	protected IntVal interpretModuleInstance(ModuleInstance instance) throws Exception{
		List<Expression> expressions = instance.expList;

		if(environment.moduleExists(instance.instanceName)){
			ModuleDeclaration modDeclaration = environment.lookupModule(instance.instanceName);
			//First Redeclare all of the Items in the Module inside a new Scope
			interpretModule(modDeclaration);
		}


		OpUtil.errorAndExit("Error: module instances have not been handled by the interpreter up to this point...");

		return OpUtil.errorOccured();
	}

	protected IntVal interpretModInstantiation(ModuleInstantiation modList) throws Exception{
		List<ModuleInstance> modInstances = modList.modList;
		
		for(ModuleInstance Instance : modInstances){
			interpretModuleInstance(Instance);
		}

		return OpUtil.errorOccured();
	}

	/**
	 * Now we will defined the Procedure Declarations and where those statements reside
	 * @throws Exception
	 */

	 protected IntVal interpretProcedureDeclaration(ProcedureDeclaration Procedure) throws Exception{
		if(Procedure instanceof TaskDeclaration) return interpretTaskDeclaration((TaskDeclaration) Procedure);
		else if (Procedure instanceof FunctionDeclaration) return interpretFunctionDeclaration((FunctionDeclaration) Procedure);
		else {
			OpUtil.errorAndExit("Unknown Procedure Declaration SubType " + Procedure.getClass().getName());
			return OpUtil.errorOccured();
		}
	 }

	 /**
	 * This is used to visit a task declaration in verilog
	 * 
	 * @param task
	 * @throws Exception
	 */

	protected IntVal interpretTaskDeclaration(TaskDeclaration task) throws Exception{
		String taskName = task.taskName;

		if (environment.functionExists(taskName)) {
			OpUtil.errorAndExit("Task declaration by the name of " + taskName + " found at [" + task.position
				+ "] already exists at " + environment.lookupTask(taskName).position.toString());
		} else {
			environment.addTask(taskName, task);
		}

		return null;
	}

	protected IntVal interpretFunctionDeclaration(FunctionDeclaration function) throws Exception{
		StrVal functionName = OpUtil.fetchFunctionName(function.functionName);
		// May need to finish this later

		if(environment.functionExists(functionName.toString())){
			OpUtil.errorAndExit("Error: No function with the name of " + functionName + " was found");
			return OpUtil.errorOccured();
		} else {
			environment.addFunction(functionName.toString(), function);
			return OpUtil.success();
		}
	}

	protected IntVal interpretProcess(ProcessBase process) throws Exception{
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

	protected IntVal interpretAllwaysProcess(AllwaysProcess process){
		environment.addProcess(process);
		return OpUtil.success();
	}

	/**
	 * Now we will go through and visit all of the ModuleItem classes
	 */

	protected IntVal interpretInitialProcess(InitialProcess process){
		environment.addProcess(process);
		return OpUtil.success();
	}

	/**
	 * This is the code to visit a Continuous Assignment in Verilog.
	 * 
	 * @param assign
	 * @throws Exception
	 */

	protected IntVal interpretContinuousAssignment(ContinuousAssignment assign) throws Exception{
		for (BlockingAssignment amnt : assign.assignmentList){
			interpretDeepAssignment(amnt);
		}

		return OpUtil.success();
	}

	protected IntVal interpretDeepAssignment(BlockingAssignment Assignment) throws Exception{
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

	protected IntVal interpretEmptyModItem(EmptyModItem macro){
		return null; // this class is just for completeness
	}

	protected IntVal interpretIdentDeclaration(IdentDeclaration declaration) throws Exception{
		if(declaration instanceof ArrayDeclaration) return interpretArrayDeclaration((ArrayDeclaration)declaration);
		else if(declaration instanceof Input.Wire.Vector.Ident) return interpretDeclaration((Input.Wire.Vector.Ident)declaration);
		else if(declaration instanceof Input.Reg.Vector.Ident) return interpretDeclaration((Input.Reg.Vector.Ident)declaration);
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
		else if(declaration instanceof Real.Ident) return interpretDeclaration((Real.Ident)declaration);
		else {
			OpUtil.errorAndExit("Error Could not find Ident Declaration with the following type " + declaration.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	protected IntVal interpretArrayDeclaration(ArrayDeclaration declaration) throws Exception{
		if(declaration instanceof Reg.Scalar.Array) return interpretDeclaration((Reg.Scalar.Array)declaration);
		else if(declaration instanceof Reg.Vector.Array) return interpretDeclaration((Reg.Vector.Array)declaration);
		else if(declaration instanceof Int.Array) return interpretDeclaration((Int.Array)declaration);
		else {
			OpUtil.errorAndExit("No Array Type found of type " + declaration.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	protected IntVal interpretDeclaration(Input.Wire.Vector.Ident decl) throws Exception{
		Expression exp1 = decl.GetIndex1();
		Expression exp2 = decl.GetIndex2();

		Value val1 = interpretShallowExpression(exp1);
		Value val2 = interpretShallowExpression(exp2);

		if (!environment.localVariableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(val1.intValue(), val2.intValue()));
		} else {
			OpUtil.errorAndExit("Error identifier allready exists...");
		}

		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Reg.Scalar.Array decl) throws Exception;
	protected abstract IntVal interpretDeclaration(Reg.Vector.Array decl) throws Exception;

	protected IntVal interpretDeclaration(Int.Array decl) throws Exception{
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new ArrayVal<IntVal>(ArraySize));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Input.Reg.Vector.Ident decl) throws Exception;

	/**
	 * This is used to visit any input VectorVal declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Input.Wire.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new WireVal());
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Input.Reg.Scalar.Ident decl) throws Exception;

	/**
	 * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
	 * ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Wire.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
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
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Wire.Vector.Ident decl) throws Exception{
		Expression index1 = decl.GetIndex1();
		 // check whether the expressions return ints
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if (!environment.localVariableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Reg.Scalar.Ident decl) throws Exception;
	protected abstract IntVal interpretDeclaration(Reg.Vector.Ident decl) throws Exception;

	/**
	 * This is used to visit any output scalar declaration in Verilog. Ex. output a, b, c
	 * ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Output.Wire.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new WireVal());
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Output.Reg.Scalar.Ident decl) throws Exception;

	protected IntVal interpretDeclaration(Output.Wire.Vector.Ident decl) throws Exception{
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	protected abstract IntVal interpretDeclaration(Output.Reg.Vector.Ident  decl) throws Exception;

	/**
	 * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Int.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
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
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Real.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
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
	 * @throws Exception
	 */

	protected IntVal interpretUnidentifiedDeclaration(Unidentified.Declaration decl) throws Exception{
		String Current = decl.declaration;

		if(!environment.localVariableExists(Current)){
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
	 * @throws Exception
	 */

	public IntVal interpretShallowStatement(Statement Stat) throws Exception{
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

	protected IntVal interpretDeepStatement(Statement Stat) throws Exception{
		if(Stat instanceof Assignment) return interpretDeepAssignment((Assignment)Stat);
		else if(Stat instanceof SeqBlockStatement) return interpretDeepBlockOfStatements((SeqBlockStatement)Stat);
	    else {
			OpUtil.errorAndExit("Invalid Type Node");
			return OpUtil.errorOccured();
		}
	}

	protected IntVal interpretShallowAssignment(Assignment assign) throws Exception{
		if(assign instanceof BlockingAssignment) return interpretShallowBlockingAssignment((BlockingAssignment)assign);
		else if (assign instanceof NonBlockingAssignment) return interpretShallowNonBlockingAssignment((NonBlockingAssignment)assign);
		else {
			OpUtil.errorAndExit("Invalid Assingment type found at position " + assign.position);
			return OpUtil.errorOccured();
		}
	}

	protected IntVal interpretDeepAssignment(Assignment assign) throws Exception{
		if(assign instanceof BlockingAssignment) return interpretDeepBlockingAssignment((BlockingAssignment)assign);
		else if (assign instanceof NonBlockingAssignment) return interpretDeepNonBlockingAssignment((NonBlockingAssignment)assign);
		else {
			OpUtil.errorAndExit("Invalid Assingment type found at position " + assign.position);
			return OpUtil.errorOccured();
		}
	}

	protected abstract IntVal interpretShallowBlockingAssignment(BlockingAssignment assign) throws Exception;

	protected IntVal interpretDeepBlockingAssignment(BlockingAssignment assign) throws Exception{
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

	protected abstract IntVal interpretShallowNonBlockingAssignment(NonBlockingAssignment assign) throws Exception;

	protected IntVal interpretDeepNonBlockingAssignment(NonBlockingAssignment assign) throws Exception{
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
	 * @throws Exception
	 */

	protected IntVal interpretCaseStatement(CaseStatement stat) throws Exception{
		if(stat instanceof CaseXStatement) return interpretCaseXStatement((CaseXStatement) stat);
		else if (stat instanceof CaseZStatement) return interpretCaseZStatement((CaseZStatement) stat);
		else {
			loop: for (CaseItem item : stat.itemList){
				Value switchExpVal = interpretShallowExpression(stat.exp);
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
	 * @throws Exception
	 */

	protected IntVal interpretCaseXStatement(CaseXStatement stat) throws Exception{
		loop: for (CaseItem item : stat.itemList){
			Value switchExp = interpretShallowExpression(stat.exp);
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

	protected IntVal interpretCaseZStatement(CaseZStatement stat) throws Exception{
		

		loop: for (CaseItem item : stat.itemList){
			Value switchExp = interpretShallowExpression(stat.exp);
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
	 * @throws Exception
	 */

	protected IntVal interpretForLoop(ForStatement forLoop) throws Exception{
		for (interpretShallowBlockingAssignment(forLoop.init); 
			interpretShallowExpression(forLoop.exp).boolValue() && !environment.stackFrameInExit(); 
			interpretShallowStatement(forLoop.change)) {
			
			interpretShallowStatement(forLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a forever loop in verilog
	 * 
	 * @param foreverLoop
	 * @throws Exception
	 */

	protected IntVal interpretForeverLoop(ForeverStatement foreverLoop) throws Exception{
		boolean tf = true;

		while(!environment.stackFrameInExit()) {
			interpretShallowStatement(foreverLoop.stat);
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 * @throws Exception
	 */

	protected IntVal interpretIfElseStatement(IfElseStatement ifElseStatement) throws Exception{
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
	 * @throws Exception
	 */

	protected IntVal interpretIfStatement(IfStatement ifStatement) throws Exception{
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
	 * @throws Exception
	 */

	protected IntVal interpretRepeatLoop(RepeatStatement stat) throws Exception{
		Value expr = interpretShallowExpression(stat.exp);

		if (expr.isWire() || expr.isRegister() || expr.isBoolValue()) {
			if (expr.boolValue()) {
				interpretShallowStatement(stat.stat);
			}
		} else {
			long amount = expr.longValue();

			for (long i = 0; i < amount; i++) {
				interpretShallowStatement(stat.stat);
				if(environment.stackFrameInExit()){
					break;
				}
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a seq block in verilog
	 * 
	 * @param stat
	 * @throws Exception
	 */

	protected IntVal interpretShallowBlockOfStatements(SeqBlockStatement stat) throws Exception{
		for (Statement stmt : stat.statementList) {
			interpretShallowStatement(stmt);
			if(environment.stackFrameInExit()){
				break;
			}
		}

		return OpUtil.success();
	}

	protected IntVal interpretDeepBlockOfStatements(SeqBlockStatement stat) throws Exception{
		for (Statement stmt : stat.statementList) {
			interpretDeepStatement(stmt);
		}

		return OpUtil.success();
	}



	/**
	 * This is used to visit a taskcall in verilog
	 * 
	 * @param stat
	 * @throws Exception
	 */

	protected IntVal interpretTaskCall(TaskStatement task) throws Exception{
		if(task instanceof SystemTaskStatement) interpretSystemTaskCall((SystemTaskStatement)task);
		else {
			String tname = task.taskName;

			if (environment.taskExists(tname)) {
				// Collect symbol table data from the function
				TaskDeclaration funcData = environment.lookupTask(tname);
				environment.addStackFrame(tname);
				environment.BeginParamaterDeclarations();
				
				List<Value> argumentValues = new LinkedList<Value>();
				for(Expression exp : task.argumentList){
					Value result = interpretShallowExpression(exp);
					argumentValues.add(result);
				}

				List<String> paramaterNames = new LinkedList<String>();
				for(ModuleItem Decl : funcData.paramaters){
					String paramaterName = OpUtil.getParamaterName(Decl);
					if(paramaterName != null){
						paramaterNames.add(paramaterName);
					}
					interpretModuleItem(Decl);
				}

				environment.EndParamaterDeclarations();

				if (argumentValues.size() == paramaterNames.size()) {
					// Assign parameter value
					for(int i = 0; i < paramaterNames.size(); i++){
						String paramaterName = paramaterNames.get(i);
						Pointer<Value> paramaterValue = environment.lookupVariable(paramaterName);
						Expression argExpr = task.argumentList.get(i);
						Value argValue = argumentValues.get(i);
						paramaterValue.assign(argValue);
					}
				} else {
					OpUtil.errorAndExit("Argument amount mismatch " + tname + " [Expected -> " + paramaterNames.size()
						+ " | Got -> " + task.argumentList.size() + " ]");
					return OpUtil.errorOccured();
				}

				interpretShallowStatement(funcData.stat);
				environment.removeStackFrame();
			} else {
				OpUtil.errorAndExit("Function Entry " + tname + " Doesnt Exist", task.position);
				return OpUtil.errorOccured();
			}
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit a system task statement in verilog
	 * 
	 * @param stat
	 * @throws Exception
	 */

	protected abstract IntVal interpretSystemTaskCall(SystemTaskStatement task) throws Exception;


	protected IntVal interpretWaitStatement(WaitStatement wait) throws Exception{
		Expression expr = wait.exp;
		while(interpretShallowExpression(expr).boolValue());
		return interpretShallowStatement(wait.stat);
	}

	/**
	 * This is used to visit a while loop in verilog
	 * 
	 * @param whileLoop
	 * @throws Exception
	 */

	protected IntVal interpretWhileLoop(WhileStatement whileLoop) throws Exception{

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

	protected IntVal visit(EmptyStatement stat){
		// this is empty it is just a placeholder
		return OpUtil.success();
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	protected Value interpretShallowExpression(Expression exp) throws Exception{
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
		else if (exp instanceof Slice) return interpretShallowSlice((Slice)exp);
		else if (exp instanceof Element) return interpretShallowElement((Element)exp);
		else if (exp instanceof Identifier) return interpretShallowIdentifier((Identifier)exp);
		else {
			OpUtil.errorAndExit("Error: Could not find an expression of type" + exp.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

	protected Value interpretDeepExpression(Expression exp) throws Exception{
		if(exp instanceof BinaryOperation) return interpretDeepBinaryOperation((BinaryOperation)exp);
		else if (exp instanceof UnaryOperation) return interpretDeepUnaryOperation((UnaryOperation)exp);
		else if (exp instanceof Concatenation) return interpretDeepConcatenation((Concatenation)exp);
		else if (exp instanceof FunctionCall) return interpretDeepFunctionCall((FunctionCall)exp);
		else if (exp instanceof TernaryOperation) return interpretDeepTernaryOperation((TernaryOperation)exp);
		else if (exp instanceof PortConnection) return interpretDeepPortConnection((PortConnection)exp);
		else if (exp instanceof Identifier) return interpretDeepIdentifier((Identifier)exp);
		else {
			OpUtil.errorAndExit("Error: Could not find an expression of type " + exp.getClass().toString());
			return OpUtil.errorOccured();
		}
	}

	protected Value interpretDeepBinaryOperation(BinaryOperation op) throws Exception{
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

	protected Value interpretShallowBinaryOperation(BinaryOperation op) throws Exception{
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
	 * @throws Exception
	 */

	protected Value interpretShallowUnaryOperation(UnaryOperation op) throws Exception{
		Value right = interpretShallowExpression(op.rightHandSideExpression);

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
	 * @throws Exception
	 */

	 protected Value interpretDeepUnaryOperation(UnaryOperation op) throws Exception{
		Value right = interpretDeepExpression(op.rightHandSideExpression);

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
	 * @throws Exception
	 */

	protected Value interpretDeepConcatenation(Concatenation concat) throws Exception{
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
	 * @throws Exception
	 */

	 protected Value interpretShallowConcatenation(Concatenation concat) throws Exception{
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
	 * @throws Exception
	 */

	protected Value interpretConstantExpression(ConstantExpression expr) throws Exception{ 
		return interpretShallowExpression(expr.expression);
	}

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	protected Value interpretEmptyExpression(EmptyExpression expr){ 
		return OpUtil.success(); 
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 * @throws Exception
	 */

	protected Value interpretShallowFunctionCall(FunctionCall call) throws Exception{
		if(call instanceof SystemFunctionCall) return interpretSystemFunctionCall((SystemFunctionCall)call);
		else {
		String tname = call.functionName;

		if (environment.functionExists(tname)) {
			// Collect symbol table data from the function
			FunctionDeclaration funcData = environment.lookupFunction(tname);

			environment.addStackFrame(tname);

			//Add the Function Name to the Symbol Table
			interpretModuleItem(funcData.functionName);

			Pointer<Value> returnData = environment.lookupVariable(tname); // get return object

			List<Value> paramaterValues = new LinkedList<Value>();
			for(Expression paramExp : call.argumentList){
				Value argVal = interpretShallowExpression(paramExp);
				paramaterValues.add(argVal);
			}

			List<String> paramaterNames = new LinkedList<String>();
			for(ModuleItem parameter : funcData.paramaters){
				String paramaterName = OpUtil.getParamaterName(parameter);
				if(paramaterName != null){
					paramaterNames.add(paramaterName);
				}
				interpretModuleItem(parameter);
			} // declare the return variable for the function


			if (paramaterValues.size() == paramaterNames.size()) {
				for(int i = 0; i < paramaterValues.size(); i++){
					String paramaterName = paramaterNames.get(i);
					Value paramaterValue = paramaterValues.get(i);

					Pointer<Value> paramaterHolder = environment.lookupVariable(paramaterName);
					paramaterHolder.assign(paramaterValue);
				}
			} else {
				OpUtil.errorAndExit("Argument amount mismatch " + tname + " [Expected -> " + funcData.paramaters.size()
					+ " | Got -> " + call.argumentList.size() + " ]", call.position);
				return OpUtil.errorOccured();
			}

			environment.BeginFunctionBody();
			interpretShallowStatement(funcData.stat);
			environment.EndFunctionBody();

			environment.removeStackFrame();
			return returnData.deRefrence();
		} else {
			OpUtil.errorAndExit("Function Entry " + tname + " Doesnt Exist", call.position);
			return OpUtil.errorOccured();
		}
	}
	}

	protected Value interpretDeepFunctionCall(FunctionCall call) throws Exception{
		if(!(call instanceof SystemFunctionCall)){
			String tname = call.functionName;

			if (environment.functionExists(tname)) {
				// Collect symbol table data from the function
				FunctionDeclaration funcData = environment.lookupFunction(tname);

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
	 * @throws Exception
	 */
	protected abstract Value interpretSystemFunctionCall(SystemFunctionCall call) throws Exception;

	protected abstract Value interpretShallowIdentifier(Identifier ident) throws Exception;
	
	protected Value interpretDeepIdentifier(Identifier ident) throws Exception{
		if (environment.variableExists(ident.labelIdentifier)) {
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
 * @throws Exception
	*/

	protected Value interpretDeepPortConnection(PortConnection connection) throws Exception{
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

	protected Value interpretStr(StringNode string){
		return new StrVal(string.lexeme);
	}

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 * @throws Exception
	 */

	protected Value interpretShallowTernaryOperation(TernaryOperation expr) throws Exception{
		if (interpretShallowExpression(expr.condition).boolValue()) {
			return interpretShallowExpression(expr.ifTrue);
		} else {
			return interpretShallowExpression(expr.ifFalse);
		}
	}

	protected Value interpretDeepTernaryOperation(TernaryOperation expr) throws Exception{
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

	protected abstract Value interpretShallowElement(Element elem) throws Exception;
	protected abstract Value interpretShallowSlice(Slice vector) throws Exception;

	protected Value interpretBinaryNode(BinaryNode Bin) throws Exception{
		int indexOfColon = Bin.lexeme.indexOf('\'');

		if(indexOfColon == -1){
			OpUtil.errorAndExit("Error: Malformed BinaryNode");
			return OpUtil.errorOccured();
		} else {
			String beforeIndex = Bin.lexeme.substring(0, indexOfColon);
			String afterIndex = Bin.lexeme.substring(indexOfColon + 2, Bin.lexeme.length());

			if(OpUtil.numberIsPattern(afterIndex)){
				//Then it is a pattern and we need to return the Pattern
				return new BinaryPattern(afterIndex);
			} else {
				//Otherwise it is a typical Binary Number and we need to return the number
				return new UnsignedIntVal(Integer.parseUnsignedInt(afterIndex, 2));
			}
		}
	}

	protected Value interpretHexadecimalNode(HexadecimalNode Hex) throws Exception{
		int indexOfColon = Hex.lexeme.indexOf('\'');

		if(indexOfColon == -1){
			OpUtil.errorAndExit("Error: Malformed HexNode");
			return OpUtil.errorOccured();
		} else {
			String beforeIndex = Hex.lexeme.substring(0, indexOfColon);
			String afterIndex = Hex.lexeme.substring(indexOfColon + 2, Hex.lexeme.length());

			if(OpUtil.numberIsPattern(afterIndex)){
				return new HexadecimalPattern(afterIndex);
			} else {
				return new UnsignedIntVal(Integer.parseUnsignedInt(afterIndex, 16));
			}
		}
	}

	protected Value interpretDecimalNode(DecimalNode Dec) throws Exception{
		int indexOfColon = Dec.lexeme.indexOf('\'');

		if(indexOfColon == -1){
			return new UnsignedIntVal(Integer.parseInt(Dec.lexeme));
		} else {
			String beforeIndex = Dec.lexeme.substring(0, indexOfColon);
			String afterIndex = Dec.lexeme.substring(indexOfColon + 2, Dec.lexeme.length());
			return new UnsignedIntVal(Integer.parseUnsignedInt(afterIndex, 16));
		}
	}

	protected Value interpretOctalNode(OctalNode Oct) throws Exception{
		int indexOfColon = Oct.lexeme.indexOf('\'');

		if(indexOfColon == -1){
			OpUtil.errorAndExit("Error: Malformed OctalNode");
			return OpUtil.errorOccured();
		} else {
			String beforeIndex = Oct.lexeme.substring(0, indexOfColon);
			String afterIndex = Oct.lexeme.substring(indexOfColon + 2, Oct.lexeme.length());

			if(OpUtil.numberIsPattern(afterIndex)){
				return new OctalPattern(afterIndex);
			} else {
				return new UnsignedIntVal(Integer.parseUnsignedInt(afterIndex, 8));
			}
		}
	}

	protected Value interpretStringNode(StringNode Str){
		return new StrVal(Str.lexeme);
	}
}
