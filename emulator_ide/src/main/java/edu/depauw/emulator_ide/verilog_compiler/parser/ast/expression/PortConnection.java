package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The port connection class is used for module instantiations
 * 
 * @author Jacob Bauer
 */
public class PortConnection extends AstNode implements Expression {

    public final String connectingTo;// name of the port connection
    public final Expression connectingFrom;  // what the port connection connects to

    /**
     * The port connection constructor takes in an identifier
     * 
     * @param ident          name of the port connecting to
     * @param exp.expression representing what is being connected
     */
    public PortConnection(Position start, String connectingTo, Expression connectingFrom) {
        super(start);
        this.connectingTo= connectingTo;
        this.connectingFrom = connectingFrom;
    }

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
