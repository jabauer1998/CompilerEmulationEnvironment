package edu.depauw.emulator_ide.verilog_compiler.ast.statement;


import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.common.Position;

/**
 * The assignment class is used to parse Assignment Statements Basic Assignment
 * statements usually happen in for loops
 * 
 * @author Jacob Bauer
 */
public class Assignment extends Statement {

    private Expression lValue; // the value on the left hand side of the expression
    private Expression exp;    // the expresson on the right hand side of the equals

    /**
     * The Assignment constructor takes an lvalue expression and an expression to form an
     * ssignment
     * 
     * @param lValue
     * @param exp
     */
    public Assignment(Expression lValue, Expression exp) {
        super(lValue.getPosition());
        this.lValue = lValue;
        this.exp = exp;
    }

    /**
     * The get Lvalue function returns the value of the thing being assigned to
     * 
     * @param none
     */
    public Expression getLValue(){ return lValue; }

    /**
     * The get Lvalue function returns the value of the thing being assigned to
     * 
     * @param none
     */
    public void setLValue(Expression lValue){ this.lValue = lValue; }

    /**
     * This returns the expression on the right hand side of the equals
     * 
     * @param none
     */
    public Expression getExpression(){ return exp; }

    /**
     * This returns the expression on the right hand side of the equals
     * 
     * @param none
     */
    public void setExpression(Expression exp){ this.exp = exp; }

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
