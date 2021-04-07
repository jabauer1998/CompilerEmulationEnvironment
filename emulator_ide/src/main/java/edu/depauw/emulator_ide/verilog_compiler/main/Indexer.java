package edu.depauw.emulator_ide.verilog_compiler.main;

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
import edu.depauw.emulator_ide.verilog_compiler.visitor.*;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;
    
public class Indexer implements ExpressionVisitor<Void>, StatementVisitor<Void>, ModuleVisitor<Void>, RegValueVisitor<Void>{
    
    private Environment<String, Position> modEnv;
    private Environment<String, Position> funcEnv;
    private Environment<String, Position> varEnv;
    private Destination dest;
    private InfoLog errorLog;
    
    public Indexer(Destination dest, InfoLog errorLog){
	this.modEnv = new Environment<>();
	this.funcEnv = new Environment<>();
	this.varEnv = new Environment<>();
	this.dest = dest;
	this.errorLog = errorLog;
    }
    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod
     *@author Jacob bauer
     */
    
    public Void visit(ModuleDeclaration mod, Object... argv){
	modEnv.addScope();
	funcEnv.addScope();
	varEnv.addScope();
	Identifier modName = mod.getModuleName();
	if(modEnv.entryExists(modName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Module Entry " + modName.getLexeme() + " Allready Exists", modName.getPosition())); 
	} else {
	    dest.println("DECL MODULE " + modName.getLexeme() + " AT [" + modName.getPosition() + ']');
	    modEnv.addEntry(modName.getLexeme(), modName.getPosition());
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
    
    public Void visit(FunctionDeclaration function, Object... argv){
	Declaration funcName = function.getFunctionName();
	varEnv.addScope();
	funcName.accept(this);
	for(int i = 0; i < function.numDeclarations(); i++){
	    function.getDeclaration(i).accept(this);
	}
	function.getStatement().accept(this);
	varEnv.removeScope();
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
	if(modEnv.entryExists(modName.getLexeme())){
	    dest.println("USE MODULE " + modName.getLexeme() + " DECLARED AT [" + modEnv.getEntry(modName.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Identifier " + modName.getLexeme() + " not found", modName.getPosition()));
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
	    errorLog.addItem(new ErrorItem("Task Entry " + taskName.getLexeme() + " Allready Exists", taskName.getPosition())); 
	} else {
	    dest.println("DECL TASK " + taskName.getLexeme() + " AT [" + taskName.getPosition() + ']');
	    funcEnv.addEntry(taskName.getLexeme(), taskName.getPosition());
	}
	varEnv.addScope();
	for(int i = 0; i < task.numDeclarations(); i++){
	    task.getDeclaration(i).accept(this);
	}
	task.getStatement().accept(this);
	varEnv.removeScope();
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(MacroDefinition macro, Object... argv){
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
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT [" + current.getPosition() + ']');
		varEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
	return null;
    }

     /**
     * This is used to visit any input wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT [" + current.getPosition() + ']');
		varEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT [" + current.getPosition() + ']');
		varEnv.addEntry(current.getLexeme(), current.getPosition());
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
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT " + current.getPosition());
		varEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireVectorDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT " + current.getPosition());
		varEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
	return null;
    }

    /**
     * This is used to visit any input wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireVectorDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier current = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		dest.println("USE WIRE " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT " + current.getPosition());
		varEnv.addEntry(current.getLexeme(), current.getPosition());
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
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputRegScalarDeclaration decl, Object... argv){
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
    
    public Void visit(OutputRegVectorDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
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
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(UnidentifiedDeclaration decl, Object... argv){
	Identifier cur = decl.getIdentifier();
	if(!varEnv.entryExists(cur.getLexeme())){
	    dest.println("DECL " + cur.getLexeme() + " AT [" + cur.getPosition() + ']');
	    varEnv.addEntry(cur.getLexeme(), cur.getPosition());
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
		dest.println("USE REAL " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']'); 
	    } else {
		dest.println("DECL REAL " + current.getLexeme() + " AT [" + current.getPosition() + ']');
		varEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	    decl.getExpression(i).accept(this);
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
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign, Object... argv){
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat, Object... argv){
	stat.getExpression().accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    exprItem.getExpression(x).accept(this);
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
	stat.getExpression().accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    exprItem.getExpression(x).accept(this);
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
	stat.getExpression().accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    exprItem.getExpression(x).accept(this);
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
	forLoop.getExpression().accept(this);
	forLoop.getChange().accept(this);
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
	ifElseStatement.getExpression().accept(this);
	ifElseStatement.getIfStatement().accept(this);
	ifElseStatement.getElseStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement, Object... argv){
	ifStatement.getExpression().accept(this);
	ifStatement.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign, Object... argv){
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat, Object... argv){
	stat.getExpression().accept(this);
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
	    dest.println("USE FUNCTION " + tname.getLexeme() + " AT [" + tname.getPosition() + "] DEFINED AT [" + funcEnv.getEntry(tname.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition())); 
	}

	for(int i = 0; i < task.numExpressions(); i++){
	    task.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a system task statement in verilog
     * @param stat
     */
     
    public Void visit(SystemTaskStatement task, Object... argv){

	for(int i = 0; i < task.numExpressions(); i++){
	    task.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait, Object... argv){
	wait.getExpression().accept(this);
	wait.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop, Object... argv){
	whileLoop.getExpression().accept(this);
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
    
    public Void visit(BinaryOperation op, Object... argv){
	op.getLeft().accept(this);
	op.getRight().accept(this);
	return null;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public Void visit(UnaryOperation op, Object... argv){
	op.getRight().accept(this);
	return null;
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public Void visit(Concatenation concat, Object... argv){
	for(int i = 0; i < concat.numExpressions(); i++){
	    concat.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public Void visit(ConstantExpression expr, Object... argv){
	expr.getExpression().accept(this);
	return null;
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public Void visit(EmptyExpression expr, Object... argv){
	//this is just a placeholder we do not need to put anything here
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Void visit(FunctionCall call, Object... argv){
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
    
    public Void visit(SystemFunctionCall call, Object... argv){
	for(int i = 0; i < call.numExpressions(); i++){
	    call.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public Void visit(Identifier ident, Object... argv){
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
    
    public Void visit(NumValue number, Object... argv){
	// do nothing
	return null;
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public Void visit(PortConnection connection, Object... argv){
	connection.getExpression().accept(this);
	return null;
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public Void visit(StrValue string, Object... argv){
	// do nothing
	return null;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public Void visit(TernaryExpression expr, Object... argv){
	expr.getCondition().accept(this);
	expr.getLeft().accept(this);
	expr.getRight().accept(this);
	return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Void visit(VectorCall vector, Object... argv){
	Identifier ident = vector.getIdentifier();
	if(varEnv.entryExists(ident.getLexeme())){
	    dest.println("USE VECTOR " + ident.getLexeme() + " AT [" + ident.getPosition() + "] DECLARED AT [" + varEnv.getEntry(ident.getLexeme()) + ']');
	} else {
	    errorLog.addItem(new ErrorItem("Vector Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	}
	vector.getExpression1().accept(this);
	return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Void visit(VectorSlice vector, Object... argv){
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

    /**
     * This is the code for visiting a Macro identifier in verilog
     * @param string
     */
    
    public Void visit(MacroIdentifier ident, Object... argv){
	return null;
    }

    /* Below is the code for handling Macro Identifiers
     */

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(IntegerArray arr, Object... argv){
	Identifier current = arr.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	arr.getExpression1().accept(this);
	arr.getExpression2().accept(this);
	return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(RegScalarArray arr, Object... argv){
	Identifier current = arr.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	arr.getExpression1().accept(this);
	arr.getExpression2().accept(this);
	return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(RegVectorArray arr, Object... argv){
	Identifier current = arr.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	arr.getExpression1().accept(this);
	arr.getExpression2().accept(this);
	return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(IntegerIdent ident, Object... argv){
	Identifier current = ident.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(RegScalarIdent ident, Object... argv){
	Identifier current = ident.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * @param Jacob Bauer
     */

    public Void visit(RegVectorIdent ident, Object... argv){
	Identifier current = ident.getIdentifier();
	if(varEnv.entryExists(current.getLexeme())){
	    dest.println("USE INTEGER " + current.getLexeme() + " AT [" + current.getPosition() + "] DECLARED AT [" + varEnv.getEntry(current.getLexeme()) + ']');
	} else {
	    dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
	    varEnv.addEntry(current.getLexeme(), current.getPosition());
	}
	return null;
    }

    
}
