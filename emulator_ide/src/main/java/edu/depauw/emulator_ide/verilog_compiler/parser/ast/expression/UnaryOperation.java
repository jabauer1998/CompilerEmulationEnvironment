package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
/**
 * The Unary Operation class is used to par.E unary operations unary operations are
 * operations with one operator and an.expression ex: - (1 + 5) the.expression is 1 + 5
 * and the negation is the unary operation
 * 
 * @author Jacob Bauer
 */

public class UnaryOperation extends Expression {

    //Enum types to know the correct type of operation
    public enum Type{ PLUS, MINUS, BNEG, LNEG, BAND, BNAND, BOR, BXOR, BXNOR }

    public static Type tokenToUnaryOp(Token.Type type){
        switch(type){
            case PLUS: return Type.PLUS;
            case MINUS: return Type.MINUS;
            case BNEG: return Type.BNEG;
            case LNEG: return Type.LNEG;
            case BAND: return Type.BAND;
            case BNAND: return Type.BNAND;
            case BOR: return Type.BOR;
            case BXOR: return Type.BXOR;
            default: return Type.BXNOR;
        }
    }

    private Expression right; // the right hand side of the equation
    private final Type op;   // the operator

    /**
     * The UnaryOperation constuctor is used to create a Unary operation
     * 
     * @param op               operator to use
     * @param right.expression to apply the operator to
     */

    public UnaryOperation(Type op, Expression right) {
        super(right.getPosition());
        this.right = right;
        this.op = op;
    }

    /**
     * The right operator is used to get the right hand side of an.expression
     * 
     * @param none
     */

    public Expression getRight(){ return right; }

    /**
     * The right operator is used to get the right hand side of an.expression
     * 
     * @param none
     */

    public void setRight(Expression expression){ this.right = expression; }

    /**
     * .expression used to get the operator used in the Operation
     * 
     * @param none
     */

    public Type getOp(){ return op; }

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
