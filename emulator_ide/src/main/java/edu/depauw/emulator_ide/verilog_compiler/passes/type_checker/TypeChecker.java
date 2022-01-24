package edu.depauw.emulator_ide.verilog_compiler.passes.type_checker;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.EmptyExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.PortConnection;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.UnaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.StringNode;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Real;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.r_value copy.IntegerIdent;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.EmptyStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.SeqBlockStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.WaitStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.CaseItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.ExprCaseItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.Assignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.BlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.NonBlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForeverStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.RepeatStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.WhileStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching._if_.IfStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.SystemTaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.TaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_compiler.passes.type_checker.TypeCheckerVariableData.Type;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;

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
		String modName = mod.getModuleName();

		if (modEnv.entryExists(modName)) {
			errorLog.addItem(new ErrorItem(
				"Redeclaration of Module " + modName + " at " + '[' + mod.getPosition() + "]\n originally declared here... ",
				modEnv.getEntry(modName)));
		} else {
			modEnv.addEntry(modName, mod.getPosition());
		}

		for (int i = 0; i < mod.numberOfModuleItems(); i++) { mod.getModuleItemAt(i).accept(this); }

		varEnv.removeScope();
		funcEnv.removeScope();
		modEnv.removeScope();
		return null;
	}

	/*
	 * Below is the code for visiting ModItem constructs
	 */

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

		for (int i = 0; i < assign.numAssignments(); i++) { assign.getAssignment(i).accept(this); }

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
		ModuleItem funcDeclaration = function.getFunctionName();
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
			ErrorItem error = new ErrorItem("Duplicate function " + topFunctionName + " called at " + function.getPosition() + " already was declared ", data.getPosition());
			errorLog.addItem(error);
		} else {
			TypeCheckerVariableData varData = varEnv.getEntry(topFunctionName);
			TypeCheckerFunctionData funcData = new TypeCheckerFunctionData(varData, function.getPosition());
			funcEnv.addEntry(topFunctionName, funcData);
		}

		inFunctionParam = true;
		for (int i = 0; i < function.numberofParamaters(); i++) { function.getParamater(i).accept(this); }
		inFunctionParam = false;

		function.getStatement().accept(this);
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
		stat.getStatement().accept(this);
		varEnv.removeScope();
		return null;
	}

	/**
	 * This is the code to visit a Module call or Instantiation in verilog
	 * 
	 * @param mod
	 */

	public Void visit(ModuleInstantiation mod, Object... argv){

		for (int i = 0; i < mod.numModInstances(); i++) { mod.getModInstance(i).accept(this); }

		return null;
	}

	/**
	 * This is the code to visit a Module instance in Verilog
	 * 
	 * @param mod
	 */

	public Void visit(ModuleInstance mod, Object... argv){
		String modName = mod.getInstanceName();

		if (!modEnv.entryExists(modName)) {
			ErrorItem error = new ErrorItem("No Module found declared with the name " + modName, mod.getPosition());
			errorLog.addItem(error);
		}

		for (int i = 0; i < mod.numExpressions(); i++) { mod.getExpression(i).accept(this); }

		return null;
	}

	/**
	 * This is used to visit a task declaration in verilog
	 * 
	 * @param task
	 */

	public Void visit(TaskDeclaration task, Object... argv){
		String taskName = task.getTaskName();

		if (funcEnv.entryExists(taskName)) {
			TypeCheckerFunctionData data = funcEnv.getEntry(taskName);
			ErrorItem error = new ErrorItem("Task declaration by the name of " + taskName + " found at ["
			+ task.getPosition() + "] already exists at ", data.getPosition());
			errorLog.addItem(error);
		} else {
			funcEnv.addEntry(taskName, new TypeCheckerFunctionData(null, task.getPosition()));
		}

		inFunctionParam = true;
		topFunctionName = taskName;
		varEnv.addScope();

		for (int i = 0; i < task.numberofParamaters(); i++) { task.getParamater(i).accept(this); }

		inFunctionParam = false;
		task.getStatement().accept(this);
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
			String declarationName = decl.getIdentifier();

			if (varEnv.entryExists(declarationName)) {
				TypeCheckerVariableData entryData = varEnv.getEntry(declarationName);
				
				if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
					entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
				} else {
					ErrorItem error = new ErrorItem("Cannot override the declaration of "+ declarationName +" declared at " + entryData.getPosition() + " from type " + entryData.type + " to type " + Type.INPUT_WIRE, decl.getPosition());
					errorLog.addItem(error);
				}

				if (inFunctionParam && funcEnv.entryExists(declarationName)) {
					funcEnv.getEntry(declarationName).addParameterType(entryData); // add paramter to function
				}

			} else {
				TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE, decl.getPosition());
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
		String declarationName = decl.getIdentifier();

		if (varEnv.entryExists(declarationName)) {
			TypeCheckerVariableData entryData = varEnv.getEntry(declarationName);

			if (entryData.type == Type.UNDEFINED) {
				entryData.type = Type.INPUT_REGISTER;
			} else {
				ErrorItem error = new ErrorItem("Cannot override the declaration of "+ declarationName +" declared at " + entryData.getPosition() + " from type " + entryData.type + " to type " + Type.INPUT_REGISTER, decl.getPosition());
				errorLog.addItem(error);
			}

			if (inFunctionParam) {
				funcEnv.getEntry(declarationName).addParameterType(entryData); // add paramter to function
			}

		} else {
			TypeCheckerVariableData data = new TypeCheckerVariableData(Type.INPUT_REGISTER, decl.getPosition());
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
		Expression type1 = decl.getLeftIndex(); // check whether the expressions return ints
		Expression type2 = decl.getRightIndex();



		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				ErrorItem error = new ErrorItem("");
				errorLog.addItem(error);
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

				if (varEnv.entryExists(current.getLexeme())) {
					TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

					if (vectorSize != entryData.getSize()) {
						errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> "
							+ entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
					}

					if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
						entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR;
					} else {
						errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
							+ TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR, current.getPosition()));
					}

					if (inFunctionParam) {
						funcEnv.getEntry(topFunctionName.getLexeme()).addParameterType(entryData); // add paramter to function
					}

				} else {
					TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR,
						vectorSize, current.getPosition());
					varEnv.addEntry(current.getLexeme(), data);

					if (inFunctionParam) {
						funcEnv.getEntry(topFunctionName.getLexeme()).addParameterType(data); // add paramter to function
					}

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

	public Void visit(InputRegVectorDeclarationNode decl, Object... argv){
		TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.getPosition()));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

			for (int i = 0; i < decl.numIdentifiers(); i++) {
				Identifier current = decl.getIdentifier(i);

				if (varEnv.entryExists(current.getLexeme())) {
					TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

					if (vectorSize != entryData.getSize()) {
						errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> "
							+ entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
					}

					if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
						entryData.type = TypeCheckerVariableData.Type.INPUT_REGISTER_VECTOR;
					} else {
						errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
							+ TypeCheckerVariableData.Type.INPUT_REGISTER_VECTOR, current.getPosition()));
					}

					if (inFunctionParam) {
						funcEnv.getEntry(topFunctionName.getLexeme()).addParameterType(entryData); // add paramter to function
					}

				} else {
					TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_REGISTER_VECTOR,
						vectorSize, current.getPosition());
					varEnv.addEntry(current.getLexeme(), data);

					if (inFunctionParam) {
						funcEnv.getEntry(topFunctionName.getLexeme()).addParameterType(data); // add paramter to function
					}

				}

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

			if (varEnv.entryExists(current.getLexeme())) {
				TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
					entryData.type = TypeCheckerVariableData.Type.WIRE;
				} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
					entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
				} else if (entryData.type == TypeCheckerVariableData.Type.INPUT) {
					entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
				} else {
					errorLog.addItem(new ErrorItem(
						"Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.WIRE,
						current.getPosition()));
				}

			} else {
				varEnv.addEntry(current.getLexeme(),
					new TypeCheckerVariableData(TypeCheckerVariableData.Type.WIRE, current.getPosition()));
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
		TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.getPosition()));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

			for (int i = 0; i < decl.numIdentifiers(); i++) {
				Identifier current = decl.getIdentifier(i);

				if (varEnv.entryExists(current.getLexeme())) {
					TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

					if (vectorSize != entryData.getSize()) {
						errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> "
							+ entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
					}

					if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
						entryData.type = TypeCheckerVariableData.Type.WIRE_VECTOR;
					} else if (entryData.type == TypeCheckerVariableData.Type.INPUT_VECTOR) {
						entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR;
					} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT_VECTOR) {
						entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR;
					} else {
						errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
							+ TypeCheckerVariableData.Type.WIRE_VECTOR, current.getPosition()));
					}

				} else {
					varEnv.addEntry(current.getLexeme(),
						new TypeCheckerVariableData(TypeCheckerVariableData.Type.WIRE_VECTOR, vectorSize, current.getPosition()));
				}

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

		TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.getPosition()));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

			for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this, vectorSize); }

		}

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
				TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

				if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
					entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
				} else {
					errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
						+ TypeCheckerVariableData.Type.OUTPUT_WIRE, current.getPosition()));
				}

			} else {
				varEnv.addEntry(current.getLexeme(),
					new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_WIRE, current.getPosition()));
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
		TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.getPosition()));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

			for (int i = 0; i < decl.numIdentifiers(); i++) {
				Identifier current = decl.getIdentifier(i);

				if (varEnv.entryExists(current.getLexeme())) {
					TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());

					if (vectorSize != entryData.getSize()) {
						errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> "
							+ entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
					}

					if (entryData.type == TypeCheckerVariableData.Type.UNDEFINED) {
						entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR;
					} else {
						errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type "
							+ TypeCheckerVariableData.Type.OUTPUT_VECTOR, current.getPosition()));
					}

				} else {
					varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(
						TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR, vectorSize, current.getPosition()));
				}

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

		TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); // check whether the expressions return ints
		TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

		if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {

			if (type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type1 + "]",
					decl.getPosition()));
			}

			if (type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
				errorLog.addItem(new ErrorItem(
					"Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> "
						+ type2 + "]",
					decl.getPosition()));
			}

		} else {

			ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

			int slice1 = (int)decl.getExpression1().accept(constantVisitor);
			int slice2 = (int)decl.getExpression2().accept(constantVisitor);

			int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;

			for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this, vectorSize); }

		}

		return null;
	}

	/**
	 * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Integer decl, Object... argv){

		for (int i = 0; i < decl.numRegValues(); i++) { decl.getRegValue(i).accept(this); }

		return null;
	}

	/**
	 * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(Real decl, Object... argv){

		for (int i = 0; i < decl.numIdentifiers(); i++) {
			Identifier current = decl.getIdentifier(i);

			if (varEnv.entryExists(current.getLexeme())) {
				TypeCheckerVariableData dataType = varEnv.getEntry(current.getLexeme());
				errorLog.addItem(new ErrorItem(
					"Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ",
					current.getPosition()));
			} else {
				varEnv.addEntry(current.getLexeme(),
					new TypeCheckerVariableData(TypeCheckerVariableData.Type.REAL, current.getPosition()));
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

		if (varEnv.entryExists(current.getLexeme())) {
			TypeCheckerVariableData dataType = varEnv.getEntry(current.getLexeme());
			errorLog.addItem(new ErrorItem(
				"Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ",
				current.getPosition()));
		} else {
			varEnv.addEntry(current.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.UNDEFINED, current.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any andgate declaration in verilog. Ex. integer a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(AndGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("And declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any orgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(OrGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Or declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any nandgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NandGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Nand declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any norgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NorGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Nor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any xorgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(XorGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Xor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any xnorgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(XnorGateDeclaration decl, Object... argv){

		if (decl.numExpressions() >= 3) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Xnor declaration must have atleast 3 paramteters (1 output, 2 inputs) however only "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/**
	 * This is used to visit any notgate declaration in verilog. Ex. real a, b, c ... ;
	 * 
	 * @param decl
	 */

	public Void visit(NotGateDeclaration decl, Object... argv){

		if (decl.numExpressions() == 2) {

			for (int i = 0; i < decl.numExpressions(); i++) {
				TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);

				if (type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE) {
					errorLog
						.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
				}

			}

		} else {
			errorLog.addItem(new ErrorItem("Not declaration must have exactly 2 paramteters (1 output, 1 input) however "
				+ decl.numExpressions() + " were found", decl.getPosition()));
		}

		return null;
	}

	/*
	 * Below is the code for viewing statements in the verilog language
	 */

	/**
	 * This is used to visit assignments in verilog
	 * 
	 * @param assign
	 */

	public Void visit(Assignment assign, Object... argv){
		TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
		TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);

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

		return null;
	}

	/**
	 * This is used to visit blocking assignments in verilog
	 * 
	 * @param assign
	 */

	public Void visit(BlockingAssignment assign, Object... argv){
		TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
		TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);

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

		return null;
	}

	/**
	 * This is used to visit case statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
		}

		for (int i = 0; i < stat.numCaseItems(); i++) {
			CaseItem item = stat.getCaseItem(i);

			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (int x = 0; x < exprItem.numExpressions(); x++) {
					TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER
						&& exprType != TypeCheckerVariableData.Type.CONSTANT_REAL) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.getExpression().getPosition()));
					}

				}

			}

			item.getStatement().accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit casex statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseXStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN
			|| numType == TypeCheckerVariableData.Type.REAL) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
		}

		for (int i = 0; i < stat.numCaseItems(); i++) {
			CaseItem item = stat.getCaseItem(i);

			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (int x = 0; x < exprItem.numExpressions(); x++) {
					TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.getExpression().getPosition()));
					}

				}

			}

			item.getStatement().accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit casez statements in verilog
	 * 
	 * @param assign
	 */

	public Void visit(CaseZStatement stat, Object... argv){
		TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);

		if (numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN
			|| numType == TypeCheckerVariableData.Type.REAL) {
			errorLog
				.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
		}

		for (int i = 0; i < stat.numCaseItems(); i++) {
			CaseItem item = stat.getCaseItem(i);

			if (item instanceof ExprCaseItem) {
				ExprCaseItem exprItem = (ExprCaseItem)item;

				for (int x = 0; x < exprItem.numExpressions(); x++) {
					TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);

					if (exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
						errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType,
							stat.getExpression().getPosition()));
					}

				}

			}

			item.getStatement().accept(this);
		}

		return null;
	}

	/**
	 * This is used to visit a for loop in verilog
	 * 
	 * @param forLoop
	 */

	public Void visit(ForStatement forLoop, Object... argv){
		forLoop.getInit().accept(this);
		TypeCheckerVariableData.Type type = forLoop.getExpression().accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN) {
			errorLog
				.addItem(new ErrorItem("Unexpected Expression Type for For loop" + type, forLoop.getExpression().getPosition()));
		}

		forLoop.getStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a forever loop in verilog
	 * 
	 * @param foreverLoop
	 */

	public Void visit(ForeverStatement foreverLoop, Object... argv){
		foreverLoop.getStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public Void visit(IfElseStatement ifElseStatement, Object... argv){
		TypeCheckerVariableData.Type type = ifElseStatement.getExpression().accept(this);

		if (type == TypeCheckerVariableData.Type.STRING || isArray(type)) {
			errorLog.addItem(new ErrorItem("Unexpected expression type " + type + " for if-else statement ",
				ifElseStatement.getExpression().getPosition()));
		}

		ifElseStatement.getIfStatement().accept(this);
		ifElseStatement.getElseStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a if else statement in verilog
	 * 
	 * @param ifElseStatement
	 */

	public Void visit(IfStatement ifStatement, Object... argv){
		TypeCheckerVariableData.Type type = ifStatement.getExpression().accept(this);

		if (type == TypeCheckerVariableData.Type.STRING || isArray(type)) {
			errorLog.addItem(new ErrorItem("Unexpected expression type " + type + " for if statement ",
				ifStatement.getExpression().getPosition()));
		}

		ifStatement.getStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a non blocking assignment statement in verilog
	 * 
	 * @param assign
	 */

	public Void visit(NonBlockingAssignment assign, Object... argv){
		TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
		TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);

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

		return null;
	}

	/**
	 * This is used to visit a repeat statement in verilog
	 * 
	 * @param stat
	 */

	public Void visit(RepeatStatement stat, Object... argv){
		TypeCheckerVariableData.Type type = stat.getExpression().accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, stat.getExpression().getPosition()));
		}

		stat.getStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a seq block in verilog
	 * 
	 * @param stat
	 */

	public Void visit(SeqBlockStatement stat, Object... argv){

		for (int i = 0; i < stat.numStatements(); i++) { stat.getStatement(i).accept(this); }

		return null;
	}

	/**
	 * This is used to visit a taskcall in verilog
	 * 
	 * @param stat
	 */

	public Void visit(TaskStatement task, Object... argv){
		Identifier tname = task.getTaskName();

		if (funcEnv.entryExists(tname.getLexeme())) {
			TypeCheckerFunctionData funcData = funcEnv.getEntry(tname.getLexeme());

			if (task.numExpressions() == funcData.numParameterTypes()) {

				for (int i = 0; i < task.numExpressions(); i++) {
					TypeCheckerVariableData.Type call = task.getExpression(i).accept(this);
					TypeCheckerVariableData def = funcData.getParameterType(i);

					if (def.type == TypeCheckerVariableData.Type.UNDEFINED || call == TypeCheckerVariableData.Type.UNDEFINED) {
						errorLog.addItem(new ErrorItem("Can not have undefined types as parameters [FunctionDef -> " + def.type
							+ " | FunctionCall -> " + call + "]", task.getExpression(i).getPosition()));
					}

				}

			} else {
				errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname.getLexeme() + " [Expected -> "
					+ funcData.numParameterTypes() + " | Got -> " + task.numExpressions() + " ]", tname.getPosition()));
			}

		} else {
			errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " doesnt exist", tname.getPosition()));
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
		TypeCheckerVariableData.Type type = wait.getExpression().accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, wait.getExpression().getPosition()));
		}

		wait.getStatement().accept(this);
		return null;
	}

	/**
	 * This is used to visit a while loop in verilog
	 * 
	 * @param whileLoop
	 */

	public Void visit(WhileStatement whileLoop, Object... argv){
		TypeCheckerVariableData.Type type = whileLoop.getExpression().accept(this);

		if (type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER
			&& type != TypeCheckerVariableData.Type.INTEGER) {
			errorLog.addItem(
				new ErrorItem("Unknown type " + type + " for while loop expression ", whileLoop.getExpression().getPosition()));
		}

		whileLoop.getStatement().accept(this);
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
		TypeCheckerVariableData.Type left = op.getLeftIndex().accept(this);
		TypeCheckerVariableData.Type right = op.getRightIndex().accept(this);

		if (left == TypeCheckerVariableData.Type.UNDEFINED) {
			return right;
		} else if (right == TypeCheckerVariableData.Type.UNDEFINED) {
			return left;
		} else if (left == TypeCheckerVariableData.Type.STRING || right == TypeCheckerVariableData.Type.STRING) {
			errorLog.addItem(new ErrorItem("Can't have Strings in Binary Operation " + left + " + " + right, op.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else if (isArray(left) || isArray(right)) {
			errorLog
				.addItem(new ErrorItem("Can't have Array Types in Binary Operation " + left + " + " + right, op.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {

			switch(op.getOp()){
				case PLUS:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " + " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " - " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " * " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " / " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " / " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					}
					if (isConstant(left) && isConstant(right)) {
						return TypeCheckerVariableData.Type.CONSTANT_REAL;
					} else {
						return TypeCheckerVariableData.Type.REAL;
					}
				case MOD:
					if (right == TypeCheckerVariableData.Type.BOOLEAN && left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(right) || isReal(left)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else {
						return TypeCheckerVariableData.Type.INTEGER;
					}
				case EQ2:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case EQ3:
					if (left != right) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.BOOLEAN;
					} else {
						return TypeCheckerVariableData.Type.BOOLEAN;
					}
				case NE1:
					return TypeCheckerVariableData.Type.BOOLEAN;
				case NE2:
					if (left != right) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " % " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " & " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " & " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " | " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " | " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
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
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return TypeCheckerVariableData.Type.INTEGER;
					} else if (right == TypeCheckerVariableData.Type.BOOLEAN || left == TypeCheckerVariableData.Type.BOOLEAN) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
						return (left == TypeCheckerVariableData.Type.BOOLEAN) ? right : left;
					} else if (isReal(left) || isReal(right)) {
						errorLog.addItem(new ErrorItem("Unknown operation of type " + left + " ^ " + right, op.getPosition()));
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
						new ErrorItem("Invalid operation in verilog of type " + left + " " + op.getOp() + " " + right,
							op.getPosition()));
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
		TypeCheckerVariableData.Type right = op.getRight().accept(this);

		if (right == TypeCheckerVariableData.Type.UNDEFINED) {
			errorLog
				.addItem(new ErrorItem("Cant have an undefined value in expression [Type -> " + right + "]", op.getPosition()));
		}

		if (op.getOp() == UnaryOperation.Type.PLUS
			&& (right == TypeCheckerVariableData.Type.BOOLEAN || right == TypeCheckerVariableData.Type.STRING)) {
			errorLog.addItem(new ErrorItem("Unexpected type for unary plus operation [Type -> " + right + "]", op.getPosition()));
		} else if (op.getOp() == UnaryOperation.Type.LNEG && right == TypeCheckerVariableData.Type.STRING) {
			errorLog
				.addItem(new ErrorItem("Unexpected type for Boolean Not operation [Type -> " + right + "]", op.getPosition()));
		} else if (op.getOp() == UnaryOperation.Type.BNEG && (right == TypeCheckerVariableData.Type.STRING
			|| right == TypeCheckerVariableData.Type.REAL || right == TypeCheckerVariableData.Type.BOOLEAN)) {
			errorLog
				.addItem(new ErrorItem("Unexpected type for Bitwise Not operation [Type -> " + right + "]", op.getPosition()));
		}

		return op.getRight().accept(this);
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
		TypeCheckerVariableData.Type type = expr.getExpression().accept(this);

		if (!isConstant(type)) {
			errorLog.addItem(new ErrorItem("Constant Expression must yeild constant result ", expr.getPosition()));
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

	public TypeCheckerVariableData.Type visit(FunctionCallNode call, Object... argv){
		Identifier tname = call.getFunctionName();

		if (funcEnv.entryExists(tname.getLexeme())) {
			TypeCheckerFunctionData funcData = funcEnv.getEntry(tname.getLexeme());

			if (call.numExpressions() == funcData.numParameterTypes()) {

				for (int i = 0; i < call.numExpressions(); i++) {
					TypeCheckerVariableData.Type call2 = call.getExpression(i).accept(this);
					TypeCheckerVariableData def = funcData.getParameterType(i);

					if (def.type == TypeCheckerVariableData.Type.UNDEFINED || call2 == TypeCheckerVariableData.Type.UNDEFINED) {
						errorLog.addItem(new ErrorItem(
							"Can not have undefined types as parameters [Func -> " + def.type + " | Expr -> " + call2 + "]",
							call.getExpression(i).getPosition()));
					}

				}

			} else {
				errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname.getLexeme() + " [Expected -> "
					+ funcData.numParameterTypes() + " | Got -> " + call.numExpressions() + " ]", tname.getPosition()));
			}

			return funcData.getReturnType().type;
		} else {
			errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		}

	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public TypeCheckerVariableData.Type visit(SystemFunctionCall call, Object... argv){

		if (call.getSystemFunctionName().getLexeme().equals("feof")) {
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

		if (varEnv.entryExists(ident.getLexeme())) {
			TypeCheckerVariableData entry = varEnv.getEntry(ident.getLexeme());

			if (entry.type == TypeCheckerVariableData.Type.UNDEFINED) {
				errorLog
					.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " is of Undefined type", ident.getPosition()));
			} else {
				return entry.type;
			}

			return varEnv.getEntry(ident.getLexeme()).type;
		} else {
			errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		}

	}

	/**
	 * This is the code for visiting an Number in verilog
	 * 
	 * @param number
	 */

	public TypeCheckerVariableData.Type visit(Number number, Object... argv){

		if (number.getLexeme().contains(".")) {
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
		return connection.getExpression().accept(this);
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
		TypeCheckerVariableData.Type cond = expr.getCondition().accept(this);

		if (cond != TypeCheckerVariableData.Type.BOOLEAN && cond != TypeCheckerVariableData.Type.INTEGER
			&& cond != TypeCheckerVariableData.Type.CONSTANT_INTEGER) {
			errorLog.addItem(new ErrorItem("Expected condition to result in type boolean but got " + cond, expr.getPosition()));
		}

		TypeCheckerVariableData.Type left = expr.getLeft().accept(this);
		TypeCheckerVariableData.Type right = expr.getRight().accept(this);

		if (left != right) {
			errorLog.addItem(new ErrorItem(
				"Expression types in ternary operation do not match [Left -> " + left + " | Right -> " + right + "]",
				expr.getPosition()));
		}

		return null;
	}

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public TypeCheckerVariableData.Type visit(VectorElement vector, Object... argv){
		Identifier ident = vector.getIdentifier();

		if (!varEnv.entryExists(ident.getLexeme())) {
			errorLog.addItem(new ErrorItem("Identifier " + ident.getLexeme() + " not found", ident.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());

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
				errorLog.addItem(new ErrorItem("Cant index a " + data.type, vector.getPosition()));
				return TypeCheckerVariableData.Type.UNDEFINED;
			}

		}

	}

	public TypeCheckerVariableData.Type visit(Slice vector, Object... argv){
		Identifier ident = vector.getIdentifier();

		if (!varEnv.entryExists(ident.getLexeme())) {
			errorLog.addItem(new ErrorItem("Identifier " + ident.getLexeme() + " not found", ident.getPosition()));
			return TypeCheckerVariableData.Type.UNDEFINED;
		} else {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			vector.getExpression1().accept(this);
			vector.getExpression2().accept(this);
			return data.type;
		}

	}

	/*
	 * Below are RegValue visitors. These are used in the declarations of Integers and
	 * Registers
	 */

	public Void visit(RegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR, size, ident.getPosition()));

			if (inFunctionName) { topFunctionName = ident; }

		}

		return null;
	}

	public Void visit(RegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER, ident.getPosition()));

			if (inFunctionName) { topFunctionName = ident; }

		}

		return null;
	}

	public Void visit(OutputRegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR, size, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER, ident.getPosition()));
		}

		return null;
	}

	public Void visit(IntegerIdent intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER, ident.getPosition());
			varEnv.addEntry(ident.getLexeme(), data);

			if (inFunctionName) { topFunctionName = ident; }

		}

		return null;
	}

	public Void visit(RegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY, size, ident.getPosition()));
		}

		return null;
	}

	public Void visit(RegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_ARRAY, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			int size = (int)argv[0];
			varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(
				TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY, size, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY, ident.getPosition()));
		}

		return null;
	}

	public Void visit(IntegerArrayElement intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		if (varEnv.inScope(ident.getLexeme())) {
			TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
			errorLog.addItem(
				new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(),
					ident.getPosition()));
		} else {
			varEnv.addEntry(ident.getLexeme(),
				new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER_ARRAY, ident.getPosition()));
		}

		return null;
	}
}
