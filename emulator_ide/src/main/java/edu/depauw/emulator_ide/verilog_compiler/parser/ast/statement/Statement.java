package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.StatementVisitor;

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
