package edu.depauw.emulator_ide.verilog_compiler.visitor_passes.type_checker;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NandGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.NotGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.OrGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.XnorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.XorGateDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstance;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstantiation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.AllwaysProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.InitialProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Input;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Int;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Output;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Real;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Reg;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.RegValueList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Unidentified;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Wire;
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
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.type_checker.TypeCheckerVariableData.Type;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.StatementVisitor;

public class TypeChecker implements ExpressionVisitor<TypeCheckerVariableData.Type>, StatementVisitor<Void>, ModuleVisitor<Void>{

	private final SymbolTable<Position>                modEnv;
	private final SymbolTable<TypeCheckerFunctionData> funcEnv;
	private final SymbolTable<TypeCheckerVariableData> varEnv;
	private final ErrorLog                             errorLog;

	public TypeChecker(ErrorLog errorLog) {
		this.modEnv = new SymbolTable<>();
		this.funcEnv = new SymbolTable<>();
		this.varEnv = new SymbolTable<>();
		this.errorLog = errorLog;
	}

	public Void visit(ModuleDeclaration mod, Object... argv){
		modEnv.addScope();
		funcEnv.addScope();
		varEnv.addScope();
		
		String modName = mod.moduleName;
		if (modEnv.entryExists(modName)) {
			errorLog.addItem(new ErrorItem(
				"Redeclaration of Module " + modName + " at " + '[' + mod.position + "]\n originally declared here... ",
				modEnv.getEntry(modName)));
		} else {
			modEnv.addEntry(modName, mod.position);
		}

		for(ModuleItem Item : mod.moduleItemList){
			Item.accept(this);
		}

		varEnv.removeScope();
		funcEnv.removeScope();
		modEnv.removeScope();
		return null;
	}

	/**
	 * This is the visit statment to visit an Allways Statement.
	 * 
	 * @param stat
	 */

	public Void visit(AllwaysProcess stat, Object... argv){

		return null;
	}

	/**
	 * This is the code to visit a Continuous Assignment in Verilog.
	 * 
	 * @param assign
	 */

	public Void visit(ContinuousAssignment assign, Object... argv){

		for(BlockingAssignment Assign : assign.assignmentList){
			Assign.accept(this);
		}

		return null;
	}

	/**
	 * This is the code that is used to visit a function declaration in java
	 * 
	 * @param function
	 */
	private boolean    inFunctionParam = false;
	private boolean    inFunctionName  = false;
	private String topFunctionName = "NULL";

	public Void visit(FunctionDeclaration function, Object... argv){
		ModuleItem funcDeclaration = function.functionName;
		varEnv.addScope(); // add variable scope for function declarations and paramteters

		/*
		 * In Verilog functions return via an assignment to the function name To do this the
		 * function needs to be in the variable environment as well This will allow the user to
		 * use assignment statements on the function name However to return something the
		 * Function environment needs to keep track of the variable data too The in function
		 * name makes it so the name of the function is returned to topFunctionName
		 */

		inFunctionName = true;
		funcDeclaration.accept(this);
		inFunctionName = false;

		if (funcEnv.entryExists(topFunctionName)) {
			TypeCheckerFunctionData data = funcEnv.getEntry(topFunctionName);
			ErrorItem error = new ErrorItem("Duplicate function " + topFunctionName + " called at " + function.position + " already was declared ", data.getPosition());
			errorLog.addItem(error);
		} else {
			TypeCheckerVariableData varData = varEnv.getEntry(topFunctionName);
			TypeCheckerFunctionData funcData = new TypeCheckerFunctionData(varData, function.position);
			funcEnv.addEntry(topFunctionName, funcData);
		}

		inFunctionParam = true;
		for (ModuleItem Item :  function.paramaters) { 
			Item.accept(this);
		}
		inFunctionParam = false;

		function.stat.accept(this);
		varEnv.removeScope();

		return null;
	}

	/**
	 * This is the code to visit a Initial Statement in Verilog
	 * 
	 * @param stat
	 */

	public Void visit(InitialProcess stat, Object... argv){
		varEnv.addScope();
		stat.statement.accept(this);
		varEnv.removeScope();
		return null;
	}

	/**
	 * This is the code to visit a Module call or Instantiation in verilog
	 * 
	 * @param mod
	 */

	public Void visit(ModuleInstantiation mod, Object... argv){
		for (ModuleInstance Instance : mod.modList) {
			Instance.accept(this);
		}

		return null;
	}

	/**
	 * This is the code to visit a Module instance in Verilog
	 * 
	 * @param mod
	 */

	public Void visit(ModuleInstance mod, Object... argv){
		String modName = mod.instanceName;

		if (!modEnv.entryExists(modName)) {
			ErrorItem error = new ErrorItem("No Module found declared with the name " + modName, mod.position);
			errorLog.addItem(error);
		}

		for (Expression expr : mod.expList) {
			expr.accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit a task declaration in verilog
	 * 
	 * @param task
	 */

	public Void visit(TaskDeclaration task, Object... argv){
		String taskName = task.taskName;

		if (funcEnv.entryExists(taskName)) {
			TypeCheckerFunctionData data = funcEnv.getEntry(taskName);
			ErrorItem error = new ErrorItem("Task declaration by the name of " + taskName + " found at ["
			+ task.position + "] already exists at ", data.getPosition());
			errorLog.addItem(error);
		} else {
			funcEnv.addEntry(taskName, new TypeCheckerFunctionData(null, task.position));
		}

		inFunctionParam = true;
		topFunctionName = taskName;
		varEnv.addScope();

		for (ModuleItem exp : task.paramaters){ 
			exp.accept(this);
		}

		inFunctionParam = false;
		task.stat.accept(this);
		varEnv.removeScope();
		return null;
	}

	/**
	 * This is used to visit a empty mod item in verilog
	 * 
	 * @param task
	 */

	public Void visit(EmptyModItem macro, Object... argv){
		return null; // this class is just for completeness
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. Ex. input a, b, c ...
	 * ;
	 * 
	 * @param decl
	 */

	public Void visit(Input.Wire.Scalar.Ident decl, Object... argv){
			String declarationName = decl.declarationIdentifier;

			if (varEnv.entryExists(declarationName)) {
				TypeCheckerVariableData entryData = varEnv.getEntry(declarationName);
				
				if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
					entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
				} else {
					ErrorItem error = new ErrorItem("Cannot override the declaration of "+ declarationName +" declared at " + entryData.getPosition() + " from type " + entryData.type + " to type " + Type.INPUT_WIRE, decl.position);
					errorLog.addItem(error);
				}

				if (inFunctionParam && funcEnv.entryExists(declarationName)) {
					funcEnv.getEntry(declarationName).addParameterType(entryData); // add paramter to function
				}

			} else {
				TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE, decl.position);
				varEnv.addEntry(declarationName, data);
			}
		return null;
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. Ex. input a, b, c ...
	 * ;
	 * 
	 * @param decl
	 */

	public Void visit(Input.Reg.Scalar.Ident decl, Object... argv){
		String declarationName = decl.declarationIdentifier;

		if (varEnv.entryExists(declarationName)) {
			TypeCheckerVariableData entryData = varEnv.getEntry(declarationName);

			if (entryData.type == Type.UNDEFINED) {
				entryData.type = Type.INPUT_REGISTER;
			} else {
				ErrorItem error = new ErrorItem("Cannot override the declaration of "+ declarationName +" declared at " + entryData.getPosition() + " from type " + entryData.type + " to type " + Type.INPUT_REGISTER, decl.position);
				errorLog.addItem(error);
			}

			if (inFunctionParam) {
				funcEnv.getEntry(declarationName).addParameterType(entryData); // add paramter to function
			}

		} else {
			TypeCheckerVariableData data = new TypeCheckerVariableData(Type.INPUT_REGISTER, decl.position);
			varEnv.addEntry(declarationName, data);
		}

		return null;
	}

	/**
	 * This is used to visit any input vector declaration in verilog. Ex. input [31:0] a, b,
	 * c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Input.Wire.Vector.Ident decl, Object... argv){
		Expression exprType1 = decl.GetIndex1(); // check whether the expressions return ints
		Expression exprType2 = decl.GetIndex2();

		TypeCheckerVariableData.Type type1 = exprType1.accept(this);
		TypeCheckerVariableData.Type type2 = exprType2.accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				ErrorItem error = new ErrorItem("");
				errorLog.addItem(error);
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
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

	public Void visit(Input.Reg.Vector.Ident decl, Object... argv){
		TypeCheckerVariableData.Type type1 = decl.GetIndex1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.GetIndex2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.position));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
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

	public Void visit(Wire.Scalar.Ident decl, Object... argv){

		if (varEnv.entryExists(decl.declarationIdentifier)) {
			TypeCheckerVariableData entryData = varEnv.getEntry(decl.declarationIdentifier);

			if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
				entryData.type = TypeCheckerVariableData.Type.WIRE;
			} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
				entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
			} else if (entryData.type == TypeCheckerVariableData.Type.INPUT) {
				entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
			} else {
				errorLog.addItem(new ErrorItem(
					"Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.WIRE,decl.position));
			}

		} else {
			varEnv.addEntry(decl.declarationIdentifier,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.WIRE, decl.position));
		}
		return null;
	}

	/**
	 * This is used to visit any wire vector declaration in verilog. Ex. wire [31:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Wire.Vector.Ident decl, Object... argv){
		TypeCheckerVariableData.Type type1 = decl.GetIndex1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.GetIndex2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.position));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Reg.Scalar.Ident decl, Object... argv){
		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Reg.Vector.Ident decl, Object... argv){

		TypeCheckerVariableData.Type type1 = decl.GetIndex1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.GetIndex2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.position));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any output scalar declaration in Verilog. Ex. output a, b, c
	 * ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Output.Wire.Scalar.Ident decl, Object... argv){

		if (varEnv.entryExists(decl.declarationIdentifier)) {
			TypeCheckerVariableData entryData = varEnv.getEntry(decl.declarationIdentifier);

			if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
				entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
			} else {
				errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
					+ TypeCheckerVariableData.Type.OUTPUT_WIRE, decl.position));
			}

		} else {
			varEnv.addEntry(decl.declarationIdentifier,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_WIRE, decl.position));
		}

		return null;
	}

	/**
	 * This is where I will declare the output Register Scalar declaration
	 * 
	 * @param Jacob Bauer
	 */

	public Void visit(Output.Reg.Scalar.Ident decl, Object... argv){

		return null;
	}

	public Void visit(Output.Wire.Vector.Ident decl, Object... argv){
		TypeCheckerVariableData.Type type1 = decl.GetIndex1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.GetIndex2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.position));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
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

	public Void visit(Output.Reg.Vector.Ident decl, Object... argv){

		TypeCheckerVariableData.Type type1 = decl.GetIndex1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.GetIndex2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.position));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.position));
			}

		}

		return null;
	}

	/**
	 * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Int.Ident decl, Object... argv){

		return null;
	}

	/**
	 * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Real.Ident decl, Object... argv){

		if (varEnv.entryExists(decl.declarationIdentifier)) {
			TypeCheckerVariableData dataType = varEnv.getEntry(decl.declarationIdentifier);
			errorLog.addItem(new ErrorItem(
				"Variable " + decl.declarationIdentifier + " allready defined at " + decl.position + " declared again at ",
				decl.position));
		} else {
			varEnv.addEntry(decl.declarationIdentifier,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REAL, decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any Unidentified declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Unidentified.Declaration decl, Object... argv){
		String current = decl.declaration;

		if (varEnv.entryExists(current)) {
			TypeCheckerVariableData dataType = varEnv.getEntry(current);
			errorLog.addItem(new ErrorItem(
				"Variable " + current + " allready defined at " + dataType.getPosition() + " declared again at ",
				decl.position));
		} else {
			varEnv.addEntry(decl.declaration,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.UNDEFINED, decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any andgate declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(AndGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("And declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any orgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(OrGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Or declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any nandgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NandGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Nand declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any norgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NorGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Nor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any xorgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(XorGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Xor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any xnorgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(XnorGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() >= 3) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Xnor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit any notgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NotGateDeclaration decl, Object... argv){

		if (decl.gateConnections.size() == 2) {
			for(Expression exp : decl.gateConnections){
				TypeCheckerVariableData.Type type = exp.accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.position));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Not declaration must have atleast 2 paramteters (1 output, 2 inputs) however only "
				+ decl.gateConnections.size() + " were found", decl.position));
		}

		return null;
	}

	/**
	 * This is used to visit blocking assignments in verilog
	 * 
	 * @param assign
	 */

	public Void visit(BlockingAssignment assign, Object... argv){
		//TypeCheckerVariableData.Type type1 = assign.leftHandSide.accept(this);
		TypeCheckerVariableData.Type type2 = assign.rightHandSide.accept(this);

		/*
		if (/*type1 == TypeCheckerVariableData.Type.INTEGER
			&& (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		} else if (/*type1 == TypeCheckerVariableData.Type.REAL
			&& (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		} else if (isReg(type1) && type2 == TypeCheckerVariableData.Type.REAL) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		}
		*/

		return null;
	}

	/**
	 * This is used to visit case statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.exp.accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.position));
		}
		
		for(CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for(Expression exp : exprItem.expList){
					TypeCheckerVariableData.Type exprType = exp.accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER
						&& exprType != TypeCheckerVariableData.Type.CONSTANT_REAL) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.position));
					}

				}

			}

			item.statement.accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit casex statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseXStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.exp.accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.position));
		}
		
		for(CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for(Expression exp : exprItem.expList){
					TypeCheckerVariableData.Type exprType = exp.accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER
						&& exprType != TypeCheckerVariableData.Type.CONSTANT_REAL) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.position));
					}

				}

			}

			item.statement.accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit casez statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseZStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.exp.accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.position));
		}
		
		for(CaseItem item : stat.itemList){
			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for(Expression exp : exprItem.expList){
					TypeCheckerVariableData.Type exprType = exp.accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER
						&& exprType != TypeCheckerVariableData.Type.CONSTANT_REAL) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.position));
					}

				}

			}

			item.statement.accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit a for loop in verilog
	 * 
	 * @param forLoop
	 */

	public Void visit(ForStatement forLoop, Object... argv){
		forLoop.init.accept(this);
		TypeCheckerVariableData.Type type = forLoop.exp.accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Expression Type for For loop" + type, forLoop.position));
		}

		forLoop.stat.accept(this);
		return null;
	}

	/**
	 * This is used to visit a forever loop in verilog
	 * 
	 * @param foreverLoop
	 */

	public Void visit(ForeverStatement foreverLoop, Object... argv){
		foreverLoop.stat.accept(this);
		return null;
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public Void visit(IfElseStatement ifElseStatement, Object... argv){
		TypeCheckerVariableData.Type type = ifElseStatement.condition.accept(this);

		if (type == TypeCheckerVariableData.Type.STRING || isArray(type)) {
			errorLog.addItem(new ErrorItem("Unexpected expression type " + type + " for if-else statement ",
				ifElseStatement.position));
		}

		ifElseStatement.trueStatement.accept(this);
		ifElseStatement.falseStatement.accept(this);
		return null;
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public Void visit(IfStatement ifStatement, Object... argv){
		TypeCheckerVariableData.Type type = ifStatement.condition.accept(this);

		if (type == TypeCheckerVariableData.Type.STRING || isArray(type)) {
			errorLog.addItem(new ErrorItem("Unexpected expression type " + type + " for if statement ", ifStatement.position));
		}

		ifStatement.trueStatement.accept(this);
		return null;
	}

	/**
	 * This is used to visit a non blocking assignment statement in verilog
	 * 
	 * @param assign
	 */

	public Void visit(NonBlockingAssignment assign, Object... argv){
		for(Expression rhs : assign.rightHandSide){
			rhs.accept(this);
		}

		/*
		if (type1 == TypeCheckerVariableData.Type.INTEGER
			&& (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		} else if (type1 == TypeCheckerVariableData.Type.REAL
			&& (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		} else if (isReg(type1) && type2 == TypeCheckerVariableData.Type.REAL) {
			errorLog
				.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
		}
		*/

		return null;
	}

	/**
	 * This is used to visit a repeat statement in verilog
	 * 
	 * @param stat
	 */

	public Void visit(RepeatStatement stat, Object... argv){
		TypeCheckerVariableData.Type type = stat.exp.accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, stat.position));
		}

		stat.stat.accept(this);
		return null;
	}

	/**
	 * This is used to visit a seq block in verilog
	 * 
	 * @param stat
	 */

	public Void visit(SeqBlockStatement stat, Object... argv){
		for(Statement stmt : stat.statementList){
			stmt.accept(this);
		}


		return null;
	}

	/**
	 * This is used to visit a taskcall in verilog
	 * 
	 * @param stat
	 */

	public Void visit(TaskStatement task, Object... argv){
		String tname = task.taskName;

		if (funcEnv.entryExists(tname)) {
			TypeCheckerFunctionData funcData = funcEnv.getEntry(tname);

			if (task.argumentList.size() == funcData.numParameterTypes()) {
				int i = 0;
				for (Expression exp : task.argumentList) {
					TypeCheckerVariableData.Type call = exp.accept(this);
					TypeCheckerVariableData def = funcData.getParameterType(i);

					if (def.type == TypeCheckerVariableData.Type.UNDEFINED || call == TypeCheckerVariableData.Type.UNDEFINED) {
						errorLog.addItem(new ErrorItem("Can not have undefined types as parameters [FunctionDef -> " + def.type
							+ " | FunctionCall -> " + call + "]", task.position));
					}
					i++;
				}
			} else {
				errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname + " [Expected -> "
					+ funcData.numParameterTypes() + " | Got -> " + task.argumentList.size() + " ]", task.position));
			}

		} else {
			errorLog.addItem(new ErrorItem("Function Entry " + tname + " doesnt exist", task.position));
		}

		return null;
	}

	/**
	 * This is used to visit a system task statement in verilog
	 * 
	 * @param stat
	 */

	public Void visit(SystemTaskStatement task, Object... argv){
		// These are not important for now I will handle those later
		return null;
	}

	/**
	 * This is used to visit a wait statement in verilog
	 * 
	 * @param stat
	 */

	public Void visit(WaitStatement wait, Object... argv){
		TypeCheckerVariableData.Type type = wait.exp.accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, wait.position));
		}

		wait.stat.accept(this);
		return null;
	}

	/**
	 * This is used to visit a while loop in verilog
	 * 
	 * @param whileLoop
	 */

	public Void visit(WhileStatement whileLoop, Object... argv){
		TypeCheckerVariableData.Type type = whileLoop.exp.accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(
				new ErrorItem("Unknown type " + type + " for while loop expression ", whileLoop.position));
		}

		whileLoop.stat.accept(this);
		return null;
	}

	/**
	 * This is the code for visiting empty statements this is here just for completion
	 * 
	 * @param none
	 */

	public Void visit(EmptyStatement stat, Object... argv){
		// this is empty it is just a placeholder
		return null;
	}

	/*
	 * Below is the code that is used for visiting Expressions
	 */

	private static boolean isArray(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.REGISTER_ARRAY || type == TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY
			|| type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY
			|| type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY || type == TypeCheckerVariableData.Type.INTEGER_ARRAY);
	}

	private static boolean isWire(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.INPUT_WIRE || type == TypeCheckerVariableData.Type.WIRE
			|| type == TypeCheckerVariableData.Type.OUTPUT_WIRE || type == TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR
			|| type == TypeCheckerVariableData.Type.WIRE_VECTOR || type == TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR);
	}

	private static boolean isReg(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.OUTPUT_REGISTER || type == TypeCheckerVariableData.Type.REGISTER
			|| type == TypeCheckerVariableData.Type.REGISTER_VECTOR
			|| type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR);
	}

	private static boolean isScalar(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.INPUT_WIRE || type == TypeCheckerVariableData.Type.WIRE
			|| type == TypeCheckerVariableData.Type.OUTPUT_WIRE || type == TypeCheckerVariableData.Type.OUTPUT_REGISTER
			|| type == TypeCheckerVariableData.Type.REGISTER);
	}

	private static boolean isVector(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR || type == TypeCheckerVariableData.Type.WIRE_VECTOR
			|| type == TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR || type == TypeCheckerVariableData.Type.REGISTER_VECTOR
			|| type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR);
	}

	private static boolean isInteger(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.INTEGER || type == TypeCheckerVariableData.Type.CONSTANT_INTEGER);
	}

	private static boolean isReal(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.REAL || type == TypeCheckerVariableData.Type.CONSTANT_REAL);
	}

	private static boolean isConstant(TypeCheckerVariableData.Type type){
		return (type == TypeCheckerVariableData.Type.CONSTANT_INTEGER || type == TypeCheckerVariableData.Type.CONSTANT_REAL);
	}

	/**
	 * This is the code for visiting binary operations
	 * 
	 * @param op
	 */

	public TypeCheckerVariableData.Type visit(BinaryOperation op, Object... argv){
		TypeCheckerVariableData.Type left = op.left.accept(this);
		TypeCheckerVariableData.Type right = op.right.accept(this);

		if (left == TypeCheckerVariableData.Type.UNDEFINED) {
			return right;
		} else if (right == TypeCheckerVariableData.Type.UNDEFINED) {
			return left;
		} else if (left == TypeCheckerVariableData.Type.STRING || right == TypeCheckerVariableData.Type.STRING) {
			errorLog.addItem(new ErrorItem("Can't have Strings in Binary Operation " + left + " + " + right, op.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else if (isArray(left) || isArray(right)) {
			errorLog
				.addItem(new ErrorItem("Can't have Array Types in Binary Operation " + left + " + " + right, op.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {

			switch(op.Op){
				case PLUS:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && isConstant(left))) {
						return TypeCheckerVariableData.Type.CONSTANT_REAL;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && !isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && !isConstant(left))) {
						return TypeCheckerVariableData.Type.REAL;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.position));
						return isReal(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER_VECTOR;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case MINUS:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && isConstant(left))) {
						return TypeCheckerVariableData.Type.CONSTANT_REAL;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && !isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && !isConstant(left))) {
						return TypeCheckerVariableData.Type.REAL;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.position));
						return isReal(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER_VECTOR;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case TIMES:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && isConstant(left))) {
						return TypeCheckerVariableData.Type.CONSTANT_REAL;
					} else if ((isInteger(left) && isConstant(left) && isReal(right) && !isConstant(right))
						|| (isInteger(right) && isConstant(right) && isReal(left) && !isConstant(left))) {
						return TypeCheckerVariableData.Type.REAL;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.position));
						return isReal(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER_VECTOR;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case DIV:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " / " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " / " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					}
					if (isConstant(left) && isConstant(right)) {
						return TypeCheckerVariableData.Type.CONSTANT_REAL;
					} else {
						return TypeCheckerVariableData.Type.REAL;
					}
				case MOD:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(right) || isReal(left)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else {
						return TypeCheckerVariableData.Type.INTEGER;
					}
				case EQ2:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case EQ3:
					if (left != right) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.position));
						return TypeCheckerVariableData.Type.BOOLEAN;
					} else {
						return TypeCheckerVariableData.Type.BOOLEAN;
					}
				case NE1:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case NE2:
					if (left != right) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.position));
						return TypeCheckerVariableData.Type.BOOLEAN;
					} else {
						return TypeCheckerVariableData.Type.BOOLEAN;
					}
				case LAND:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case LOR:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case LE:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case LT:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case GE:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case GT:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case BAND:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " & " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " & " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case BOR:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " | " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " | " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case BXOR:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case BXNOR:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case LSHIFT:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				case RSHIFT:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.position));
						return isReal(left) ? right : left;
					} else if (right == left) {
						return left;
					} else if (left == TypeCheckerVariableData.Type.MIXED_VECTOR
						|| right == TypeCheckerVariableData.Type.MIXED_VECTOR) {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					} else if ((isInteger(right) && isConstant(right) && isInteger(left))
						|| (isInteger(left) && isConstant(left) && isInteger(right))) {
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isInteger(left) || isInteger(right)) {
						return isInteger(left) ? right : left;
					} else if (isWire(left) && isWire(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right) && (isVector(left) || isVector(right))) {
						return TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (isReg(left) && isReg(right)) {
						return TypeCheckerVariableData.Type.REGISTER;
					} else if (isWire(left) && isWire(right)) {
						return TypeCheckerVariableData.Type.WIRE;
					} else {
						return TypeCheckerVariableData.Type.MIXED_VECTOR;
					}
				default:
					errorLog.addItem(
						new ErrorItem("Invalid operation in verilog of type " + left + " " + op.Op + " " + right,
							op.position));
					return TypeCheckerVariableData.Type.UNDEFINED;
			}

		}

	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public TypeCheckerVariableData.Type visit(UnaryOperation op, Object... argv){
		TypeCheckerVariableData.Type right = op.rightHandSideExpression.accept(this);

		if (right == TypeCheckerVariableData.Type.UNDEFINED) {
			errorLog
				.addItem(new ErrorItem("Cant have an undefined value in expression [Type -> " + right + "]", op.position));
		}

		if (op.Op == UnaryOperation.Operator.PLUS
			&& (right == TypeCheckerVariableData.Type.BOOLEAN || right == TypeCheckerVariableData.Type.STRING)) {
			errorLog.addItem(new ErrorItem("Unexpected type for unary plus operation [Type -> " + right + "]", op.position));
		} else if (op.Op == UnaryOperation.Operator.LNEG && right == TypeCheckerVariableData.Type.STRING) {
			errorLog
				.addItem(new ErrorItem("Unexpected type for Boolean Not operation [Type -> " + right + "]", op.position));
		} else if (op.Op == UnaryOperation.Operator.BNEG && (right == TypeCheckerVariableData.Type.STRING
			|| right == TypeCheckerVariableData.Type.REAL || right == TypeCheckerVariableData.Type.BOOLEAN)) {
			errorLog
				.addItem(new ErrorItem("Unexpected type for Bitwise Not operation [Type -> " + right + "]", op.position));
		}

		return right;
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	public TypeCheckerVariableData.Type visit(Concatenation concat, Object... argv){
		return TypeCheckerVariableData.Type.MIXED_VECTOR;
	}

	/**
	 * This is the code for visiting Constant Expressions
	 * 
	 * @param expr
	 */

	public TypeCheckerVariableData.Type visit(ConstantExpression expr, Object... argv){
		TypeCheckerVariableData.Type type = expr.expression.accept(this);

		if (!isConstant(type)) {
			errorLog.addItem(new ErrorItem("Constant Expression must yeild constant result ", expr.position));
		}

		return type;
	}

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	public TypeCheckerVariableData.Type visit(EmptyExpression expr, Object... argv){
		return TypeCheckerVariableData.Type.UNDEFINED;
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public TypeCheckerVariableData.Type visit(FunctionCall call, Object... argv){
		String tname = call.functionName;

		if (funcEnv.entryExists(tname)) {
			TypeCheckerFunctionData funcData = funcEnv.getEntry(tname);

			if (call.argumentList.size() == funcData.numParameterTypes()) {

				int i = 0;
				for(Expression exp : call.argumentList){
					TypeCheckerVariableData.Type call2 = exp.accept(this);
					TypeCheckerVariableData def = funcData.getParameterType(i);

					if (def.type == TypeCheckerVariableData.Type.UNDEFINED || call2 == TypeCheckerVariableData.Type.UNDEFINED) {
						errorLog.addItem(new ErrorItem(
							"Can not have undefined types as parameters [Func -> " + def.type + " | Expr -> " + call2 + "]", call.position));
					}
					i++;
				}

			} else {
				errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname + " [Expected -> "
					+ funcData.numParameterTypes() + " | Got -> " + call.argumentList.size() + " ]", call.position));
			}

			return funcData.getReturnType().type;
		} else {
			errorLog.addItem(new ErrorItem("Function Entry " + tname + " Doesnt Exist", call.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		}

	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public TypeCheckerVariableData.Type visit(SystemFunctionCall call, Object... argv){
		if (call.functionName.equals("feof")) {
			return TypeCheckerVariableData.Type.BOOLEAN;
		} else {
			return TypeCheckerVariableData.Type.UNDEFINED;
		}

	}

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 */

	public TypeCheckerVariableData.Type visit(Identifier ident, Object... argv){

		if (varEnv.entryExists(ident.labelIdentifier)) {
			TypeCheckerVariableData entry = varEnv.getEntry(ident.labelIdentifier);

			if (entry.type == TypeCheckerVariableData.Type.UNDEFINED) {
				errorLog
					.addItem(new ErrorItem("Variable Entry " + ident.labelIdentifier + " is of Undefined type", ident.position));
			} else {
				return entry.type;
			}

			return varEnv.getEntry(ident.labelIdentifier).type;
		} else {
			errorLog.addItem(new ErrorItem("Variable Entry " + ident.labelIdentifier + " Doesnt Exist", ident.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		}

	}

	/**
	 * This is the code for visiting an Number in verilog
	 * 
	 * @param number
	 */

	public TypeCheckerVariableData.Type visit(Number number, Object... argv){
		if (number.toString().contains(".")) {
			return TypeCheckerVariableData.Type.CONSTANT_REAL;
		} else {
			return TypeCheckerVariableData.Type.CONSTANT_INTEGER;
		}

	}

	/**
	 * This is the code for visiting a port connection in verilog
	 * 
	 * @param connection
	 */

	public TypeCheckerVariableData.Type visit(PortConnection connection, Object... argv){
		return connection.connectingFrom.accept(this);
	}

	/**
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	public TypeCheckerVariableData.Type visit(StringNode string, Object... argv){
		// do nothing
		return TypeCheckerVariableData.Type.STRING;
	}

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	public TypeCheckerVariableData.Type visit(TernaryOperation expr, Object... argv){
		TypeCheckerVariableData.Type cond = expr.condition.accept(this);

		if (cond != TypeCheckerVariableData.Type.BOOLEAN && cond != TypeCheckerVariableData.Type.INTEGER
			&& cond != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
			errorLog.addItem(new ErrorItem("Expected condition to result in type boolean but got " + cond, expr.position));
		}

		TypeCheckerVariableData.Type left = expr.ifTrue.accept(this);
		TypeCheckerVariableData.Type right = expr.ifFalse.accept(this);

		if (left != right) {
			errorLog.addItem(new ErrorItem(
				"Expression types in ternary operation do not match [Left -> " + left + " | Right -> " + right + "]",
				expr.position));
		}

		return null;
	}

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public TypeCheckerVariableData.Type visit(Element vector, Object... argv){
		String ident = vector.labelIdentifier;

		if (!varEnv.entryExists(ident)) {
			errorLog.addItem(new ErrorItem("Identifier " + ident + " not found", vector.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {
			TypeCheckerVariableData data = varEnv.getEntry(ident);

			if (data.type == TypeCheckerVariableData.Type.INTEGER_ARRAY) {
				return TypeCheckerVariableData.Type.INTEGER;
			} else if (data.type == TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY) {
				return TypeCheckerVariableData.Type.REGISTER_VECTOR;
			} else if (data.type == TypeCheckerVariableData.Type.REGISTER_ARRAY) {
				return TypeCheckerVariableData.Type.REGISTER;
			} else if (data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY) {
				return TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR;
			} else if (data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY) {
				return TypeCheckerVariableData.Type.OUTPUT_REGISTER;
			} else if (data.type == TypeCheckerVariableData.Type.REGISTER_VECTOR) {
				return TypeCheckerVariableData.Type.REGISTER;
			} else if (data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR) {
				return TypeCheckerVariableData.Type.OUTPUT_REGISTER;
			} else if (data.type == TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR) {
				return TypeCheckerVariableData.Type.OUTPUT_WIRE;
			} else if (data.type == TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR) {
				return TypeCheckerVariableData.Type.OUTPUT_WIRE;
			} else if (data.type == TypeCheckerVariableData.Type.WIRE_VECTOR) {
				return TypeCheckerVariableData.Type.WIRE;
			} else if (data.type == TypeCheckerVariableData.Type.INPUT_VECTOR) {
				return TypeCheckerVariableData.Type.INPUT;
			} else if (data.type == TypeCheckerVariableData.Type.OUTPUT_VECTOR) {
				return TypeCheckerVariableData.Type.OUTPUT;
			} else {
				errorLog.addItem(new ErrorItem("Cant index a " + data.type, vector.position));
				return TypeCheckerVariableData.Type.UNDEFINED;
			}

		}

	}

	public TypeCheckerVariableData.Type visit(Slice vector, Object... argv){
		String ident = vector.labelIdentifier;

		if (!varEnv.entryExists(ident)) {
			errorLog.addItem(new ErrorItem("Identifier " + ident + " not found", vector.position));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			vector.index1.accept(this);
			vector.index2.accept(this);
			return data.type;
		}

	}

	public Void visit(Reg.Vector.Array regVector, Object... argv){
		String ident = regVector.declarationIdentifier;

		if (varEnv.inScope(ident)) {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident + " allready exists at " + data.getPosition(),
					regVector.position));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY, size, regVector.position));
		}

		return null;
	}

	public Void visit(Reg.Scalar.Array regScalar, Object... argv){
		String ident = regScalar.declarationIdentifier;

		if (varEnv.inScope(ident)) {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident + " allready exists at " + regScalar.position,
					regScalar.position));
		} else {
			varEnv.addEntry(ident,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_ARRAY, regScalar.position));
		}

		return null;
	}

	public Void visit(Output.Reg.Vector.Array regVector, Object... argv){
		String ident = regVector.declarationIdentifier;

		if (varEnv.inScope(ident)) {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident + " allready exists at " + regVector.position,
					regVector.position));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident, new TypeCheckerVariableData(
				TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY, size, regVector.position));
		}

		return null;
	}

	public Void visit(Output.Reg.Scalar.Array regScalar, Object... argv){
		String ident = regScalar.declarationIdentifier;

		if (varEnv.inScope(ident)) {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident + " allready exists at " + data.getPosition(),
					regScalar.position));
		} else {
			varEnv.addEntry(ident,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY, regScalar.position));
		}

		return null;
	}

	public Void visit(Int.Array intIdent, Object... argv){
		String ident = intIdent.declarationIdentifier;

		if (varEnv.inScope(ident)) {
			TypeCheckerVariableData data = varEnv.getEntry(ident);
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident + " allready exists at " + data.getPosition(),
					intIdent.position));
		} else {
			varEnv.addEntry(ident,
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER_ARRAY, intIdent.position));
		}

		return null;
	}

	@Override
	public Void visit(RegValueList decls, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Void visit(DefCaseItem stat, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Void visit(ExprCaseItem stat, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Type visit(BinaryNode number, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Type visit(DecimalNode number, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Type visit(HexadecimalNode number, Object... argv){ // TODO Auto-generated method stub
	return null; }

	@Override
	public Type visit(OctalNode number, Object... argv){ // TODO Auto-generated method stub
	return null; }
}
