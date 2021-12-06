package edu.depauw.emulator_ide.verilog_compiler.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The port connection class is used for module instantiations
 * 
 * @author Jacob Bauer
 */
public class PortConnection extends Expression {

    private final Identifier ident;// name of the port connection
    private Expression       exp;  // what the port connection equals

    /**
     * The port connection constructor takes in an identifier
     * 
     * @param ident          name of the port connecting to
     * @param exp.expression representing what is being connected
     */
    public PortConnection(Identifier ident, Expression exp) {
        super(ident.getPosition());
        this.ident = ident;
        this.exp = exp;
    }

    /**
     * Retr.Eves the.expression that represents what is being connected
     * 
     * @param none
     */
    public Expression getExpression(){ return exp; }

    /**
     * Retr.Eves the.expression that represents what is being connected
     * 
     * @param none
     */
    public void setExpression(Expression exp){ this.exp = exp; }

    /**
     * Returns the name of the identifier that we are connected to
     * 
     * @param none
     */
    public Identifier getIdentifier(){ return ident; }

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
