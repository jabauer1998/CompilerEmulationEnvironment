package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.verilog_compiler.visitor.*;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.*;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.*;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.*;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Vector;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Range;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pattern;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.*;
import edu.depauw.emulator_ide.verilog_compiler.main.util.*;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.InterpreterVariableData;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.InterpreterFunctionData;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.gui.GuiMemory;
import edu.depauw.emulator_ide.gui.GuiRegister;
import edu.depauw.emulator_ide.gui.GuiStatusBit;
import edu.depauw.emulator_ide.gui.Main;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;

import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
    
public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void>, ModuleVisitor<Void>, RegValueVisitor<Void>{
    
    private Environment<String, Position> modEnv;
    private Environment<String, InterpreterFunctionData> funcEnv;
    private Environment<String, InterpreterVariableData> varEnv;
    private Stack<String> topFunction = new Stack<String>();
    private Stack<Boolean> needExit = new Stack<Boolean>();
    private final InfoLog errorLog;

    private volatile Semaphore sema;

    private ArrayList<ModItem> processes = new ArrayList<>();
    
    public Interpreter(InfoLog errorLog){
	this.modEnv = new Environment<>();
	this.funcEnv = new Environment<>();
	this.varEnv = new Environment<>();
	this.errorLog = errorLog;
    }

    private String getTopFunctionName(){
	return topFunction.peek();
    }

    private boolean getTopExit(){
	return needExit.peek();
    }

    private void setTopFunctionName(String func){
	topFunction.push(func);
    }

    private void setTopExit(boolean val){
	needExit.push(val);
    }

    private void changeTopExit(boolean val){
	removeTopExit();
	needExit.push(val);
    }

    private void removeTopFunctionName(){
	topFunction.pop();
    }

    private void removeTopExit(){
	needExit.pop();
    }
    
    public void passModTable(Environment<String, Position> modEnv){
	this.modEnv = modEnv;
    }
    
    public void passFuncTable(Environment<String, InterpreterFunctionData> funcEnv){
	this.funcEnv = funcEnv;
    }
    
    public void passVarTable(Environment<String, InterpreterVariableData> varEnv) {
    	this.varEnv = varEnv;
    }

    private void errorAndExit(String error){
	errorAndExit(error, null);
    }

    private void errorAndExit(String error, Position pos){
	errorLog.addItem(new ErrorItem(error, pos));
	errorLog.printLog();
	System.exit(1);
    }
    
    public Void visit(ModuleDeclaration mod, Object... argv){
	modEnv.addScope();
	funcEnv.addScope();
	varEnv.addScope();
	Identifier modName = mod.getModuleName();
	if(modEnv.entryExists(modName.getLexeme())){
	    errorAndExit("Redeclaration of Module " + modName.getLexeme() + "found at " + '[' + mod.getPosition() + "] declared ", modEnv.getEntry(modName.getLexeme()));
	} else {
	    modEnv.addEntry(modName.getLexeme(), mod.getPosition());
	}
	for(int i = 0; i < mod.numParameters(); i++){
	    mod.getParameter(i).accept(this);
	}
	for(int i = 0; i < mod.numModItems(); i++){
	    mod.getModItem(i).accept(this);
	}
	sema = new Semaphore(-processes.size() + 1);
	AllwaysStatement.setThreadParameters(this, sema); //set parameters for the run methods for allways statements
	InitialStatement.setThreadParameters(this, sema); //do the same for initial statements
	for(int i = 0; i < processes.size(); i++){ //Iterate through queue and execute threads
	    if(processes.get(i) instanceof AllwaysStatement){ //Creates a new thread that should loop until cntrl-C
		new Thread((AllwaysStatement)processes.get(i)).start();
	    } else if(processes.get(i) instanceof InitialStatement){
		new Thread((InitialStatement)processes.get(i)).start();
	    } else {
		errorAndExit(" Unknown process type " + processes.get(i).getClass(), processes.get(i).getPosition());
	    }
	}
	try{
	    sema.acquire(); //aquire semaphore and wait until all other processes are done
	} catch (InterruptedException e){
	    e.printStackTrace();
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
	processes.add(stat);
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
    private boolean inFunctionParam = false;
    private boolean inFunctionName = false;

    public Void visit(FunctionDeclaration function, Object... argv){
	//May need to finish this later
	Declaration functionName = function.getFunctionName();
		
	inFunctionName = true;
	functionName.accept(this); //retrieve the function name from the declaration
	inFunctionName = false;
	
	funcEnv.addEntry(getTopFunctionName(), new InterpreterFunctionData(function, function.getPosition()));

	removeTopFunctionName();
		
	return null;
    }

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public Void visit(InitialStatement stat, Object... argv){
	processes.add(stat);
	return null;
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod, Object... argv){
	return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod, Object... argv){
	//I dont need these yet
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(TaskDeclaration task, Object... argv){
	Identifier taskName = task.getTaskName();
	if(funcEnv.inScope(taskName.getLexeme())){
	    errorAndExit("Task declaration by the name of " + taskName.getLexeme() + " found at [" + taskName.getPosition() + "] already exists at ", funcEnv.getEntry(taskName.getLexeme()).getPosition()); 
	} else {
	    funcEnv.addEntry(taskName.getLexeme(), new InterpreterFunctionData(task, task.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit a empty mod item in verilog
     * @param task
     */
    
    public Void visit(EmptyModItem macro, Object... argv){
	return null; //this class is just for completeness
    }
    
    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(!varEnv.inScope(current.getLexeme())){
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Wire(), current.getPosition()));		
	    }
	    InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    if(inFunctionParam){
		funcEnv.getEntry(getTopFunctionName()).addParameter(entryData); //add paramter to function
	    }
	}
	return null;
    }

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputRegScalarDeclaration decl, Object... argv){

	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(!varEnv.inScope(current.getLexeme())){
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Register(false), current.getPosition()));
	    }

	    InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    
	    if(inFunctionParam){
		funcEnv.getEntry(getTopFunctionName()).addParameter(entryData); //add paramter to function
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
	
	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));
	    
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
		if(entryData.getObject() == null){
		    Vector vec = new Vector(index1, index2);

		    if(index1 <= index2){
			for(int x = index1; x <= index2; x++){
			    vec.setValue(x, new Wire());
			}
		    } else {
			for(int x = index2; x <= index1; x++){
			    vec.setValue(x, new Wire());
			}
		    }
		    entryData.setObject(vec);
		} else {
		    errorLog.addItem(new ErrorItem("Variable named " + current.getLexeme() + "allready exists and cannot be converted to " + entryData.getObject().getClass(), current.getPosition()));
		}
	    } else {
		Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);
		if(index1 <= index2){
		    for(int x = index1; x <= index2; x++){
			vec.setValue(x, new Wire());
		    }
		} else {
		    for(int x = index2; x <= index1; x++){
			vec.setValue(x, new Wire());
		    }
		}
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
	    }

	    InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    if(inFunctionParam){
		funcEnv.getEntry(getTopFunctionName()).addParameter(entryData); //add paramter to function
	    }
	}
	return null;
    }

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputRegVectorDeclaration decl, Object... argv){
	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));
	
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
		
		if(entryData.getObject() == null){
		    entryData.setObject(new Vector(index1, index2));
		} else {
		    errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector", current.getPosition()));
		}
	    } else {
		Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);
		if(index1 <= index2){
		    for(int x = index1; x <= index2; x++){
			vec.setValue(x, new Register(false));
		    }
		} else {
		    for(int x = index2; x <= index1; x++){
			vec.setValue(x, new Register(false));
		    }
		}
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
	    }
	    InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    if(inFunctionParam){
		funcEnv.getEntry(getTopFunctionName()).addParameter(entryData); //add paramter to function
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
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    } else {
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Wire(), current.getPosition()));
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
	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));
	    
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
			
		if(entryData.getObject() == null){
		    entryData.setObject(new Vector(index1, index2));
		} else {
		    errorLog.addItem(new ErrorItem("Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector", current.getPosition()));
		}
	    } else {
		Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);
		if(index1 <= index2){
		    for(int x = index1; x != index2; x++){
			vec.setValue(x, new Wire());
		    }
		} else {
		    for(int x = index1; x != index2; x--){
		    	vec.setValue(x, new Wire());
		    }
		}
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
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

	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));

	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this, index1, index2);
	}
	
	return null;
    }

    /**
     * This is used to visit any output scalar declaration in Verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
	    } else {
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(new Wire(), current.getPosition()));
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

	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));
	    
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData entryData = varEnv.getEntry(current.getLexeme());
		
		if(entryData.getObject() == null){
		    entryData.setObject(new Vector(index1, index2));
		} else {
		    errorAndExit("Cannot re-assign variable of type " + entryData.getObject() + " to type output wire vector", current.getPosition());
		}
	    } else {
		Vector<CircuitElem> vec = new Vector<CircuitElem>(index1, index2);
		if(index1 <= index2){
		    for(int x = index1; x != index2; x++){
			vec.setValue(x, new Wire());
		    }
		} else {
		    for(int x = index1; x != index2; x--){
			vec.setValue(x, new Wire());
		    }
		}
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(vec, current.getPosition()));
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
	
	int index1 = (int)longValue(decl.getExpression1().accept(this)); //check whether the expressions return ints
	int index2 = (int)longValue(decl.getExpression2().accept(this));
	
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this, index1, index2);
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
	    if(varEnv.inScope(current.getLexeme())){
		InterpreterVariableData dataType = varEnv.getEntry(current.getLexeme());
		errorLog.addItem(new ErrorItem("Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ", current.getPosition()));
	    } else {
		varEnv.addEntry(current.getLexeme(), new InterpreterVariableData((double)0, current.getPosition()));
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
	if(varEnv.inScope(current.getLexeme())){
	    InterpreterVariableData dataType = varEnv.getEntry(current.getLexeme());
	    errorLog.addItem(new ErrorItem("Variable " + current.getLexeme() + " allready defined at " + dataType.getPosition() + " declared again at ", current.getPosition()));
	} else {
	    varEnv.addEntry(current.getLexeme(), new InterpreterVariableData(null, current.getPosition()));
	}
	return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new AndGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OrGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new OrGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NandGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new NandGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NorGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new NorGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XorGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new XorGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XnorGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input1 = (CircuitElem)decl.getExpression(1).accept(this);
	CircuitElem input2 = (CircuitElem)decl.getExpression(2).accept(this);
	CircuitElem[] inputs = new CircuitElem[decl.numExpressions() - 3];
	for(int i = 3; i < decl.numExpressions(); i++){
	    inputs[i] = (CircuitElem)decl.getExpression(i).accept(this);
	}
	new XnorGate(output, input1, input2, inputs);
	return null;
    }

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NotGateDeclaration decl, Object... argv){
	CircuitElem output = (CircuitElem)decl.getExpression(0).accept(this);
	CircuitElem input = (CircuitElem)decl.getExpression(1).accept(this);
	new NotGate(output, input);
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
	Expression lValue = assign.getLValue();

	Identifier name = null;
	if(lValue instanceof VectorCall){
	    name = ((VectorCall)lValue).getIdentifier();
	} else if(lValue instanceof Identifier){
	    name = (Identifier)lValue;
	} else if (lValue instanceof VectorSlice){
	    name = ((VectorSlice)lValue).getIdentifier();
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected Type for LValue " + lValue.getClass(), assign.getPosition()));
	    System.exit(1);
	    return null;
	}

	InterpreterVariableData data = varEnv.getEntry(name.getLexeme());

	Object result = assign.getExpression().accept(this);

	if(data.getObject() instanceof Double){
	    double value = 0;
	    if(result instanceof Vector){
		value = (double)OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = (double)OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (double)result;
	    }
	    data.setObject(value);
	} else if (data.getObject() instanceof Long) {
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    data.setObject(value);
	} else if(data.getObject() instanceof Long[]){
	    Long[] arr = (Long[])data.getObject();
	    int index = (int)((VectorCall)lValue).getExpression().accept(this);
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    arr[index] = value;
	} else if(data.getObject() instanceof Vector){
	    Vector<CircuitElem> vec = (Vector<CircuitElem>)data.getObject(); //returns the vector stored in the symbol table
	    if(lValue instanceof Identifier){
		if(result instanceof Vector){
		    OpUtil.deepAssign(vec, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.deepAssign(vec, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + vec.getClass(), lValue.getPosition()));
		    System.exit(1);
		}
	    } else if(lValue instanceof VectorCall){
		int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));

		if(result instanceof Vector){
		    OpUtil.deepAssign(vec, index, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.deepAssign(vec, index, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + vec.getClass(), lValue.getPosition()));
		    System.exit(1);
		}
		
	    } else if (lValue instanceof VectorSlice){
		int index1 = (int)longValue(((VectorSlice)lValue).getExpression1().accept(this));
		int index2 = (int)longValue(((VectorSlice)lValue).getExpression2().accept(this));

		if(result instanceof Vector){
		    OpUtil.deepAssign(vec, index1, index2, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.deepAssign(vec, index1, index2, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index1, index2, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index1, index2, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + vec.getClass(), lValue.getPosition()));
		    System.exit(1);
		}
	    }
	} else if (data.getObject() instanceof Vector[]){
	    Vector<CircuitElem>[] arr = (Vector<CircuitElem>[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		OpUtil.deepAssign(arr[index], (Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		OpUtil.deepAssign(arr[index], (CircuitElem)result);
	    } else if(result instanceof Long){
		OpUtil.shallowAssign(arr[index], (long)result);
	    } else if(result instanceof Boolean){
		OpUtil.shallowAssign(arr[index], (boolean)result);
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition()));
		System.exit(1);
	    }
	} else if (data.getObject() instanceof CircuitElem[]){
	    CircuitElem[] arr = (CircuitElem[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ?  vec.getIndex1() : vec.getIndex2(); 
		arr[index] = ((Vector<CircuitElem>)result).getValue(lowestIndex);
	    } else if(result instanceof CircuitElem){
		arr[index] = (CircuitElem)result;
	    } else if(result instanceof Long){
		arr[index] = new Register(((long)result) != 0 ? true : false);
	    } else if(result instanceof Boolean){
		arr[index] = new Register((boolean)result);
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + arr.getClass(), lValue.getPosition()));
		System.exit(1);
		return null;
	    }
	} else if (data.getObject() instanceof CircuitElem){
	    CircuitElem[] arr = (CircuitElem[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ?  vec.getIndex1() : vec.getIndex2(); 
		data.setObject(vec.getValue(lowestIndex));
	    } else if(result instanceof CircuitElem){
		data.setObject(result);
	    } else if(result instanceof Long){
		data.setObject(new Register(((long)result) != 0 ? true : false));
	    } else if(result instanceof Boolean){
		data.setObject(new Register((boolean)result));
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition()));
		System.exit(1);
		return null;
	    }
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected Type for Object " + data.getObject().getClass(), assign.getPosition()));
	    System.exit(1);
	    return null;
	}
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign, Object... argv){
	Expression lValue = assign.getLValue();
	Identifier name = null;
	if(lValue instanceof VectorCall){
	    name = ((VectorCall)lValue).getIdentifier();
	} else if(lValue instanceof Identifier){
	    name = (Identifier)lValue;
	} else if (lValue instanceof VectorSlice){
	    name = ((VectorSlice)lValue).getIdentifier();
	} else {
	    errorAndExit("Unexpected Type for LValue " + lValue.getClass(), assign.getPosition());
	    return null;
	}
	
	InterpreterVariableData data = varEnv.getEntry(name.getLexeme());
	Object result = assign.getExpression().accept(this);
	if(result == null){
	    errorAndExit("Result type of Null", name.getPosition());
	}

	if(result instanceof Scanner) {
	    data.setObject(result); //if it is a scanner ignore all assign rules and assign the scanner object to the variable
	} else if(data.getObject() instanceof Double){
	    double value = 0;
	    if(result instanceof Vector){
		value = (double)OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = (double)OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (double)result;
	    }
	    data.setObject(value);
	} else if (data.getObject() instanceof Long) {
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    data.setObject(value);
	} else if(data.getObject() instanceof Long[]){
	    Long[] arr = (Long[])data.getObject();
	    int index = (int)(long)((VectorCall)lValue).getExpression().accept(this);
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    arr[index] = value;
	} else if(data.getObject() instanceof Vector){
	    Vector<CircuitElem> vec = (Vector<CircuitElem>)data.getObject(); //returns the vector stored in the symbol table
	    if(lValue instanceof Identifier){
			if(result instanceof Vector){
			    OpUtil.shallowAssign(vec, (Vector<CircuitElem>)result);
			} else if(result instanceof CircuitElem){
			    OpUtil.shallowAssign(vec, (CircuitElem)result);
			} else if(result instanceof Long){
			    OpUtil.shallowAssign(vec, (long)result);
			} else if(result instanceof Boolean){
			    OpUtil.shallowAssign(vec, (boolean)result);
			} else {
			    errorAndExit("Incompatible assignment from " + result.getClass() + " to " + vec.getClass(), lValue.getPosition());
			}
	    } else if(lValue instanceof VectorCall){
		int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));

		if(result instanceof Vector){
		    OpUtil.shallowAssign(vec, index, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.shallowAssign(vec, index, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index, (boolean)result);
		} else {
		    errorAndExit("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition());
		    System.exit(1);
		}
		
	    } else if (lValue instanceof VectorSlice){
		int index1 = (int)longValue(((VectorSlice)lValue).getExpression1().accept(this));
		int index2 = (int)longValue(((VectorSlice)lValue).getExpression2().accept(this));
		if(result instanceof Vector){
		    OpUtil.shallowAssign(vec, index1, index2, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.shallowAssign(vec, index1, index2, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index1, index2, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index1, index2, (boolean)result);
		} else {
		    errorAndExit("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition());
		}
	    }
	} else if (data.getObject() instanceof Vector[]){
	    Vector<CircuitElem>[] arr = (Vector<CircuitElem>[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		OpUtil.shallowAssign(arr[index], (Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		OpUtil.shallowAssign(arr[index], (CircuitElem)result);
	    } else if(result instanceof Long){
		OpUtil.shallowAssign(arr[index], (long)result);
	    } else if(result instanceof Boolean){
		OpUtil.shallowAssign(arr[index], (boolean)result);
	    } else {
		errorAndExit("Incompatible assignment from " + result.getClass() + " to " + arr.getClass(), lValue.getPosition());
	    }
	} else if (data.getObject() instanceof CircuitElem[]){
	    CircuitElem[] arr = (CircuitElem[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ?  vec.getIndex1() : vec.getIndex2(); 
		((Register)arr[index]).setSignal(((Vector<CircuitElem>)result).getValue(lowestIndex).getSignal());
	    } else if(result instanceof CircuitElem){
		((Register)arr[index]).setSignal(((CircuitElem)result).getSignal());
	    } else if(result instanceof Long){
		((Register)arr[index]).setSignal(((long)result) != 0 ? true : false);
	    } else if(result instanceof Boolean){
		((Register)arr[index]).setSignal((boolean)result);
	    } else {
		errorAndExit("Incompatible assignment from " + result.getClass() + " to " + arr.getClass(), lValue.getPosition());
	    }
	} else if (data.getObject() instanceof CircuitElem){
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
		((Register)data.getObject()).setSignal(vec.getValue(lowestIndex).getSignal());
	    } else if(result instanceof CircuitElem){
		((Register)data.getObject()).setSignal(((CircuitElem)result).getSignal());
	    } else if(result instanceof Long){
		((Register)data.getObject()).setSignal(((long)result) != 0 ? true : false);
	    } else if(result instanceof Boolean){
		((Register)data.getObject()).setSignal((boolean)result);
	    } else {
		errorAndExit("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition());
	    }
	} else {
	    errorAndExit("Unexpected Type for Object " + data.getObject().getClass(), assign.getPosition());
	}

	if(inFunctionReturn && name.getLexeme().equals(getTopFunctionName())){
	    changeTopExit(true);
	}
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat, Object... argv){
	Object expr = stat.getExpression().accept(this);

	long target = longValue(expr);
    
	loop: for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Object exprValue = exprItem.getExpression(x).accept(this);
		    if(caseBoolean(target, exprValue)){
			item.getStatement().accept(this);
			break loop;
		    }
		}
	    } else {
		DefCaseItem exprItem = (DefCaseItem)item;
		item.getStatement().accept(this);
		break;
	    }
	}
	
	return null;
    }

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public Void visit(CaseXStatement stat, Object... argv){
	Object expr = stat.getExpression().accept(this);
	long target = longValue(expr);
    
	loop: for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Object exprValue = exprItem.getExpression(x).accept(this);
		    if(caseBoolean(target, exprValue)){
			item.getStatement().accept(this);
			break loop;
		    }
		}
	    } else {
		DefCaseItem exprItem = (DefCaseItem)item;
		item.getStatement().accept(this);
		break;
	    }
	}
	
	return null;
    }

    /**
     * This is used to visit casedz statements in verilog
     * @param assign
     */

    private boolean caseBoolean(long target, Object expr){
	if(expr instanceof Range){
	    Range r = (Range)expr;
	    return r.inRange(target);
	} else if (expr instanceof Pattern) {
	    Pattern pat = (Pattern)expr;
	    return pat.match(target);
	} else {
	    return target == longValue(expr);
	}
    }
    
    public Void visit(CaseZStatement stat, Object... argv){
	Object expr = stat.getExpression().accept(this);
	long target = longValue(expr);
	loop: for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Object exprValue = exprItem.getExpression(x).accept(this);
		    if(caseBoolean(target, exprValue)){
			item.getStatement().accept(this);
			break loop;
		    }
		}
	    } else {
		DefCaseItem exprItem = (DefCaseItem)item;
		item.getStatement().accept(this);
		break;
	    }
	}
	
	return null;
    }

    private boolean boolValue(Object obj){
	if(obj instanceof Long){
	    return ((long)obj) != 0;
	} else if(obj instanceof Boolean){
	    return (boolean)obj;
	} else if(obj instanceof Double){
	    return ((double)obj) != 0;
	} else if(obj instanceof Vector){
	    return boolValue(OpUtil.toLong((Vector<CircuitElem>)obj));
	} else if(obj instanceof CircuitElem){
	    return ((CircuitElem)obj).getSignal();
	} else {
	    errorAndExit("Unexpected Type to change to bool " + obj.getClass());
	    return false;
	}
    }

    private long longValue(Object obj){
	if(obj instanceof Long){
	    return ((long)obj);
	} else if(obj instanceof Boolean){
	    return (long)(((boolean)obj) ? 1 : 0);
	} else if(obj instanceof Double){
	    return (long)(double)obj;
	} else if(obj instanceof Vector){
	    return OpUtil.toLong((Vector<CircuitElem>)obj);
	} else if(obj instanceof CircuitElem){
	    return OpUtil.toLong((CircuitElem)obj);
	} else {
	    errorAndExit("Unexpected Type to change to bool " + obj.getClass());
	    return (long)-1;
	}
    }

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public Void visit(ForStatement forLoop, Object... argv){
	for(forLoop.getInit().accept(this); boolValue(forLoop.getExpression().accept(this)); forLoop.getChange().accept(this)){
	    forLoop.getStatement().accept(this);
	    if(inFunctionReturn){
		if(getTopExit()){
		    return null;
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop, Object... argv){
	boolean tf = true;
	while(tf){
	    foreverLoop.getStatement().accept(this);
	    if(inFunctionReturn){
		if(getTopExit()){
		    return null;
		}
	    }
	}
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement, Object... argv){
	Object expr = ifElseStatement.getExpression().accept(this);
	if(boolValue(expr)){
	    ifElseStatement.getIfStatement().accept(this);
	} else {
	    ifElseStatement.getElseStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement, Object... argv){
	Object expr = ifStatement.getExpression().accept(this);
	if(boolValue(expr)){
	    ifStatement.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign, Object... argv){
	Expression lValue = assign.getLValue();

	Identifier name = null;
	if(lValue instanceof VectorCall){
	    name = ((VectorCall)lValue).getIdentifier();
	} else if(lValue instanceof Identifier){
	    name = (Identifier)lValue;
	} else if (lValue instanceof VectorSlice){
	    name = ((VectorSlice)lValue).getIdentifier();
	} else {
	    errorAndExit("Unexpected Type for LValue " + lValue.getClass(), assign.getPosition());
	    return null;
	}

	InterpreterVariableData data = varEnv.getEntry(name.getLexeme());
	Object result = assign.getExpression().accept(this);

	if(data.getObject() instanceof Double){
	    double value = 0;
	    if(result instanceof Vector){
		value = (double)OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = (double)OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (double)result;
	    }
	    data.setObject(value);
	} else if (data.getObject() instanceof Long) {
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    data.setObject(value);
	} else if(data.getObject() instanceof Long[]){
	    Long[] arr = (Long[])data.getObject();
	    int index = (int)((VectorCall)lValue).getExpression().accept(this);
	    long value = 0;
	    if(result instanceof Vector){
		value = OpUtil.toLong((Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		value = OpUtil.toLong((CircuitElem)result);
	    } else {
		value = (long)result;
	    }
	    arr[index] = value;
	} else if(data.getObject() instanceof Vector){
	    Vector<CircuitElem> vec = (Vector<CircuitElem>)data.getObject(); //returns the vector stored in the symbol table
	    if(lValue instanceof Identifier){
		if(result instanceof Vector){
		    OpUtil.shallowAssign(vec, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.shallowAssign(vec, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + vec.getClass(), lValue.getPosition()));
		    System.exit(1);
		}
	    } else if(lValue instanceof VectorCall){
		int index = (int)((VectorCall)lValue).getExpression().accept(this);

		if(result instanceof Vector){
		    OpUtil.shallowAssign(vec, index, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.shallowAssign(vec, index, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition()));
		    System.exit(1);
		}
		
	    } else if (lValue instanceof VectorSlice){
		int index1 = (int)longValue(((VectorSlice)lValue).getExpression1().accept(this));
		int index2 = (int)longValue(((VectorSlice)lValue).getExpression2().accept(this));

		if(result instanceof Vector){
		    OpUtil.shallowAssign(vec, index1, index2, (Vector<CircuitElem>)result);
		} else if(result instanceof CircuitElem){
		    OpUtil.shallowAssign(vec, index1, index2, (CircuitElem)result);
		} else if(result instanceof Long){
		    OpUtil.shallowAssign(vec, index1, index2, (long)result);
		} else if(result instanceof Boolean){
		    OpUtil.shallowAssign(vec, index1, index2, (boolean)result);
		} else {
		    errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition()));
		    System.exit(1);
		}
	    }
	} else if (data.getObject() instanceof Vector[]){
	    Vector<CircuitElem>[] arr = (Vector<CircuitElem>[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		OpUtil.shallowAssign(arr[index], (Vector<CircuitElem>)result);
	    } else if(result instanceof CircuitElem){
		OpUtil.shallowAssign(arr[index], (CircuitElem)result);
	    } else if(result instanceof Long){
		OpUtil.shallowAssign(arr[index], (long)result);
	    } else if(result instanceof Boolean){
		OpUtil.shallowAssign(arr[index], (boolean)result);
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + arr.getClass(), lValue.getPosition()));
		System.exit(1);
	    }
	} else if (data.getObject() instanceof CircuitElem[]){
	    CircuitElem[] arr = (CircuitElem[])data.getObject();
	    //it must be an lvalue becuase you can only assign one element of an array at a time
	    int index = (int)longValue(((VectorCall)lValue).getExpression().accept(this));
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ?  vec.getIndex1() : vec.getIndex2(); 
		((Register)arr[index]).setSignal(((Vector<CircuitElem>)result).getValue(lowestIndex).getSignal());
	    } else if(result instanceof CircuitElem){
		((Register)arr[index]).setSignal(((CircuitElem)result).getSignal());
	    } else if(result instanceof Long){
		((Register)arr[index]).setSignal(((long)result) != 0 ? true : false);
	    } else if(result instanceof Boolean){
		((Register)arr[index]).setSignal((boolean)result);
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + arr.getClass(), lValue.getPosition()));
		System.exit(1);
		return null;
	    }
	} else if (data.getObject() instanceof CircuitElem){
	    if(result instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)result;
		int lowestIndex = (vec.getIndex1() <= vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
		((Register)data.getObject()).setSignal(vec.getValue(lowestIndex).getSignal());
	    } else if(result instanceof CircuitElem){
		((Register)data.getObject()).setSignal(((CircuitElem)result).getSignal());
	    } else if(result instanceof Long){
		((Register)data.getObject()).setSignal(((long)result) != 0 ? true : false);
	    } else if(result instanceof Boolean){
		((Register)data.getObject()).setSignal((boolean)result);
	    } else {
		errorLog.addItem(new ErrorItem("Incompatible assignment from " + result.getClass() + " to " + data.getObject().getClass(), lValue.getPosition()));
		System.exit(1);
		return null;
	    }
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected Type for Object " + data.getObject().getClass(), assign.getPosition()));
	    System.exit(1);
	    return null;
	}
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat, Object... argv){
        Object expr = stat.getExpression().accept(this);
	if(expr instanceof Long){
	    long amount = (long)expr;
	    for(long i = 0; i < amount; i++){
		stat.getStatement().accept(this);
		if(inFunctionReturn){
		    if(getTopExit()){
			return null;
		    }
		}
	    }
	} else if (expr instanceof Vector){
	    long amount = OpUtil.toLong((Vector<CircuitElem>)expr);
	    for(long i = 0; i < amount; i++){
		stat.getStatement().accept(this);
		if(inFunctionReturn){
		    if(getTopExit()){
			return null;
		    }
		}
	    }
	} else if(expr instanceof CircuitElem || expr instanceof Boolean){
	    if(boolValue(expr)){
		stat.getStatement().accept(this);
		if(inFunctionReturn){
		    if(getTopExit()){
			return null;
		    }
		}
	    }
	} else {
	    errorLog.addItem(new ErrorItem("Unknown Type for While loop expression " + expr.getClass(), stat.getExpression().getPosition()));
	    System.exit(1);
	    return null;
	}
	return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat, Object... argv){
	for(int i = 0; i < stat.numStatements(); i++){
	    stat.getStatement(i).accept(this);
	    if(inFunctionReturn){
		if(getTopExit()){
		    return null;
		}
	    }
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
	    //Collect symbol table data from the function
	    InterpreterFunctionData funcData = funcEnv.getEntry(tname.getLexeme());
	    funcData.storeParameterList();
	    TaskDeclaration decl = (TaskDeclaration)funcData.getFuncDeclaration();
	    varEnv.addScope();

	    setTopFunctionName(tname.getLexeme());
	    
	    inFunctionParam = true;
	    for(int i = 0; i < decl.numDeclarations(); i++){
	    	decl.getDeclaration(i).accept(this);
	    }
	    inFunctionParam = false;

	    removeTopFunctionName();
	    
	    if(task.numExpressions() == funcData.numParameters()){
		//Assign parameter value
		for(int i = 0; i < task.numExpressions(); i++){
		    InterpreterVariableData varData = funcData.getParameter(i);
		    varData.setObject(task.getExpression(i).accept(this));
		}
		
	    } else {
	    	errorAndExit("Argument amount mismatch " + tname.getLexeme() + " [Expected -> " + funcData.numParameters() + " | Got -> " + task.numExpressions() + " ]", tname.getPosition());
	    }

	    
	    decl.getStatement().accept(this);

	    funcData.restoreParameterList();
	    
	    varEnv.removeScope();
	    
	} else {
	    errorAndExit("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition());
	    return null;
	}
	return null;
    }

    /**
     * This is used to visit a system task statement in verilog
     * @param stat
     */
     
    public Void visit(SystemTaskStatement task, Object... argv){
	Identifier taskName = task.getSystemTaskName();
	if (taskName.getLexeme().equals("fclose")){
	    Scanner fReader = (Scanner)task.getExpression(0).accept(this);
	    fReader.close();
	} else if (taskName.getLexeme().equals("display")){
	    if(task.numExpressions() == 2){
		String fString = (String)task.getExpression(0).accept(this);
		Object data = task.getExpression(1).accept(this);
		Main.getByteOutputStream().writeBytes((fString + " " + data + "\n").getBytes()); //write to standard output in the gui
	    } else if(task.numExpressions() == 1){
		Object data = task.getExpression(0).accept(this);
		Main.getByteOutputStream().writeBytes(("\t" + data.toString() + '\n').getBytes()); //write to standard output in the gui
	    } else {
		errorAndExit("Unknown number of print arguments in " + taskName.getLexeme(), taskName.getPosition());
	    }
	} else if (taskName.getLexeme().equals("finish")){
	    System.out.println("Program is finished!!!");
	} else if (taskName.getLexeme().equals("setMemory")){
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
     
    public Void visit(WaitStatement wait, Object... argv){
	while(boolValue(wait.getExpression().accept(this)));
	wait.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop, Object... argv){
	int itr = 0;
	while(boolValue(whileLoop.getExpression().accept(this))){
	    itr++;
	    whileLoop.getStatement().accept(this);
	    if(inFunctionReturn){
		if(getTopExit()){
		    return null;
		}
	    }
	}
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
     * Below is the code that is used for visiting expressions
     /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public Object visit(BinaryOperation op, Object... argv){
	Object left = op.getLeft().accept(this);
	Object right = op.getRight().accept(this);

	switch(op.getOp().getOpType()){
	case PLUS:
	    if (left instanceof Double && right instanceof Double){
		return (double)left + (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left + longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) + (double)right;
	    } else {
		return longValue(left) + longValue(right);
	    }
	case MINUS:
	    if (left instanceof Double && right instanceof Double){
		return (double)left - (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left - longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) - (double)right;
	    } else {
		return longValue(left) - longValue(right);
	    }
	case TIMES:
	    if (left instanceof Double && right instanceof Double){
		return (double)left * (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left * longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) * (double)right;
	    } else {
		return longValue(left) * longValue(right);
	    }
	case DIV:
	    if (left instanceof Double && right instanceof Double){
		return (double)left / (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left / longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) / (double)right;
	    } else {
		return longValue(left) / longValue(right);
	    }
	case MOD:
	    return longValue(left) % longValue(right);
	case EQ2:
	    if (left instanceof Double && right instanceof Double){
		return (double)left == (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left == longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) == (double)right;
	    } else {
		return longValue(left) == longValue(right);
	    }
	case EQ3:
	    if(right instanceof Double && left instanceof Double){
		return (Double)left == (Double)right;
	    } else {
		return longValue(left) == longValue(right);
	    }
	case NE1:
	    if (left instanceof Double && right instanceof Double){
		return (double)left != (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left != longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) != (double)right;
	    } else {
		return longValue(left) != longValue(right);
	    }
	case NE2:
	    if(right instanceof Double && left instanceof Double){
		return (Double)left != (Double)right;
	    } else {
		return longValue(left) != longValue(right);
	    }
	case LAND:
	    return boolValue(left) && boolValue(right);
	case LOR:
	    return boolValue(left) || boolValue(right);
	case LE:
	    if (left instanceof Double && right instanceof Double){
		return (double)left <= (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left <= longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) <= (double)right;
	    } else {
		return longValue(left) <= longValue(right);
	    }
	case LT:
	    if (left instanceof Double && right instanceof Double){
		return (double)left < (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left < longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) < (double)right;
	    } else {
		return longValue(left) < longValue(right);
	    }
	case GE:
	    if (left instanceof Double && right instanceof Double){
		return (double)left >= (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left >= longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) >= (double)right;
	    } else {
		return longValue(left) >= longValue(right);
	    }
	case GT:
	    if (left instanceof Double && right instanceof Double){
		return (double)left > (double)right;
	    } else if (left instanceof Double && !(right instanceof Double)) {
		return (double)left > longValue(right);
	    } else if (!(left instanceof Double) && right instanceof Double){
		return longValue(left) > (double)right;
	    } else {
		return longValue(left) > longValue(right);
	    }
	case BAND:
	    return longValue(left) & longValue(right);
	case BOR:
	    return longValue(left) | longValue(right);
	case BXOR:
	    return longValue(left) ^ longValue(right);
	case BXNOR:
	    return ~(longValue(left) ^ longValue(right));
	case LSHIFT:
	    return longValue(left) << longValue(right);
	case RSHIFT:
	    return longValue(left) >> longValue(right);
	default:
	    errorLog.addItem(new ErrorItem("Invalid operation in verilog of type " + left.getClass() + " " + op.getOp().getOpType()+ " " + right.getClass(), op.getPosition()));
            return null;
	}
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public Object visit(UnaryOperation op, Object... argv){
	Object right = op.getRight().accept(this);
	switch(op.getOp().getOpType()){
	case PLUS:
	    return right;
	case MINUS:
	    if (right instanceof Double) {
		return -(double)right;
	    } else {
		return -longValue(right);
	    }
	case LNEG:
	    return !boolValue(right);
	case BNEG:
	    return ~longValue(right);
	default:
	    errorLog.addItem(new ErrorItem("Unknown unary operation in verilog of type " + op.getOp().getOpType() + " " + right.getClass(), op.getPosition()));
            return null;
	}
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public Object visit(Concatenation concat, Object... argv){
	int size = 0;
	for(int expr = 0; expr < concat.numExpressions(); expr++){
	    Object obj = concat.getExpression(expr).accept(this);
	    if(obj instanceof Vector){
		size += ((Vector<CircuitElem>)obj).getSize();
	    } else {
		size++; //it is a CircuitElem
	    }
	}
	
	Vector<CircuitElem> newVec = new Vector(size - 1, 0);
	int total = size - 1;
	for(int expr = 0; expr < concat.numExpressions(); expr++){
	    Object obj = concat.getExpression(expr).accept(this);
	    if(obj instanceof Vector){
		Vector<CircuitElem> vec = (Vector<CircuitElem>)obj;
		if(vec.getIndex1() <= vec.getIndex2()){
		    for(int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--){
			newVec.setValue(total, new Register(vec.getValue(v).getSignal()));
		    }
		} else {
		    for(int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--){
			newVec.setValue(total, new Register(vec.getValue(v).getSignal()));
		    }
		}
	    } else {
		CircuitElem circ = (CircuitElem)obj;
		newVec.setValue(total, circ);
		total--;
	    }
	}
	return newVec;
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public Object visit(ConstantExpression expr, Object... argv){
    return expr.getExpression().accept(this);
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public Object visit(EmptyExpression  expr, Object... argv){
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    boolean inFunctionReturn = false;
    public Object visit(FunctionCall call, Object... argv){
	Identifier tname = call.getFunctionName();
	if(funcEnv.entryExists(tname.getLexeme())){
	    //Collect symbol table data from the function
	    InterpreterFunctionData funcData = funcEnv.getEntry(tname.getLexeme());
	    FunctionDeclaration decl = (FunctionDeclaration)funcData.getFuncDeclaration();

	    funcData.storeParameterList();//store the old parameter list
	    varEnv.addScope();

	    setTopFunctionName(tname.getLexeme());
	    setTopExit(false);
	    decl.getFunctionName().accept(this); //declare the return variable for the function
	    
	    InterpreterVariableData returnData = varEnv.getEntry(tname.getLexeme()); //get return object

	    inFunctionParam = true; //Set input declarations as function params
	    for(int i = 0; i < decl.numDeclarations(); i++){
		decl.getDeclaration(i).accept(this);
	    }
	    inFunctionParam = false; //Params are declared if any
	    
	    if(call.numExpressions() == funcData.numParameters()){
		//Assign parameter values
		for(int i = 0; i < call.numExpressions(); i++){
		    InterpreterVariableData varData = funcData.getParameter(i);
		    Object exp = call.getExpression(i).accept(this);
		    varData.setObject(exp);
		}
		
	    } else {
		errorAndExit("Argument amount mismatch " + tname.getLexeme() + " [Expected -> " + funcData.numParameters() + " | Got -> " + call.numExpressions() + " ]", tname.getPosition());
		return null;
	    }
	    
	    inFunctionReturn = true;
	    decl.getStatement().accept(this);
	    inFunctionReturn = false;
	    removeTopExit();
	    varEnv.removeScope();
	    removeTopFunctionName();
	    funcData.restoreParameterList();
	    return returnData.getObject();
	} else {
	    errorAndExit("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition());
	    return null;
	}
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Object visit(SystemFunctionCall call, Object... argv){
	Identifier functionName = call.getSystemFunctionName();
	if(functionName.getLexeme().equals("fopen")){
	    String fname = (String)call.getExpression(0).accept(this);
	    String basePath = new File("").getAbsolutePath();
	    String access = (String)call.getExpression(1).accept(this);
	    if(fname.equals("default")) {
	    	return new Scanner(Main.getByteInputStream());
	    } else {
		File filename = new File(basePath + '/' + fname);
		if(access.equals("r")){
		    filename.setReadOnly();
			
		    Scanner ref = null;
				    			
		    try{
			ref = new Scanner(filename);
		    } catch (FileNotFoundException exp) {
			exp.printStackTrace();
			System.exit(1);
		    }
		    return ref;
		} else if(access.equals("w")){
		    filename.setWritable(true, false);

		    FileWriter ref = null;
		    try{
			ref = new FileWriter(filename);
		    } catch (IOException exp) {
			exp.printStackTrace();
			System.exit(1);
		    }
		    return ref;
		} else {
		    errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.getPosition());
		}
		return null;
	    }
	} else if(functionName.getLexeme().equals("feof")){
	    Scanner fReader = (Scanner)call.getExpression(0).accept(this);
	    boolean hasNextLine = fReader.hasNextLine();
	    return !hasNextLine;
	} else if(functionName.getLexeme().equals("fscanf")){
	    Scanner fReader = (Scanner)call.getExpression(0).accept(this);
	    String fString = (String)call.getExpression(1).accept(this);
	    Vector<CircuitElem> location = (Vector<CircuitElem>)call.getExpression(2).accept(this);
	    String data = fReader.nextLine();
	    //data = String.format(fString, data);
	    OpUtil.shallowAssign(location, data);
	    return (long)0; //allways true just for consistency with verilog
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
	    errorAndExit("Could not find a systemcall with the name " + functionName.getLexeme(), call.getPosition());
	}
	return null;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public Object visit(Identifier ident, Object... argv){
	if(varEnv.entryExists(ident.getLexeme())){
	    InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
	    return data.getObject();
	} else {
	    errorAndExit("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition());
	    return null;
	}
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public Object visit(NumValue number, Object... argv){
	if(number.getLexeme().contains(".")){
	    return Double.parseDouble(number.getLexeme());
	} else {
	    if(number.getLexeme().contains("\'")){
		if(NumberUtil.getBase(number.getLexeme()) == 10){
		    return NumberUtil.getDecimal(number.getLexeme());
		} else if(NumberUtil.getBase(number.getLexeme()) == 8){
		    return NumberUtil.getOctal(number.getLexeme());
		} else if(NumberUtil.getBase(number.getLexeme()) == 16){
		    return NumberUtil.getHexidecimal(number.getLexeme());
		} else {
		    return NumberUtil.getPattern(number.getLexeme());
		}
	    } else {
		return Long.parseLong(number.getLexeme());
	    }
	}
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public Object visit(PortConnection connection, Object... argv){
	return connection.getExpression().accept(this);
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public Object visit(StrValue string, Object... argv){
	return string.getLexeme();
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public Object visit(TernaryExpression expr, Object... argv){
	
	if(boolValue(expr.getCondition().accept(this))){
	    return expr.getLeft().accept(this);
	} else {
	    return expr.getRight().accept(this);
	}
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Object visit(VectorCall vector, Object... argv){
	Identifier ident = vector.getIdentifier();
	Object expr = vector.getExpression().accept(this);
	int index = (int)longValue(expr);
	if(varEnv.entryExists(ident.getLexeme())){
	    InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
	    Object dataObject = data.getObject();
	    if(dataObject instanceof Vector[]){
		Vector<CircuitElem> [] arr = (Vector<CircuitElem>[])data.getObject();
		Vector<CircuitElem> vec = arr[index];
		return vec;
	    } else if (dataObject instanceof Vector){
		return ((Vector<CircuitElem>)dataObject).getValue(index);
	    } else if (dataObject instanceof Long[]){
		return ((Long[])dataObject)[index];
	    } else {
		errorAndExit("Unkown array type for " + ident.getLexeme() + " [ Type -> " + dataObject.getClass() + " ]", ident.getPosition());
		return null;
	    }
	} else {
	    errorAndExit("Array or Vector " + ident.getLexeme() + " not found", ident.getPosition());
	    return null;
	}
    }

    public Object visit(VectorSlice vector, Object... argv){
	Identifier ident = vector.getIdentifier();
	int startIndex = (int)longValue(vector.getExpression1().accept(this));
	int endIndex = (int)longValue(vector.getExpression2().accept(this));
	if(varEnv.entryExists(ident.getLexeme())){
	    InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
	    Object dataObject = data.getObject();
	    if (dataObject instanceof Vector){
		Vector<CircuitElem> toRet = ((Vector<CircuitElem>)dataObject).getShallowSlice(startIndex, endIndex);
		return toRet;
	    } else {
		errorAndExit("Unkown array type for " + ident.getLexeme() + " [ Type -> " + dataObject.getClass() + " ]", ident.getPosition());
		return null;
	    }
	} else {
	    errorAndExit("Array or Vector " + ident.getLexeme() + " not found", ident.getPosition());
	    return null;
	}
    }

    /*
     *Below are RegValue visitors. These are used in the declarations of Integers and Registers
     */

    public Void visit(RegVectorIdent regVector, Object... argv){
	Identifier ident = regVector.getIdentifier();
	int start = (int)argv[0];
	int end = (int)argv[1];
	if(inFunctionName){
	    setTopFunctionName(ident.getLexeme());
	} else {
	    Vector vec = new Vector(start, end);
	    if(start <= end){
		for(int i = start; i <= end; i++){
		    vec.setValue(i, new Register(false));
		}
	    } else {
		for(int i = end; i <= start; i++){
		    vec.setValue(i, new Register(false));
		}
	    }
	    if(varEnv.inScope(ident.getLexeme())){
		InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
		if(got.getObject() == null){
		    got.setObject(vec);
		} else {
		    errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
		}
	    } else {
		varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
	    }
	}
	return null;
    }

    public Void visit(RegScalarIdent regScalar, Object... argv){
	Identifier ident = regScalar.getIdentifier();
	if(inFunctionName){
	    setTopFunctionName(ident.getLexeme());
	} else {
	    Register reg = new Register(false);
	    if(varEnv.inScope(ident.getLexeme())){
		InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
		if(got.getObject() == null){
		    got.setObject(reg);
		} else {
		    errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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
	if(inFunctionName){
	    setTopFunctionName(ident.getLexeme());
	} else {
	    Vector vec = new Vector(start, end);
	    if(start <= end){
		for(int i = start; i <= end; i++){
		    vec.setValue(i, new Register(false));
		}
	    } else {
		for(int i = end; i <= start; i++){
		    vec.setValue(i, new Register(false));
		}
	    }
	    if(varEnv.inScope(ident.getLexeme())){
		InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
		if(got.getObject() == null){
		    got.setObject(vec);
		} else {
		    errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
		}
	    } else {
		varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
	    }
	}
	return null;
    }

    public Void visit(OutputRegScalarIdent regScalar, Object... argv){
	Identifier ident = regScalar.getIdentifier();	
	if(inFunctionName){
	    setTopFunctionName(ident.getLexeme());
	} else {
	    Register reg = new Register(false);
	    if(varEnv.inScope(ident.getLexeme())){
		InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
		if(got.getObject() == null){
		    got.setObject(reg);
		} else {
		    errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
		}
	    } else {
		varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(reg, ident.getPosition()));
	    }
	}
	return null;
    }

    public Void visit(IntegerIdent intIdent, Object... argv){
	Identifier ident = intIdent.getIdentifier();
	if(inFunctionName){
	    setTopFunctionName(ident.getLexeme());
	} else {
	    if(varEnv.inScope(ident.getLexeme())){
		InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
		if(got.getObject() == null){
		    got.setObject((long)0);
		} else {
		    errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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
	
	for(int i = 0; i < aSize; i++){
	    arr[i] = new Vector<CircuitElem>(index1, index2);	    
	    if(index1 <= index2){
		for(int x = index1; x <= index2; x++){
		    arr[i].setValue(x, new Register(false));
		}
	    } else {
		for(int x = index2; x <= index1; x++){
		    arr[i].setValue(x, new Register(false));
		}
	    }
	}
	if(varEnv.inScope(ident.getLexeme())){
	    InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
	    if(got.getObject() == null){
		got.setObject(arr);
	    } else {
		errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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

	for(int i = 0; i < aSize; i++){
	    arr[i] = new Register(false);
	}
	
	if(varEnv.inScope(ident.getLexeme())){
	    InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
	    if(got.getObject() == null){
		got.setObject(arr);
	    } else {
		errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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

	for(int i = 0; i < aSize; i++){
	    arr[i] = new Vector<CircuitElem>(index1, index2);
	    if(index1 <= index2){
		for(int x = index1; x <= index2; x++){
		    arr[i].setValue(x, new Register(false));
		}
	    } else {
		for(int x = index2; x <= index1; x++){
		    arr[i].setValue(x, new Register(false));
		}
	    }
	}
	if(varEnv.inScope(ident.getLexeme())){
	    InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
	    if(got.getObject() == null){
		got.setObject(arr);
	    } else {
		errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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

	for(int i = 0; i < aSize; i++){
	    arr[i] = new Register(false);
	}
	if(varEnv.inScope(ident.getLexeme())){
	    InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
	    if(got.getObject() == null){
		got.setObject(arr);
	    } else {
		errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
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

	for(int i = 0; i < aSize; i++){
	    arr[i] = (long)0;
	}

	if(varEnv.inScope(ident.getLexeme())){
	    InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());
	    if(got.getObject() == null){
		got.setObject(arr);
	    } else {
		errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
	    }
	} else {
	    varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
	}
	return null;
    }
}

