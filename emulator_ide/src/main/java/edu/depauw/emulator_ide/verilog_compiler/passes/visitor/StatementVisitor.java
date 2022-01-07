package edu.depauw.emulator_ide.verilog_compiler.passes.visitor;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;

public interface StatementVisitor<StatVisitType> {

    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(Assignment assign, Object... argv);

    /**
     * This is used to visit blocking assignments in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(BlockAssign assign, Object... argv);

    /**
     * This is used to visit case statements in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(CaseStatement stat, Object... argv);

    /**
     * This is used to visit casex statements in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(CaseXStatement stat, Object... argv);

    /**
     * This is used to visit casez statements in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(CaseZStatement stat, Object... argv);

    /**
     * This is used to visit a for loop in verilog
     * 
     * @param forLoop
     */

    public StatVisitType visit(ForStatement forLoop, Object... argv);

    /**
     * This is used to visit a forever loop in verilog
     * 
     * @param foreverLoop
     */

    public StatVisitType visit(ForeverStatement foreverLoop, Object... argv);

    /**
     * This is used to visit a if else statement in verilog
     * 
     * @param ifElseStatement
     */

    public StatVisitType visit(IfElseStatement ifElseStatement, Object... argv);

    /**
     * This is used to visit a if else statement in verilog
     * 
     * @param ifElseStatement
     */

    public StatVisitType visit(IfStatement ifElseStatement, Object... argv);

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(NonBlockAssign assign, Object... argv);

    /**
     * This is used to visit a repeat statement in verilog
     * 
     * @param stat
     */

    public StatVisitType visit(RepeatStatement stat, Object... argv);

    /**
     * This is used to visit a seq block in verilog
     * 
     * @param stat
     */

    public StatVisitType visit(SeqBlockStatement stat, Object... argv);

    /**
     * This is used to visit a taskcall in verilog
     * 
     * @param stat
     */

    public StatVisitType visit(TaskStatement task, Object... argv);

    /**
     * This is used to visit a taskcall in verilog
     * 
     * @param stat
     */

    public StatVisitType visit(SystemTaskStatement task, Object... argv);

    /**
     * This is used to visit a wait statement in verilog
     * 
     * @param stat
     */

    public StatVisitType visit(WaitStatement wait, Object... argv);

    /**
     * This is used to visit a while loop in verilog
     * 
     * @param whileLoop
     */

    public StatVisitType visit(WhileStatement whileLoop, Object... argv);

    /**
     * This is the code for visiting empty statements this is here just for completion
     * 
     * @param none
     */

    public StatVisitType visit(EmptyStatement stat, Object... argv);

}
