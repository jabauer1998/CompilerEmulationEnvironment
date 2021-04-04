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
	Identifier funcName = null;
	varEnv.addScope();
	if(funcDeclaration instanceof IntegerDeclaration){
	    IntegerDeclaration intDec = (IntegerDeclaration)funcDeclaration;
	    Expression regVal = intDec.getRegValue(0);
	    if(regVal instanceof Identifier){
		funcName = (Identifier)regVal; 
	    } else {
		funcName = ((VectorCall)regVal).getIdentifier();
	    }
	} else if (funcDeclaration instanceof RealDeclaration){
	    RealDeclaration realDec = (RealDeclaration)funcDeclaration;
	    funcName = realDec.getIdentifier(0);
	} else if (funcDeclaration instanceof RegVectorDeclaration){
	    RegVectorDeclaration regVecDec = (RegVectorDeclaration)funcDeclaration;
	    Expression regVal = regVecDec.getRegValue(0);
	    funcName = (Identifier)regVal; 
	} else if (funcDeclaration instanceof RegScalarDeclaration){
	    RegScalarDeclaration regScalDec = (RegScalarDeclaration)funcDeclaration;
	    Expression regVal = regScalDec.getRegValue(0);
	    if(regVal instanceof Identifier){
		funcName = (Identifier)regVal; 
	    } else {
		funcName = ((VectorCall)regVal).getIdentifier();
	    }
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
    
    public Void visit(InputWireVectorDeclaration decl){
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

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

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
    
    public Void visit(WireScalarDeclaration decl){
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
    
    public Void visit(WireVectorDeclaration decl){
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

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

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
    
    public Void visit(RegScalarDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    Expression current = decl.getRegValue(i);
	    if(current instanceof Identifier){
		Identifier cur = (Identifier)current;
		if(varEnv.entryExists(cur.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(cur.getLexeme());
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.REGISTER;
		    } else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER, cur.getPosition()));
		}
	    } else {
		VectorCall cur = (VectorCall)current;
		String lexeme = cur.getIdentifier().getLexeme();

		
		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(errorLog);
			cur.getExpression1().accept(constVisitor);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(errorLog);
			cur.getExpression1().accept(constVisitor);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(errorLog);
			cur.getExpression2().accept(constVisitor);
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
		    varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_ARRAY, cur.getPosition()));
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

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		Expression current = decl.getRegValue(i);
		Identifier cur = null;
		if(current instanceof Identifier){
		    cur = (Identifier)current;
		    if(varEnv.entryExists(cur.getLexeme())){
			TypeCheckerVariableData entryData = varEnv.getEntry(cur.getLexeme());
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.REGISTER_VECTOR;
			} else if (entryData.type == TypeCheckerVariableData.Type.OUTPUT) {
			    entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_VECTOR, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.REGISTER_VECTOR, vectorSize,  cur.getPosition()));
		    }
		} else {
		    VectorCall vecCall = (VectorCall)current;
		    cur = vecCall.getIdentifier();
		    String lexeme = cur.getLexeme();
		    if(vecCall.getExpression2() == null){
			TypeCheckerVariableData.Type type1Arr = vecCall.getExpression1().accept(this);
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression1().accept(constantVisitor);
			}
		    } else {
			TypeCheckerVariableData.Type type1Arr = vecCall.getExpression1().accept(this);
			TypeCheckerVariableData.Type type2Arr = vecCall.getExpression2().accept(this);
		    
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1Arr + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression1().accept(constantVisitor);
			}
			if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2Arr + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression2().accept(constantVisitor);
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
    
    public Void visit(OutputWireScalarDeclaration decl){
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

    public Void visit(OutputRegScalarDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    Expression current = decl.getRegValue(i);
	    if(current instanceof Identifier){
		Identifier cur = (Identifier)current;
		if(varEnv.entryExists(cur.getLexeme())){
		    TypeCheckerVariableData entryData = varEnv.getEntry(cur.getLexeme());
		    if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER;
		    } else {
			errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.OUTPUT_REGISTER, cur.getPosition()));
		    }
		} else {
		    varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER, cur.getPosition()));
		}
	    } else {
		VectorCall cur = (VectorCall)current;
		String lexeme = cur.getIdentifier().getLexeme();

		ConstantExpressionVisitor constVisitor = new ConstantExpressionVisitor(errorLog);

		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			cur.getExpression1().accept(constVisitor);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			cur.getExpression1().accept(constVisitor);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			cur.getExpression2().accept(constVisitor);
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
		    varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_ARRAY, cur.getPosition()));
		}
		
	    }
	}
	return null;
    }

    public Void visit(OutputWireVectorDeclaration decl){
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

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

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
    
    public Void visit(OutputRegVectorDeclaration decl){

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

	    ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

	    int slice1 = (int)decl.getExpression1().accept(constantVisitor);
	    int slice2 = (int)decl.getExpression2().accept(constantVisitor);

	    int vectorSize = (slice1 > slice2) ? slice1 - slice2 + 1 : slice2 - slice1 + 1;
	    
	    for(int i = 0; i < decl.numRegValues(); i++){
		Expression current = decl.getRegValue(i);
		Identifier cur = null;
		if(current instanceof Identifier){
		    cur = (Identifier)current;
		    if(varEnv.entryExists(cur.getLexeme())){
			TypeCheckerVariableData entryData = varEnv.getEntry(cur.getLexeme());
			if(entryData.type == TypeCheckerVariableData.Type.UNDEFINED){
			    entryData.type = TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR;
			} else {
			    errorLog.addItem(new ErrorItem("Cannot reassign variable of type " + entryData.type + " to type " + TypeCheckerVariableData.Type.REGISTER_VECTOR, cur.getPosition()));
			}
		    } else {
			varEnv.addEntry(cur.getLexeme(), new TypeCheckerVariableData(TypeCheckerVariableData.Type.OUTPUT_REGISTER_VECTOR, vectorSize,  cur.getPosition()));
		    }
		} else {
		    VectorCall vecCall = (VectorCall)current;
		    cur = vecCall.getIdentifier();
		    
		    String lexeme = cur.getLexeme();
		    if(vecCall.getExpression2() == null){
			TypeCheckerVariableData.Type type1Call = vecCall.getExpression1().accept(this);
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1Call + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression1().accept(constantVisitor);
			}
		    } else {
			TypeCheckerVariableData.Type type1Call = vecCall.getExpression1().accept(this);
			TypeCheckerVariableData.Type type2Call = vecCall.getExpression2().accept(this);
		    
			if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1Call + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression1().accept(constantVisitor);
			}
			if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			    errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2Call + "]", cur.getPosition()));
			} else {
			    vecCall.getExpression2().accept(constantVisitor);
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

		ConstantExpressionVisitor constantVisitor = new ConstantExpressionVisitor(errorLog);

		if(cur.getExpression2() == null){
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			cur.getExpression1().accept(constantVisitor);
		    }
		} else {
		    TypeCheckerVariableData.Type type1 = cur.getExpression1().accept(this);
		    TypeCheckerVariableData.Type type2 = cur.getExpression2().accept(this);
		    
		    if(type1 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 1 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type1 + "]", cur.getPosition()));
		    } else {
			cur.getExpression1().accept(constantVisitor);
		    }
		    if(type2 != TypeCheckerVariableData.Type.CONSTANT_INTEGER){
			errorLog.addItem(new ErrorItem("Slicing expression 2 in Input Declaration must result in an integer type[Exprected -> INTEGER | Got -> " + type2 + "]", cur.getPosition()));
		    } else {
			cur.getExpression2().accept(constantVisitor);
		    }
		}
		
		if(varEnv.entryExists(lexeme)){
		    errorLog.addItem(new ErrorItem("Redeclaration of variable " + lexeme + " originally declared at " + varEnv.getEntry(lexeme).getPosition() + " declared at ", decl.getPosition()));
		} else {
		    varEnv.addEntry(lexeme, new TypeCheckerVariableData(TypeCheckerVariableData.Type.INTEGER_ARRAY, cur.getPosition()));
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
    
    public Void visit(UnidentifiedDeclaration decl){
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
    
    public Void visit(BlockAssign assign){
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
    
    public Void visit(CaseXStatement stat){
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
    
    public Void visit(CaseZStatement stat){
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
    
    public Void visit(ForStatement forLoop){
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
    
    public Void visit(IfStatement ifStatement){
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
    
    public Void visit(NonBlockAssign assign){
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
    
    public Void visit(RepeatStatement stat){
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
     
    public Void visit(SystemTaskStatement task){
	// These are not important for now I will handle those later
      	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait){
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
	TypeCheckerVariableData.Type left = op.getLeft().accept(this);
	TypeCheckerVariableData.Type right = op.getRight().accept(this);

	//Do this later
	return TypeCheckerVariableData.Type.UNDEFINED;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public TypeCheckerVariableData.Type visit(UnaryOperation op){
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
    
    public TypeCheckerVariableData.Type visit(Concatenation concat){
	return TypeCheckerVariableData.Type.MIXED_VECTOR;
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
    
    public TypeCheckerVariableData.Type visit(SystemFunctionCall call){
	//I will do this later
	return TypeCheckerVariableData.Type.UNDEFINED;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public TypeCheckerVariableData.Type visit(Identifier ident){
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

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public TypeCheckerVariableData.Type visit(NumValue number){
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
    
    public TypeCheckerVariableData.Type visit(PortConnection connection){
	return connection.getExpression().accept(this);
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public TypeCheckerVariableData.Type visit(StrValue string){
	// do nothing
	return TypeCheckerVariableData.Type.STRING;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public TypeCheckerVariableData.Type visit(TernaryExpression expr){
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
    
    public TypeCheckerVariableData.Type visit(VectorCall vector){
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
}
