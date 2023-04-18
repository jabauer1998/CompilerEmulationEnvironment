package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class ForStatement extends AstNode implements Statement {

    public final BlockingAssignment init;
    public final Expression       exp;
    public final BlockingAssignment change;
    public final Statement  stat;

    public ForStatement(Position start, BlockingAssignment init, Expression exp, BlockingAssignment change, Statement stat) {
        super(start);
        this.exp = exp;
        this.init = init;
        this.change = change;
        this.stat = stat;
    }

    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
        return statVisitor.visit(this, argv);
    }
}
