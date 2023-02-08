package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching._if_;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

public class IfStatement extends AstNode implements Statement {

    public final Expression    condition;  // expression
    public final Statement trueStatement; // statement

    public IfStatement(Position start, Expression condition, Statement trueStatement) {
        super(start);
        this.condition = condition;
        this.trueStatement = trueStatement;
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
