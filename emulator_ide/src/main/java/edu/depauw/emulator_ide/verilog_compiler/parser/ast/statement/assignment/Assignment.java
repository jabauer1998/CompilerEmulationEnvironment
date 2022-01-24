package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The assignment class is used to parse Assignment Statements Basic Assignment
 * statements usually happen in for loops
 * 
 * @author Jacob Bauer
 */
public abstract class Assignment<ToType, FromType> extends AstNode implements Statement {

    private ToType leftHandSide; // the value on the left hand side of the expression
    private FromType rightHandSide;    // the expresson on the right hand side of the equals

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
     * The get Lvalue function returns the value of the thing being assigned to
     * 
     * @param none
     */
    public ToType getLeftHandSide(){ return leftHandSide; }

    /**
     * This returns the expression on the right hand side of the equals
     * 
     * @param none
     */
    public FromType getRightHandSide(){ return rightHandSide; }

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
