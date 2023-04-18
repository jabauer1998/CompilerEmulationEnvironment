package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement;

import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

/**
 * The Statement abstract class is used to represent Statement abstract syntax tree
 * nodes in the verilog language. This will come in handy when creating graphical user
 * interfaces.
 * 
 * @author Jacob Bauer
 */
public interface Statement {

    public abstract <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv);

}
