package edu.depauw.emulator_ide.verilog_interpreter.visitor_passes;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.BinaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.operation.UnaryOperation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.BinaryNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.DecimalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.HexadecimalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.OctalNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstance;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstantiation;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.process.AllwaysProcess;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.process.InitialProcess;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Int;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Output;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Real;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.RegValueList;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Unidentified;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Wire;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseXStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseZStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.CaseItem;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.DefCaseItem;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.ExprCaseItem;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.NonBlockingAssignment;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForeverStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching.RepeatStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching.WhileStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfElseStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.task.TaskStatement;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;

public class Indexer implements ExpressionVisitor<Void>, StatementVisitor<Void>, ModuleVisitor<Void> {
    private SymbolTable<Position> modEnv;
    private SymbolTable<Position> funcEnv;
    private SymbolTable<Position> varEnv;
    private Destination                   dest;
    private ErrorLog                       errorLog;

    public Indexer(Destination dest, ErrorLog errorLog) {
        this.modEnv = new SymbolTable<>();
        this.funcEnv = new SymbolTable<>();
        this.varEnv = new SymbolTable<>();
        this.dest = dest;
        this.errorLog = errorLog;
    }

    /**
     * This is the top level visit statement used to visit a Verilog Module which should
     * allways be the root of the AST
     * 
     * @param  mod
     * @author     Jacob bauer
     */

    public Void visit(ModuleDeclaration mod, Object... argv){
        modEnv.addScope();
        funcEnv.addScope();
        varEnv.addScope();
        String modName = mod.moduleName;

        if (modEnv.entryExists(modName)) {
            errorLog.addItem(new ErrorItem("Module Entry " + modName + " Allready Exists", mod.position));
        } else {
            dest.println("DECL MODULE " + modName + " AT [" + mod.position + ']');
            modEnv.addEntry(modName, mod.position);
        }

        for(ModuleItem Decl : mod.moduleItemList){
            Decl.accept(this);
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
     * 
     * @param stat
     */

    public Void visit(AllwaysProcess stat, Object... argv){
        stat.statement.accept(this);
        return null;
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * 
     * @param assign
     */

    public Void visit(ContinuousAssignment assign, Object... argv){

        for(BlockingAssignment Assignment : assign.assignmentList){
            Assignment.accept(this);
        }

        return null;
    }

    /**
     * This is the code that is used to visit a function declaration in java
     * 
     * @param function
     */

    public Void visit(FunctionDeclaration function, Object... argv){
        ModuleItem funcName = function.functionName;
        
        varEnv.addScope();
        funcName.accept(this);

        for (ModuleItem Param : function.paramaters) {
            Param.accept(this);
        }

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
        for(ModuleInstance instance : mod.modList){
            instance.accept(this);
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

        if (modEnv.entryExists(modName)) {
            dest.println("USE MODULE " + modName + " DECLARED AT [" + modEnv.getEntry(modName) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Identifier " + modName + " not found", mod.position));
        }

        for(Expression expr : mod.expList){
            expr.accept(this);
        }

        return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * 
     * @param mod
     */

    public Void visit(EmptyModItem mod, Object... argv){ 
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
            errorLog.addItem(new ErrorItem("Task Entry " + taskName + " Allready Exists", task.position));
        } else {
            dest.println("DECL TASK " + taskName + " AT [" + task.position + ']');
            funcEnv.addEntry(taskName, task.position);
        }

        varEnv.addScope();
        for (ModuleItem Task : task.paramaters) {
            Task.accept(this);
        }

        task.stat.accept(this);
        varEnv.removeScope();

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
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any input wire scalar wire declaration in verilog. Ex. wire a,
     * b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Input.Wire.Scalar.Ident decl, Object... argv){
        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any input wire scalar wire declaration in verilog. Ex. wire a,
     * b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Input.Reg.Scalar.Ident decl, Object... argv){
        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public Void visit(Output.Wire.Scalar.Ident decl, Object... argv){
        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
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

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog. Ex. wire [31:0] a, b, c
     * ... ;
     * 
     * @param decl
     */

    public Void visit(Output.Wire.Vector.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }


        return null;
    }

    /**
     * This is used to visit any input wire vector declaration in verilog. Ex. wire [31:0]
     * a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Input.Wire.Vector.Ident decl, Object... argv){
        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE WIRE " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL WIRE " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }


        return null;
    }

    /**
     * This is used to visit any input wire vector declaration in verilog. Ex. wire [31:0]
     * a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Input.Reg.Vector.Ident decl, Object... argv){
        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Reg.Scalar.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Output.Reg.Scalar.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
     * ... ;
     * 
     * @param decl
     */

    public Void visit(Reg.Vector.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
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

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REG " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REG " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Int.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE INTEGER " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL INTEGER " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Unidentified.Declaration decl, Object... argv){
        String cur = decl.declaration;

        if (!varEnv.entryExists(cur)) {
            dest.println("DECL " + cur + " AT [" + decl.position + ']');
            varEnv.addEntry(cur, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(Real.Ident decl, Object... argv){

        if (varEnv.entryExists(decl.declarationIdentifier)) {
            dest.println("USE REAL " + decl.declarationIdentifier + " AT [" + decl.position + "] DECLARED AT ["
                + varEnv.getEntry(decl.declarationIdentifier) + ']');
        } else {
            dest.println("DECL REAL " + decl.declarationIdentifier + " AT [" + decl.position + ']');
            varEnv.addEntry(decl.declarationIdentifier, decl.position);
        }

        return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog. Ex. integer a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(AndGateDeclaration decl, Object... argv){

        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any orgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(OrGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any nandgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(NandGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any norgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(NorGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any xorgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(XorGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any xnorgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(XnorGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit any notgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public Void visit(NotGateDeclaration decl, Object... argv){
        for(Expression exp : decl.gateConnections){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * 
     * @param assign
     */

    public Void visit(BlockingAssignment assign, Object... argv){
        assign.rightHandSide.accept(this);
        return null;
    }

    /**
     * This is used to visit case statements in verilog
     * 
     * @param assign
     */

    public Void visit(CaseStatement stat, Object... argv){
        stat.exp.accept(this);


        for (CaseItem item : stat.itemList){

            if (item instanceof ExprCaseItem) {
                ExprCaseItem exprItem = (ExprCaseItem)item;

                for (Expression exp : exprItem.expList) { 
                   exp.accept(this);
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
        stat.exp.accept(this);

        for (CaseItem item : stat.itemList){

            if (item instanceof ExprCaseItem) {
                ExprCaseItem exprItem = (ExprCaseItem)item;

                for (Expression exp : exprItem.expList) { 
                   exp.accept(this);
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
        stat.exp.accept(this);

        for (CaseItem item : stat.itemList){

            if (item instanceof ExprCaseItem) {
                ExprCaseItem exprItem = (ExprCaseItem)item;

                for (Expression exp : exprItem.expList) { 
                   exp.accept(this);
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
        forLoop.exp.accept(this);
        forLoop.change.accept(this);
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
        ifElseStatement.condition.accept(this);
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
        ifStatement.condition.accept(this);
        ifStatement.trueStatement.accept(this);
        return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * 
     * @param assign
     */

    public Void visit(NonBlockingAssignment assign, Object... argv){
        for(Expression rightHand : assign.rightHandSide){
            rightHand.accept(this);
        }
        return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * 
     * @param stat
     */

    public Void visit(RepeatStatement stat, Object... argv){
        stat.exp.accept(this);
        stat.stat.accept(this);
        return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * 
     * @param stat
     */

    public Void visit(SeqBlockStatement stat, Object... argv){
        for (Statement Stat : stat.statementList) {
            Stat.accept(this);
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
            dest.println("USE FUNCTION " + tname + " AT [" + task.position + "] DEFINED AT ["
                + funcEnv.getEntry(tname) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Function Entry " + tname + " Doesnt Exist", task.position));
        }

        for (Expression exp : task.argumentList){ 
            exp.accept(this); 
        }

        return null;
    }

    /**
     * This is used to visit a system task statement in verilog
     * 
     * @param stat
     */

    public Void visit(SystemTaskStatement task, Object... argv){
        for (Expression exp : task.argumentList){ 
            exp.accept(this); 
        }

        return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * 
     * @param stat
     */

    public Void visit(WaitStatement wait, Object... argv){
        wait.exp.accept(this);
        wait.stat.accept(this);
        return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * 
     * @param whileLoop
     */

    public Void visit(WhileStatement whileLoop, Object... argv){
        whileLoop.exp.accept(this);
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

    /**
     * This is the code for visiting binary operations
     * 
     * @param op
     */

    public Void visit(BinaryOperation op, Object... argv){
        op.left.accept(this);
        op.right.accept(this);
        return null;
    }

    /**
     * This is the code for visiting unary operations
     * 
     * @param op
     */

    public Void visit(UnaryOperation op, Object... argv){
        op.rightHandSideExpression.accept(this);
        return null;
    }

    /**
     * This is the code for visiting concatenations
     * 
     * @param concat
     */

    public Void visit(Concatenation concat, Object... argv){

        for (Expression exp : concat.circuitElementExpressionList){ 
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is the code for visiting Constant Expressions
     * 
     * @param expr
     */

    public Void visit(ConstantExpression expr, Object... argv){
        expr.expression.accept(this);
        return null;
    }

    /**
     * This is the code for visiting Empty Expressions
     * 
     * @param expr
     */

    public Void visit(EmptyExpression expr, Object... argv){
        // this is just a placeholder we do not need to put anything here
        return null;
    }

    /**
     * This is the code for visiting Function Calls
     * 
     * @param call
     */

    public Void visit(FunctionCall call, Object... argv){
        String fname = call.functionName;

        if (funcEnv.entryExists(fname)) {
            dest.println("USE FUNCTION " + fname + " AT [" + call.position + "] DECLARED AT ["
                + funcEnv.getEntry(fname) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Function Entry " + fname + " Doesnt Exist", call.position));
        }

        for (Expression arg : call.argumentList){ 
            arg.accept(this); 
        }

        return null;
    }

    /**
     * This is the code for visiting Function Calls
     * 
     * @param call
     */

    public Void visit(SystemFunctionCall call, Object... argv){

        for(Expression exp : call.argumentList){
            exp.accept(this);
        }

        return null;
    }

    /**
     * This is the code for visiting an Identifier
     * 
     * @param ident
     */

    public Void visit(Identifier ident, Object... argv){

        if (varEnv.entryExists(ident.labelIdentifier)) {
            dest.println("USE VARIABLE " + ident.labelIdentifier + " AT [" + ident.position + "] DECLARED AT ["
                + varEnv.getEntry(ident.labelIdentifier) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Variable Entry " + ident.labelIdentifier + " Doesnt Exist", ident.position));
        }

        return null;
    }

    /**
     * This is the code for visiting an Number in verilog
     * 
     * @param number
     */

    public Void visit(Number number, Object... argv){
        // do nothing
        return null;
    }

    /**
     * This is the code for visiting a port connection in verilog
     * 
     * @param connection
     */

    public Void visit(PortConnection connection, Object... argv){
        connection.connectingFrom.accept(this);
        return null;
    }

    /**
     * This is the code for visiting a string in verilog
     * 
     * @param string
     */

    public Void visit(StringNode string, Object... argv){
        // do nothing
        return null;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * 
     * @param expr
     */

    public Void visit(TernaryOperation expr, Object... argv){
        expr.condition.accept(this);
        expr.ifTrue.accept(this);
        expr.ifFalse.accept(this);
        return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * 
     * @param string
     */

    public Void visit(Element vector, Object... argv){
        String ident = vector.labelIdentifier;

        if (varEnv.entryExists(ident)) {
            dest.println("USE VECTOR " + ident + " AT [" + vector.position + "] DECLARED AT [" + varEnv.getEntry(ident) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Vector Entry " + ident + " Doesnt Exist", vector.position));
        }

        vector.index1.accept(this);
        return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * 
     * @param string
     */

    public Void visit(Slice vector, Object... argv){
        String ident = vector.labelIdentifier;

        if (varEnv.entryExists(ident)) {
            dest.println("USE VECTOR " + ident + " AT [" + vector.position + "] DECLARED AT ["
                + varEnv.getEntry(ident) + ']');
        } else {
            errorLog.addItem(new ErrorItem("Vector Entry " + ident + " Doesnt Exist", vector.position));
        }

        vector.index1.accept(this);
        vector.index2.accept(this);
        return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * 
     * @param Jacob Bauer
     */

    public Void visit(Int.Array arr, Object... argv){
        String current = arr.declarationIdentifier;

        if (varEnv.entryExists(current)) {
            dest.println("USE INTEGER " + current + " AT [" + arr.position + "] DECLARED AT ["
                + varEnv.getEntry(current)+ ']');
        } else {
            dest.println("DECL INTEGER " + current + " AT " + arr.position);
            varEnv.addEntry(current, arr.position);
        }

        arr.arrayIndex1.accept(this);
        arr.arrayIndex2.accept(this);
        return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * 
     * @param Jacob Bauer
     */

    public Void visit(Reg.Scalar.Array arr, Object... argv){
        String current = arr.declarationIdentifier;

        if (varEnv.entryExists(current)) {
            dest.println("USE INTEGER " + current + " AT [" + arr.position + "] DECLARED AT ["
                + varEnv.getEntry(current) + ']');
        } else {
            dest.println("DECL INTEGER " + current + " AT " + arr.position);
            varEnv.addEntry(current, arr.position);
        }

        arr.arrayIndex1.accept(this);
        arr.arrayIndex2.accept(this);
        return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * 
     * @param Jacob Bauer
     */

    public Void visit(Reg.Vector.Array arr, Object... argv){
        String current = arr.declarationIdentifier;

        if (varEnv.entryExists(current)) {
            dest.println("USE INTEGER " + current + " AT [" + arr.position + "] DECLARED AT ["
                + varEnv.getEntry(current) + ']');
        } else {
            dest.println("DECL INTEGER " + current + " AT " + arr.position);
            varEnv.addEntry(current, arr.position);
        }

        arr.arrayIndex1.accept(this);
        arr.arrayIndex2.accept(this);
        return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * 
     * @param Jacob Bauer
     */

    public Void visit(Output.Reg.Scalar.Array arr, Object... argv){
        String current = arr.declarationIdentifier;

        if (varEnv.entryExists(current)) {
            dest.println("USE INTEGER " + current + " AT [" + arr.position + "] DECLARED AT ["
                + varEnv.getEntry(current) + ']');
        } else {
            dest.println("DECL INTEGER " + current + " AT " + arr.position);
            varEnv.addEntry(current, arr.position);
        }

        arr.arrayIndex1.accept(this);
        arr.arrayIndex2.accept(this);
        return null;
    }

    /**
     * This is the code for visiting and integer array using Java
     * 
     * @param Jacob Bauer
     */

    public Void visit(Output.Reg.Vector.Array arr, Object... argv){
        String current = arr.declarationIdentifier;

        if (varEnv.entryExists(current)) {
            dest.println("USE INTEGER " + current + " AT [" + arr.position + "] DECLARED AT ["
                + varEnv.getEntry(current) + ']');
        } else {
            dest.println("DECL INTEGER " + current + " AT " + arr.position);
            varEnv.addEntry(current, arr.position);
        }

        arr.arrayIndex1.accept(this);
        arr.arrayIndex2.accept(this);
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
    public Void visit(BinaryNode number, Object... argv){ // TODO Auto-generated method stub
    return null; }

    @Override
    public Void visit(DecimalNode number, Object... argv){ // TODO Auto-generated method stub
    return null; }

    @Override
    public Void visit(HexadecimalNode number, Object... argv){ // TODO Auto-generated method stub
    return null; }

    @Override
    public Void visit(OctalNode number, Object... argv){ // TODO Auto-generated method stub
    return null; }
}
