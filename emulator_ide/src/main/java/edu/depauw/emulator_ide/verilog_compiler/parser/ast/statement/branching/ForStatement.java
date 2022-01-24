package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.BlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

public class ForStatement extends AstNode implements Statement {

    private final BlockingAssignment init;
    private Expression       exp;
    private final BlockingAssignment change;
    private final Statement  stat;

    public ForStatement(Position start, BlockingAssignment init, Expression exp, BlockingAssignment change, Statement stat) {
        super(start);
        this.exp = exp;
        this.init = init;
        this.change = change;
        this.stat = stat;
    }

    public BlockingAssignment getInit(){ return init; }

    public Expression getExpression(){ return exp; }

    public BlockingAssignment getChange(){ return change; }

    public Statement getStatement(){ return stat; }

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
