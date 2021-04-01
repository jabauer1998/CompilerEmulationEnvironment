package edu.depauw.emulator_ide.verilog_compiler.visitor;

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
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;
    
public class TypeCheckerVisitor implements AstNodeVisitor<Void, Void, TypeCheckerVariableData.Type>{
    
    private Environment<String, Position> modEnv;
    private Environment<String, TypeCheckerFunctionData> funcEnv;
    private Environment<String, TypeCheckerVariableData> varEnv;
    private AstNode node;
    private InfoLog errorLog;
    
    public TypeCheckerVisitor(AstNode node, InfoLog errorLog){
	this.modEnv = new Environment<>();
	this.funcEnv = new Environment<>();
	this.varEnv = new Environment<>();
	this.errorLog = errorLog;
	this.node = node;
    }
    
    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod
     *@author Jacob bauer
     */

    public void visitRoot(){
	if(node instanceof Expression){
	    ((Expression)node).accept(this);
	} else if(node instanceof Statement){
	    ((Statement)node).accept(this);
	} else {
	    visit((ModuleDeclaration)node);
	}
	this.errorLog.printLog();
    }

    
    public void visit(ModuleDeclaration mod){
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
    }

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public Void visit(AllwaysStatement stat){
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public Void visit(ContinuousAssignment assign){
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

    public Void visit(FunctionDeclaration function){
	Declaration funcDeclaration = function.getFunctionName();
	Identifier funcName;
	varEnv.addScope();
	if(funcDeclaration instanceof IntegerDeclaration){
	    IntegerDeclaration intDec = (IntegerDeclaration)funcDeclaration;
	    funcName = intDec.getIdentifier(0);
	} else if (funcDeclaration instanceof RealDeclaration){
	    RealDeclaration realDec = (RealDeclaration)funcDeclaration;
	    funcName = realDec.getIdentifier(0);
	} else if (funcDeclaration instanceof RegVectorDeclaration){
	    RegVectorDeclaration regVecDec = (RegVectorDeclaration)funcDeclaration;
	    Expression regVal = regVecDec.getRegVal(0);
	    funcName = regVal.getIdentifier(); 
	} else if (funcDeclaration instanceof RegScalarDeclaration){
	    RegScalarDeclaration regScalDec = (RegScalarDeclaration)funcDeclaration;
	    Expression regVal = regScalDec.getRegVal(0);
	    funcName = regVal.getIdentifier(); 
	}
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
    
    public Void visit(InitialStatement stat){
	varEnv.addScope();
	stat.getStatement().accept(this);
	varEnv.removeScope();
	return null;
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod){
	for(int i = 0; i < mod.numModInstances(); i++){
	    mod.getModInstance(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod){
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
    
    public Void visit(TaskDeclaration task){
	Identifier taskName = task.getTaskName();
	funcEnv.addEntry(taskName.getLexeme(), new TypeCheckerFunctionData(null, task.getPosition()));
	if(funcEnv.entryExists(taskName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Task declaration by the name of " + taskName.getLexeme() + " found at [" + taskName.getPosition() + "] already exists at ", funcEnv.getEntry(taskName.getLexeme()))); 
	} else {
	    funcEnv.addEntry(taskName.getLexeme(), taskName.getPosition());
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
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.Type.INPUT;
		} else if (entryData.type == TypeCheckerVariableData.Type.WIRE) {
		    entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.INPUT, current.getPosition()));
		}
		if(inFunction){
		    funcEnv.getEntry(topFunctionName).addParamaterType(entryData); //add paramter to function
		}
	    } else {
		TypeCheckerVariableData data =  new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.INPUT, current.getPosition());
		varEnv.addEntry(current.getLexeme(), data);
	    }
	}
	return null;
    }

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.Type.INPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.INPUT_WIRE, current.getPosition()));
		}
		if(inFunction){
		    funcEnv.getEntry(topFunctionName).addParamaterType(entryData); //add paramter to function
		}
	    } else {
		TypeCheckerVariableData data =  new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.INPUT_WIRE, current.getPosition());
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
    
    public Void visit(InputWireVectorDeclaration decl){
	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.INTEGER || type2 != TypeCheckerVariableData.Type.INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.INPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.INPUT_WIRE_VECTOR, current.getPosition()));
		    }
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParamaterType(entryData); //add paramter to function
		    }
		} else {
		   TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_WIRE_VECTOR, vectorSize, current.getPosition());
		    varEnv.addEntry(current.getLexeme(), data);
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParamaterType(data); //add paramter to function
		    }
		}
	    }
	}
	return null;
    }


    public Void visit(InputVectorDeclaration decl){
	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.INTEGER || type2 != TypeCheckerVariableData.Type.INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.INPUT_VECTOR;
		    } else if (entryData.type == TypeCheckerVariableData.Type.WIRE_VECTOR) {
			entryData.type = TypeCheckerVariableData.TYPE.INPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.INPUT_VECTOR, current.getPosition()));
		    }
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParamaterType(entryData); //add paramter to function
		    }
		} else {
		   TypeCheckerVariableData data = new TypeCheckerVariableData(TypeCheckerVariableData.Type.INPUT_VECTOR, vectorSize, current.getPosition());
		    varEnv.addEntry(current.getLexeme(), data);
		    if(inFunction){
			funcEnv.getEntry(topFunctionName).addParamaterType(data); //add paramter to function
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
    
    public Void visit(WireScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.TYPE.WIRE;
		} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
		    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE;
		} else if (entryData.type == TypeCheckerVariableData.Type.INPUT) {
		    entryData.type = TypeCheckerVariableData.TYPE.INPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.WIRE, current.getPosition()));
		}
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.WIRE, current.getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireVectorDeclaration decl){
	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.WIRE_VECTOR;
		    } else if (entryData.type == TypeCheckerVariableData.Type.INPUT_VECTOR) {
			entryData.type = TypeCheckerVariableData.TYPE.INPUT_WIRE_VECTOR;
		    } else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT_VECTOR) {
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.WIRE_VECTOR, current.getPosition()));
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
    
    public Void visit(RegScalarDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    Expression current = decl.getRegValue(i);
	    if(current instanceof Identifier){
		Identifier cur = (Identifier)current;
		if(varEnv.entryExists(cur.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.REGISTER;
		    } else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_REGISTER;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.REGISTER, cur.getPosition()));
		}
	    } else {
		VectorCall cur = (VectorCall)current;
		String lexeme = cur.getIdentifier().getLexeme();

		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression2(), errorLog);
		    }
		}
		
		if(varEnv.entryExists(lexeme)){
		    TypeCheckerVariableData entryData = varEnv.getEntry(lexeme);
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.REGISTER_ARRAY;
		    } else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_ARRAY, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_ARRAY, vectorSize, cur.getPosition()));
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
    
    public Void visit(RegVectorDeclaration decl){

	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		Expression current = decl.getRegValue(i);
		Identifier cur = null;
		if(current instanceof Identifier){
		    cur = (Identifier)current;
		    Identifier cur = (Identifier)current;
		    if(varEnv.entryExists(cur.getLexeme())){
			TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.REGISTER_VECTOR;
			} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_REGISTER_VECTOR;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_VECTOR, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.REGISTER_VECTOR, vectorSize,  ur.getPosition()));
		    }
		} else {
		    cur = ((VectorCall)current).getIdentifier();
		    String lexeme = cur.getIdentifier().getLexeme();
		    if(cur.getExpression2() == null){
			TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
			}
		    } else {
			TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
			TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
			}
			if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression2(), errorLog);
			}
		    }

		    if(varEnv.entryExists(lexeme)){
			TypeCheckerVariableData entryData = varEnv.getEntry(lexeme);
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY;
			} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			    entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR_ARRAY, vectorSize, cur.getPosition()));
		    } 
		}
	    }
	}
	return null;
    }


    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData.Type entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT;
		} else if (entryData.type == TypeCheckerVariableData.Type.WIRE) {
		    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE;
		} else if (entryData.type == TypeCheckerVariableData.Type.REGISTER) {
		    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_REGISTER;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.OUTPUT, current.getPosition()));
		}
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.OUTPUT, current.getPosition()));
	    }
	}
	return null;
    }

    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData.Type entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
		    entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE;
		} else {
		    errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.OUTPUT_WIRE, current.getPosition()));
		}
	    } else {
		varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.OUTPUT_WIRE, current.getPosition()));
	    }
	}
	return null;
    }

    /** This is where I will declare the output Register Scalar declaration
     * @param Jacob Bauer
     */

    public Void visit(OutputRegScalarDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    Expression current = decl.getRegValue(i);
	    if(current instanceof Identifier){
		Identifier cur = (Identifier)current;
		if(varEnv.entryExists(cur.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_REGISTER;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_REGISTER, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.TYPE.OUTPUT_REGISTER, cur.getPosition()));
		}
	    } else {
		VectorCall cur = (VectorCall)current;
		String lexeme = cur.getIdentifier().getLexeme();

		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression2(), errorLog);
		    }
		}
		
		if(varEnv.entryExists(lexeme)){
		    TypeCheckerVariableData entryData = varEnv.getEntry(lexeme);
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_ARRAY, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY, vectorSize, cur.getPosition()));
		}
		
	    }
	}
	return null;
    }

    /**
     * This is used to visit any output vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputVectorDeclaration decl){
	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData.Type entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_VECTOR;
		    } else if(entryData.type == TypeCheckerVariableData.Type.WIRE_VECTOR) {
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE_VECTOR;
		    } else if(entryData.type == TypeCheckerVariableData.Type.REGISTER_VECTOR) {
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_REGISTER_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.OUTPUT_VECTOR, current.getPosition()));
		    }
		    
		} else {
		    varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_VECTOR, vectorSize, current.getPosition()));
		}
	    }
	}
	return null;
    }

    

    public Void visit(OutputWireVectorDeclaration decl){
	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numIdentifiers(); i++){
		Identifier current = decl.getIdentifier(i);
		if(varEnv.entryExists(current.getLexeme())){
		    TypeCheckerVariableData.Type entryData = varEnv.getEntry(current.getLexeme());
		    
		    if(vectorSize != entryData.getSize()){
			errorLog.addItem(new ErrorItem("Size mismatch with variable " + current.getLexeme() + "[Expected -> " + entryData.getSize() + " | Got -> " + vectorSize + "]", decl.getPosition()));
		    }
		    
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.TYPE.OUTPUT_WIRE_VECTOR;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.TYPE.OUTPUT_VECTOR, current.getPosition()));
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
    
    public Void visit(OutputRegVectorDeclaration decl){

	TypeCheckerVariableData.Type type1 = decl.getExpression1.accept(this); //check whether the expressions return ints
	TypeCheckerVariableData.Type type2 = decl.getExpression2.accept(this);

	if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER || type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){   
	    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", decl.getPosition()));
	    }
	    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
		errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", decl.getPosition()));
	    }
	} else {

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1.accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2.accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		Expression current = decl.getRegValue(i);
		Identifier cur = null;
		if(current instanceof Identifier){
		    cur = (Identifier)current;
		    Identifier cur = (Identifier)current;
		    if(varEnv.entryExists(cur.getLexeme())){
			TypeCheckerVariableData entryData = varEnv.getEntry(current.getLexeme());
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_VECTOR, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR, vectorSize,  ur.getPosition()));
		    }
		} else {
		    cur = ((VectorCall)current).getIdentifier();
		    String lexeme = cur.getIdentifier().getLexeme();
		    if(cur.getExpression2() == null){
			TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
			}
		    } else {
			TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
			TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
			}
			if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
			} else {
			    ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression2(), errorLog);
			}
		    }

		    if(varEnv.entryExists(lexeme)){
			TypeCheckerVariableData entryData = varEnv.getEntry(lexeme);
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR_ARRAY, vectorSize, cur.getPosition()));
		    } 
		}
	    }
	}
	return null;
    }
    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(IntegerDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    Expression current = decl.getRegValue(i);
	    if(current instanceof Identifier){
		Identifier cur = (Identifier)current;
		if(varEnv.entryExists(cur.getLexeme())){
		    errorLog.addItem(new ErrorItem("Redeclaration of Integer " + cur.getLexeme() + " originally declared at " + varEnv.getEntry(cur.getLexeme()).getPosition() + " declared at ", decl.getPosition()));
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER, cur.getPosition()));
		}
	    } else {
		VectorCall cur = (VectorCall)current;
		String lexeme = cur.getIdentifier().getLexeme();

		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression1(), errorLog);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(cur.getExpression2(), errorLog);
		    }
		}
		
		if(varEnv.entryExists(lexeme)){
		    errorLog.addItem(new ErrorItem("Redeclaration of variable " + lexeme + " originally declared at " + varEnv.getEntry(lexeme).getPosition() + " declared at ", decl.getPosition()));
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER_ARRAY, cur.getPosition()));
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RealDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		TypeCheckerVariableData.Type dataType = varEnv.getEntry(current.getLexeme());
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
    
    public Void visit(UnidentifiedDeclaration decl){
	Identifier current = decl.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    TypeCheckerVariableData.Type dataType = varEnv.getEntry(current.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ", current.getPosition()));
	} else {
	    varEnv.addEntry(current.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.UNIDENTIFIED, current.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl){
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
    
    public Void visit(OrGateDeclaration decl){
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
    
    public Void visit(NandGateDeclaration decl){
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
    
    public Void visit(NorGateDeclaration decl){
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
    
    public Void visit(XorGateDeclaration decl){
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
    
    public Void visit(XnorGateDeclaration decl){
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
    
    public Void visit(NotGateDeclaration decl){
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
    
    public Void visit(Assignment assign){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 != TypeCheckerVariableData.Type.INTEGER && type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type2 == TypeCheckerVariableData.Type.REAL || TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 != TypeCheckerVariableData.Type.INTEGER && type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type2 == TypeCheckerVariableData.Type.REAL || TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + numType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerData.Type.CONSTANT_INTEGER && exprType != TypeCheckerData.Type.CONSTANT_REAL){
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
    
    public Void visit(CaseXStatement stat){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN || numType == TypeCheckerVariableData.Type.REAL){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerData.Type.CONSTANT_INTEGER){
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
    
    public Void visit(CaseZStatement stat){
	TypeCheckerVariableData.Type numType = stat.getExpression().accept(this);
	if(numType == TypeCheckerVariableData.Type.STRING || numType == TypeCheckerVariableData.Type.BOOLEAN || numType == TypeCheckerVariableData.Type.REAL){
	    errorLog.addItem(new ErrorItem("Unexpected Type for switch statement " + exprType, stat.getExpression().getPosition()));
	}
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    TypeCheckerData.Type exprType = exprItem.getExpression(x).accept(this);
		    if(exprType != TypeCheckerData.Type.CONSTANT_INTEGER){
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
    
    public Void visit(ForStatement forLoop){
	forLoop.getInit().accept(this);
	TypeCheckerVariableData.Type type = forLoop.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for For loop" + exprType, forLoop.getExpression().getPosition()));
	}
	forLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop){
	foreverLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement){
	TypeCheckerVariableData.Type type  = ifElseStatement.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for if Statement" + exprType, forLoop.getExpression().getPosition()));
	}
	ifElseStatement.getIfStatement().accept(this);
	ifElseStatement.getElseStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement){
	TypeCheckerVariableData.Type type = ifStatement.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN || type != TypeCheckerVariableData.Type.INTEGER || type != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
	    errorLog.addItem(new ErrorItem("Unexpected Expression Type for if Statement" + exprType, forLoop.getExpression().getPosition()));
	}
	ifStatement.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign){
	TypeCheckerVariableData.Type type1 = assign.getLValue().accept(this);
	TypeCheckerVariableData.Type type2 = assign.getExpression().accept(this);
	if(type1 == TypeCheckerVariableData.Type.INTEGER && (type2 != TypeCheckerVariableData.Type.INTEGER && type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type1 == TypeCheckerVariableData.Type.REAL && (type2 == TypeCheckerVariableData.Type.BOOLEAN || type2 == TypeCheckerVariableData.Type.STRING)){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	} else if(type2 == TypeCheckerVariableData.Type.REAL || TypeCheckerVariableData.Type.BOOLEAN){
	    errorLog.addItem(new ErrorItem("Type mismatch in assignment between " + type1 + " and " + type2, assign.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat){
	TypeCheckerVariableData.Type type = stat.getExpression().accept(this);
	if(type != TypeCheckerVariableData.Type.BOOLEAN && type != TypeCheckerVariableData.Type.CONSTANT_INTEGER && type != TypeCheckerVariableData.Type.INTEGER){
	    errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + type, whileLoop.getExpression().getPosition()));
	}
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat){
	for(int i = 0; i < stat.numStatements(); i++){
	    stat.getStatement(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
    public Void visit(TaskStatement task){
	Identifier tname = task.getTaskName();
	if(funcEnv.entryExists(tname.getLexeme())){
	    TypeCheckerFunctionData funcData = funcEnv.getExntry(tname.getLexeme());
	    if(task.numExpressions() == funcData.numParamaterType()){
		for(int i = 0; i < task.numExpressions(); i++){
		    TypeCheckerVariableData call = task.getExpression(i).accept(this);
		    TypeCheckerVariableData type = funcData.getParameterType(i);
		}
	    } else {
		errorLog.addItem(new ErrorItem("Argument amount mismatch with " + tname.getLexeme() + " [Expected -> " + funcData.numParameterType() + " | Got -> " + task.numExpressions() + " ]", tname.getPosition())); 
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
     
    public Void visit(SystemTaskStatement task){
	Identifier tname = task.getTaskName();
	if(tname.getLexeme()){
	    
	}
      	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait){
	wait.getExpression().accept(this);
        wait.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop){
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
    
    public Void visit(EmptyStatement stat){
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
    
    public TypeCheckerVariableData.Type visit(BinaryOperation op){
	op.getLeft().accept(this);
	op.getRight().accept(this);
	return null;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public TypeCheckerVariableData.Type visit(UnaryOperation op){
	op.getRight().accept(this);
	return null;
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public TypeCheckerVariableData.Type visit(Concatenation concat){
	for(int i = 0; i < concat.numExpressions(); i++){
	    concat.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(ConstantExpression expr){
	return expr.getExpression().accept(this);
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(EmptyExpression  expr){
	//this is just a placeholder we do not need to put anything here
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public TypeCheckerVariableData.Type visit(FunctionCall call){
	Identifier fname = call.getFunctionName();
	
	if(funcEnv.entryExists(fname.getLexeme())){
	    dest.println("USE FUNCTION " + fname.getLexeme() + " AT [" + fname.getPosition() + "] DECLARED AT [" + funcEnv.getEntry(fname.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + fname.getLexeme() + " Doesnt Exist", fname.getPosition())); 
	}

	for(int i = 0; i < call.numExpressions(); i++){
	    call.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public TypeCheckerVariableData.Type visit(SystemFunctionCall call){
	for(int i = 0; i < call.numExpressions(); i++){
	    call.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public TypeCheckerVariableData.Type visit(Identifier ident){
	if(varEnv.entryExists(ident.getLexeme())){
	    dest.println("USE VARIABLE " + ident.getLexeme() + " AT [" + ident.getPosition() + "] DECLARED AT [" + varEnv.getEntry(ident.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	}
	return null;
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public TypeCheckerVariableData.Type visit(NumValue number){
	// do nothing
	return null;
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public TypeCheckerVariableData.Type visit(PortConnection connection){
	connection.getExpression().accept(this);
	return null;
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public TypeCheckerVariableData.Type visit(StrValue string){
	// do nothing
	return null;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(TernaryExpression expr){
	expr.getCondition().accept(this);
	expr.getLeft().accept(this);
	expr.getRight().accept(this);
	return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public TypeCheckerVariableData.Type visit(VectorCall vector){
	Identifier ident = vector.getIdentifier();
	if(varEnv.entryExists(ident.getLexeme())){
	    dest.println("USE VECTOR " + ident.getLexeme() + " AT [" + ident.getPosition() + "] DECLARED AT [" + varEnv.getEntry(ident.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Vector Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	}
	vector.getExpression1().accept(this);
	vector.getExpression2().accept(this);
	return null;
    }
}
