package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;

/**
 * The Ternary.expression class was designed to par.E Ternary expressions Ex: (i == x) ?
 * x : y it kind of works like an if else statement
 * 
 * @author Jacob Bauer
 */
public class TernaryOperation extends AstNode implements Expression {

    private final Expression condition; // the condition phrase of the ternary Expression
    private final Expression ifTrue;      // the Expression to the left of the colon
    private final Expression ifFalse;     // the Expression to the right of the colon

    /**
     * Th ternary.expression takes in 3 expressions only one of which it returns. It can
     * return the one on the left hand side if the colon or the right.
     * 
     * @param condition the condition to be evaluated
     * @param left      the.expression to return if the condition is tr.E
     * @param right     the exprVisitoresson to evaluate if the condition is false
     */
    public TernaryOperation(Position start, Expression condition, Expression ifTrue, Expression ifFalse) {
        super(start);
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    /**
     * Returns the.expression that is tr.E
     * 
     * @param none
     */
    public Expression getExecuteIfTrue(){ return ifTrue; }


    public Expression getExecuteIfFalse(){ return ifFalse; }

    /**
     * Returns the.expression that is checked at the beginning of the ternary operation
     * 
     * @param none
     */
    public Expression getCondition(){ return condition; }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }
}
