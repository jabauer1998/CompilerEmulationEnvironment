package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.LValue;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class NonBlockingAssignment extends Assignment<List<LValue>, List<Expression>> {

    public NonBlockingAssignment(Position start, List<LValue> lValue, List<Expression> exp) {
        super(start, lValue, exp);
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
