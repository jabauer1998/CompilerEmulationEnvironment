package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.verilog_compiler.visitor.*;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.*;
import edu.depauw.emulator_ide.verilog_compiler.main.util.NumberUtil;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.TypeCheckerVariableData;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.TypeCheckerFunctionData;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;
    
public class TypeChecker implements ExpressionVisitor<TypeCheckerVariableData.Type>, StatementVisitor<Void>, ModuleVisitor<Void>, RegValueVisitor<Void>{
    
    private final Environment<String, Position> modEnv;
    private final Environment<String, TypeCheckerFunctionData> funcEnv;
    private final Environment<String, TypeCheckerVariableData> varEnv;
    private final InfoLog errorLog;
    
    public TypeChecker(InfoLog errorLog){
	this.modEnv = new Environment<>();
	this.funcEnv = new Environment<>();
	this.varEnv = new Environment<>();
	this.errorLog = errorLog;
    }
    
    public Void visit(ModuleDeclaration mod, Object... argv){
	modEnv.addScope();
	funcEnv.addScope();
	varEnv.addScope();
	Identifier modName = mod.getModuleName();
	if(modEnv.entryExists(modName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Redeclaration of Module " + modName.getLexeme() + "found at " + '[' + mod.getPosition() + "] declared ", modEnv.getEntry(modName.getLexeme()))); 
	} else {
	    modEnv.addEntry(modName.getLexeme(), mod.getPosition());
	}
	for(int i = 0; i < mod.numParameters(); i++){
	    mod.getParameter(i).accept(this);
	}
	for(int i = 0; i < mod.numModItems(); i++){
	    mod.getModItem(i).accept(this);
	}
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
     * @param stat
     */
    
    public Void visit(AllwaysStatement stat, Object... argv){
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public Void visit(ContinuousAssignment assign, Object... argv){
	for(int i = 0; i < assign.numAssignments(); i++){
	    assign.getAssignment(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code that is used to visit a function declaration in java
     * @param function
     */
    private boolean inFunction = false;
    private String topFunctionName = "";

    public Void visit(FunctionDeclaration function, Object... argv){
	Declaration funcDeclaration = function.getFunctionName();
	Identifier funcName = null;
	varEnv.addScope();
	funcDeclaration.accept(this);
	if(funcEnv.entryExists(funcName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Duplicate function " + funcName.getLexeme() + " allready exists ", funcName.getPosition())); 
	} else {
	    funcEnv.addEntry(funcName.getLexeme(), new TypeCheckerFunctionData(varEnv.getEntry(funcName.getLexeme()), function.getPosition()));
	}
	inFunction = true;
	topFunctionName = funcName.getLexeme();
	for(int i = 0; i < function.numDeclarations(); i++){
	    function.getDeclaration(i).accept(this);
	}
	function.getStatement().accept(this);
	varEnv.removeScope();
	inFunction = false;
	return null;
    }

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public Void visit(InitialStatement stat, Object... argv){
	varEnv.addScope();
	stat.getStatement().accept(this);
	varEnv.removeScope();
	return null;
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod, Object... argv){
	for(int i = 0; i < mod.numModInstances(); i++){
	    mod.getModInstance(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod, Object... argv){
	Identifier modName = mod.getIdentifier();
	if(!modEnv.entryExists(modName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Module with the name " + modName.getLexeme() + " not found ", modName.getPosition()));
	}
	for(int i = 0; i < mod.numExpressions(); i++){
	    mod.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(TaskDeclaration task, Object... argv){
	Identifier taskName = task.getTaskName();
	if(funcEnv.entryExists(taskName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Task declaration by the name of " + taskName.getLexeme() + " found at [" + taskName.getPosition() + "] already exists at ", funcEnv.getEntry(taskName.getLexeme()).getPosition())); 
	} else {
	    funcEnv.addEntry(taskName.getLexeme(), new TypeCheckerFunctionData(null, task.getPosition()));
	}
	inFunction = true;
	topFunctionName = taskName.getLexeme();
	varEnv.addScope();
	for(int i = 0; i < task.numDeclarations(); i++){
	    task.getDeclaration(i).accept(this);
	}
	task.getStatement().accept(this);
	varEnv.removeScope();
	inFunction = false;
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(MacroDefinition macro, Object... argv){
	return null; //this should allready be handled at this point
    }
    
    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.INPUT_WIRE, current.getPosition()));
		}
		if(inFunction){
		    funcEnv.getEntry(topFunctionName).addParameterType(entryData); //add paramter to function
		}
	    } else {
		TypeCheckerVariableData data =  new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE, current.getPosition());
		varEnv.addEntry(current.getLexeme(), data);
	    }
	}
	return null;
    }

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireVectorDeclaration decl, Object... argv){
	TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR, current.getPosition()));
		    }
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParameterType(entryData); //add paramter to function
		    }
		} else {
		   TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR, vectorSize, current.getPosition());
		    varEnv.addEntry(current.getLexeme(), data);
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParameterType(data); //add paramter to function
		    }
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.Type.WIRE;
		} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
		    entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
		} else if (entryData.type == TypeCheckerVariableData.Type.INPUT) {
		    entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.WIRE, current.getPosition()));
		}
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.WIRE, current.getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireVectorDeclaration decl, Object... argv){
	TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.WIRE_VECTOR;
		    } else if (entryData.type == TypeCheckerVariableData.Type.INPUT_VECTOR) {
			entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR;
		    } else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT_VECTOR) {
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.WIRE_VECTOR, current.getPosition()));
		    }
		    
		} else {
		    varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.WIRE_VECTOR, vectorSize, current.getPosition()));
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegVectorDeclaration decl, Object... argv){

	TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		decl.getRegValue(i).accept(this);
	    }
	}
	return null;
    }

    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_WIRE, current.getPosition()));
		}
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_WIRE, current.getPosition()));
	    }
	}
	return null;
    }

    /** This is where I will declare the output Register Scalar declaration
     * @param Jacob Bauer
     */

    public Void visit(OutputRegScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    public Void visit(OutputWireVectorDeclaration decl, Object... argv){
	TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_VECTOR, current.getPosition()));
		    }
		    
		} else {
		    varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR, vectorSize, current.getPosition()));
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputRegVectorDeclaration decl, Object... argv){

	TypeCheckerVariableData.Type type1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2().accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionEvaluator constantVisitor = new ConstantExpressionEvaluator(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		decl.getRegValue(i).accept(this);
	    }
	}
	return null;
    }
    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(IntegerDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RealDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData dataType = varEnv.getEntry(current.getLexeme());
		errorLog.addItem(new ErrorItem("Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ", current.getPosition()));
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REAL, current.getPosition()));
	    }
	}
	return null;
    }

     /**
     * This is used to visit any Unidentified declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(UnidentifiedDeclaration decl, Object... argv){
	Identifier current = decl.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    TypeCheckerVariableData dataType = varEnv.getEntry(current.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ", current.getPosition()));
	} else {
	    varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.UNDEFINED, current.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OrGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

     /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NandGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XnorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NotGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    TypeCheckerVariableData.Type type = decl.getExpression(i).accept(this);
	    if(type != TypeCheckerVariableData.Type.REGISTER && type != TypeCheckerVariableData.Type.WIRE){
		errorLog.addItem(new ErrorItem("Unknown Type for Gate Expression: " + type, decl.getExpression(i).getPosition()));
	    }
	}
	return null;
    }





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public Void visit(Assignment assign, Object... argv){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if (type1 == TypeCheckerVariableData.Type.UNDEFINED || type2 == TypeCheckerVariableData.Type.UNDEFINED){
	    errorLog.addItem(new ErrorItem("Can not have undefined types in assignment statements [Left -> " + type1 + " | Right -> " + type2 + "]", assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}  else if(type2 == TypeCheckerVariableData.Type.REAL || type2  == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign, Object... argv){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if (type1 == TypeCheckerVariableData.Type.UNDEFINED || type2 == TypeCheckerVariableData.Type.UNDEFINED){
	    errorLog.addItem(new ErrorItem("Can not have undefined types in assignment statements [Left -> " + type1 + " | Right -> " + type2 + "]", assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}  else if(type2 == TypeCheckerVariableData.Type.REAL || type2  == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat, Object... argv){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER && exprType != TypeCheckerVariableData.Type.CONSTANT_REAL){
			errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType, stat.getExpression().getPosition()));
		    }
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public Void visit(CaseXStatement stat, Object... argv){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN || numType == TypeCheckerVariableData.Type.REAL){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType, stat.getExpression().getPosition()));
		    }
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public Void visit(CaseZStatement stat, Object... argv){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN || numType == TypeCheckerVariableData.Type.REAL){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerVariableData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType, stat.getExpression().getPosition()));
		    }
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public Void visit(ForStatement forLoop, Object... argv){
	forLoop.getInit().accept(this);
	TypeCheckerVariableData.Type type = forLoop.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for For loop" + type, forLoop.getExpression().getPosition()));
	}
	forLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop, Object... argv){
	foreverLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement, Object... argv){
	TypeCheckerVariableData.Type type  = ifElseStatement.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for if Statement " + type, ifElseStatement.getExpression().getPosition()));
	}
	ifElseStatement.getIfStatement().accept(this);
	ifElseStatement.getElseStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement, Object... argv){
	TypeCheckerVariableData.Type type = ifStatement.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for if Statement" + type, ifStatement.getExpression().getPosition()));
	}
	ifStatement.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign, Object... argv){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if (type1 == TypeCheckerVariableData.Type.UNDEFINED || type2 == TypeCheckerVariableData.Type.UNDEFINED){
	    errorLog.addItem(new ErrorItem("Can not have undefined types in assignment statements [Left -> " + type1 + " | Right -> " + type2 + "]", assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}  else if(type2 == TypeCheckerVariableData.Type.REAL || type2  == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat, Object... argv){
	TypeCheckerVariableData.Type type = stat.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER && type != TypeCheckerVariableData.Type.INTEGER){
	    errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, stat.getExpression().getPosition()));
	}
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat, Object... argv){
	for(int i = 0; i < stat.numStatements(); i++){
	    stat.getStatement(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
    public Void visit(TaskStatement task, Object... argv){
	Identifier tname = task.getTaskName();
	if(funcEnv.entryExists(tname.getLexeme())){
	    TypeCheckerFunctionData funcData = funcEnv.getEntry(tname.getLexeme());
	    if(task.numExpressions() == funcData.numParameterTypes()){
		for(int i = 0; i < task.numExpressions(); i++){
		    TypeCheckerVariableData.Type call = task.getExpression(i).accept(this);
		    TypeCheckerVariableData def = funcData.getParameterType(i);

		    if(def.type == TypeCheckerVariableData.Type.UNDEFINED || call == TypeCheckerVariableData.Type.UNDEFINED){
			errorLog.addItem(new ErrorItem("Can not have undefined types as parameters [Func -> " + def.type + " | Expr -> " + call + "]", task.getExpression(i).getPosition()));
		    }
		    
		}
	    } else {
		errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname.getLexeme() + " [Expected -> " + funcData.numParameterTypes() + " | Got -> " + task.numExpressions() + " ]", tname.getPosition())); 
	    }
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition())); 
	}
	return null;
    }

    /**
     * This is used to visit a system task statement in verilog
     * @param stat
     */
     
    public Void visit(SystemTaskStatement task, Object... argv){
	// These are not important for now I will handle those later
      	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait, Object... argv){
	TypeCheckerVariableData.Type type = wait.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER && type != TypeCheckerVariableData.Type.INTEGER){
	    errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, wait.getExpression().getPosition()));
	}
        wait.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop, Object... argv){
	TypeCheckerVariableData.Type type = whileLoop.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER && type != TypeCheckerVariableData.Type.INTEGER){
	    errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, whileLoop.getExpression().getPosition()));
	}
	whileLoop.getStatement().accept(this);
	return null;
    }


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public Void visit(EmptyStatement stat, Object... argv){
	//this is empty it is just a placeholder
	return null;
    }

    
    /*
     * Below is the code that is used for visiting Expressions
     */


    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public TypeCheckerVariableData.Type visit(BinaryOperation op, Object... argv){
	TypeCheckerVariableData.Type left = op.getLeft().accept(this);
	TypeCheckerVariableData.Type right = op.getRight().accept(this);

	//Do this later
	return TypeCheckerVariableData.Type.UNDEFINED;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public TypeCheckerVariableData.Type visit(UnaryOperation op, Object... argv){
	TypeCheckerVariableData.Type right = op.getRight().accept(this);
	if(right == TypeCheckerVariableData.Type.UNDEFINED){
	   errorLog.addItem(new ErrorItem("Cant have an undefined value in expression [Type -> " + right + "]", op.getPosition()));
	} if(op.getOp().getOpType() == UnOp.Type.PLUS && (right == TypeCheckerVariableData.Type.BOOLEAN || right == TypeCheckerVariableData.Type.STRING)){
	   errorLog.addItem(new ErrorItem("Unexpected type for unary plus operation [Type -> " + right + "]", op.getPosition()));
	} else if(op.getOp().getOpType() == UnOp.Type.LNEG && right == TypeCheckerVariableData.Type.STRING){
	   errorLog.addItem(new ErrorItem("Unexpected type for unary Boolean Not operation [Type -> " + right + "]", op.getPosition()));
	} else if(op.getOp().getOpType() == UnOp.Type.BNEG && (right == TypeCheckerVariableData.Type.STRING || right == TypeCheckerVariableData.Type.REAL) || right == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Unexpected type for Bitwise Not operation [Type -> " + right + "]", op.getPosition()));
	}
	return op.getRight().accept(this);
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public TypeCheckerVariableData.Type visit(Concatenation concat, Object... argv){
	return TypeCheckerVariableData.Type.MIXED_VECTOR;
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(ConstantExpression expr, Object... argv){
	return expr.getExpression().accept(this);
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(EmptyExpression  expr, Object... argv){
	//this is just a placeholder we do not need to put anything here
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public TypeCheckerVariableData.Type visit(FunctionCall call, Object... argv){
	Identifier tname = call.getFunctionName();
	if(funcEnv.entryExists(tname.getLexeme())){
	    TypeCheckerFunctionData funcData = funcEnv.getEntry(tname.getLexeme());
	    if(call.numExpressions() == funcData.numParameterTypes()){
		for(int i = 0; i < call.numExpressions(); i++){
		    TypeCheckerVariableData.Type call2 = call.getExpression(i).accept(this);
		    TypeCheckerVariableData def = funcData.getParameterType(i);

		    if(def.type == TypeCheckerVariableData.Type.UNDEFINED || call2 == TypeCheckerVariableData.Type.UNDEFINED){
			errorLog.addItem(new ErrorItem("Can not have undefined types as parameters [Func -> " + def.type + " | Expr -> " + call2 + "]", call.getExpression(i).getPosition()));
		    }
		    
		}
	    } else {
		errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname.getLexeme() + " [Expected -> " + funcData.numParameterTypes() + " | Got -> " + call.numExpressions() + " ]", tname.getPosition()));
	    }
	    return funcData.getReturnType().type;
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition()));
	    return TypeCheckerVariableData.Type.UNDEFINED;
	}
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public TypeCheckerVariableData.Type visit(SystemFunctionCall call, Object... argv){
	//I will do this later
	return TypeCheckerVariableData.Type.UNDEFINED;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public TypeCheckerVariableData.Type visit(Identifier ident, Object... argv){
	if(varEnv.entryExists(ident.getLexeme())){
	    TypeCheckerVariableData entry = varEnv.getEntry(ident.getLexeme());
	    if(entry.type == TypeCheckerVariableData.Type.UNDEFINED){
		errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " is of Undefined type", ident.getPosition()));
	    } else {
		return entry.type;
	    }
	    return varEnv.getEntry(ident.getLexeme()).type;
	} else {
	    errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition()));
	    return TypeCheckerVariableData.Type.UNDEFINED;
	}
    }

    public TypeCheckerVariableData.Type visit(MacroIdentifier ident, Object... argv){
	return null; //the preprocessor should have allready done its pass before this so this shoudnt exist
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public TypeCheckerVariableData.Type visit(NumValue number, Object... argv){
	if(number.getLexeme().contains(".")){
	    return TypeCheckerVariableData.Type.CONSTANT_REAL;
	} else {
	    return TypeCheckerVariableData.Type.CONSTANT_INTEGER;
	}
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public TypeCheckerVariableData.Type visit(PortConnection connection, Object... argv){
	return connection.getExpression().accept(this);
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public TypeCheckerVariableData.Type visit(StrValue string, Object... argv){
	// do nothing
	return TypeCheckerVariableData.Type.STRING;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(TernaryExpression expr, Object... argv){
	TypeCheckerVariableData.Type cond = expr.getCondition().accept(this);
	if(cond != TypeCheckerVariableData.Type.BOOLEAN && cond != TypeCheckerVariableData.Type.INTEGER && cond != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Expected condition to result in type boolean but got " + cond, expr.getPosition()));
	}
	TypeCheckerVariableData.Type left = expr.getLeft().accept(this);
	TypeCheckerVariableData.Type right = expr.getRight().accept(this);
	if(left != right){
	    errorLog.addItem(new ErrorItem("Expression types in ternary operation do not match [Left -> " + left + " | Right -> " + right + "]", expr.getPosition()));
	}
	return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public TypeCheckerVariableData.Type visit(VectorCall vector, Object... argv){
	Identifier ident = vector.getIdentifier();
	if(!varEnv.entryExists(ident.getLexeme())){
	    errorLog.addItem(new ErrorItem("Identifier " + ident.getLexeme() + " not found", ident.getPosition()));
	    return TypeCheckerVariableData.Type.UNDEFINED;
	} else {
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    if(data.type == TypeCheckerVariableData.Type.INTEGER_ARRAY){
		return TypeCheckerVariableData.Type.INTEGER;
	    } else if(data.type == TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY){
		return TypeCheckerVariableData.Type.REGISTER_VECTOR;
	    } else if(data.type == TypeCheckerVariableData.Type.REGISTER_ARRAY){
		return TypeCheckerVariableData.Type.REGISTER;
	    } else if(data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY){
		return TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR;
	    } else if(data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY){
		return TypeCheckerVariableData.Type.OUTPUT_REGISTER;
	    } else if(data.type == TypeCheckerVariableData.Type.REGISTER_VECTOR){
		return TypeCheckerVariableData.Type.REGISTER;
	    } else if(data.type == TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR){
		return TypeCheckerVariableData.Type.OUTPUT_REGISTER;
	    } else if(data.type == TypeCheckerVariableData.Type.OUTPUT_WIRE_VECTOR){
		return TypeCheckerVariableData.Type.OUTPUT_WIRE;
	    } else if(data.type == TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR){
		return TypeCheckerVariableData.Type.OUTPUT_WIRE;
	    } else if(data.type == TypeCheckerVariableData.Type.WIRE_VECTOR){
		return TypeCheckerVariableData.Type.WIRE;
	    } else if(data.type == TypeCheckerVariableData.Type.INPUT_VECTOR){
		return TypeCheckerVariableData.Type.INPUT;
	    } else if(data.type == TypeCheckerVariableData.Type.OUTPUT_VECTOR){
		return TypeCheckerVariableData.Type.OUTPUT;
	    } else {
		errorLog.addItem(new ErrorItem("Cant index a " + data.type, vector.getPosition()));
		return TypeCheckerVariableData.Type.UNDEFINED;
	    }
	}
    }

    public TypeCheckerVariableData.Type visit(VectorSlice vector, Object... argv){
	Identifier ident = vector.getIdentifier();
	if(!varEnv.entryExists(ident.getLexeme())){
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
     *Below are RegValue visitors. These are used in the declarations of Integers and Registers
     */

    public Void visit(RegVectorIdent regVector, Object... argv){
	Identifier ident = regVector.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    int size = (int)argv[0];
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR, size, ident.getPosition()));
	}
	return null;
    }

    public Void visit(RegScalarIdent regScalar, Object... argv){
	Identifier ident = regScalar.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    int size = (int)argv[0];
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER, size, ident.getPosition()));
	}
	return null;
    }

    public Void visit(IntegerIdent intIdent, Object... argv){
	Identifier ident = intIdent.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER, ident.getPosition()));
	}
	return null;
    }

    public Void visit(RegVectorArray regVector, Object... argv){
	Identifier ident = regVector.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    int size = (int)argv[0];
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY, size, ident.getPosition()));
	}
	return null;
    }

    public Void visit(RegScalarArray regScalar, Object... argv){
	Identifier ident = regScalar.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    int size = (int)argv[0];
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_ARRAY, size, ident.getPosition()));
	}
	return null;
    }

    public Void visit(IntegerArray intIdent, Object... argv){
	Identifier ident = intIdent.getIdentifier();
	
	if(varEnv.inScope(ident.getLexeme())){
	    TypeCheckerVariableData data = varEnv.getEntry(ident.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable by the name of " + ident.getLexeme() + " allready exists at " + data.getPosition(), ident.getPosition()));
	} else {
	    varEnv.addEntry(ident.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER_ARRAY, ident.getPosition()));
	}
	return null;
    }
}
