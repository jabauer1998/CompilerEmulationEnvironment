package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_;


import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class IfElseStatement extends IfStatement {
    public final Statement falseStatement; // statement

    public IfElseStatement(Position start, Expression condition, Statement ifTrue, Statement ifFalse) {
        super(start, condition, ifTrue);
        this.falseStatement = ifFalse;
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
