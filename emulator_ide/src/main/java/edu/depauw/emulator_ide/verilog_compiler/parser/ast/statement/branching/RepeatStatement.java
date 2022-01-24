package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

public class RepeatStatement extends AstNode implements Statement {

    private final Statement stat; // Statement
    private final Expression      exp;  // Expression

    public RepeatStatement(Position start, Expression exp, Statement stat) {
        super(start);
        this.stat = stat;
        this.exp = exp;
    }

    public Statement getStatement(){ return stat; }

    public Expression getExpression(){ return exp; }

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
