package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment;


import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

/**
 * The assignment class is used to parse Assignment Statements Basic Assignment
 * statements usually happen in for loops
 * 
 * @author Jacob Bauer
 */
public abstract class Assignment<ToType, FromType> extends AstNode implements Statement {

    public ToType leftHandSide; // the value on the left hand side of the expression
    public FromType rightHandSide;    // the expresson on the right hand side of the equals

    /**
     * The Assignment constructor takes an lvalue expression and an expression to form an
     * ssignment
     * 
     * @param lValue
     * @param exp
     */
    protected Assignment(Position start, ToType leftHandSide, FromType rightHandSide) {
        super(start);
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public abstract <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv);
}
