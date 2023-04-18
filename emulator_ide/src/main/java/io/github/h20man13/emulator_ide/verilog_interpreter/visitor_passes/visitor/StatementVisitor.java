package io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor;

import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.*;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseXStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseZStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.DefCaseItem;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.ExprCaseItem;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.NonBlockingAssignment;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForeverStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.RepeatStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.WhileStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfElseStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.TaskStatement;
public interface StatementVisitor<StatVisitType> {

    /**
     * This is used to visit blocking assignments in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(BlockingAssignment assign, Object... argv);

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

    public StatVisitType visit(DefCaseItem stat, Object... argv);

    /**
     * This is used to visit casez statements in verilog
     * 
     * @param assign
     */

    public StatVisitType visit(ExprCaseItem stat, Object... argv);

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

    public StatVisitType visit(NonBlockingAssignment assign, Object... argv);

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
