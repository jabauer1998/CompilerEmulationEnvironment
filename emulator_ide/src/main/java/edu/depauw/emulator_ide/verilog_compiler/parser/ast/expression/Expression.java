package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The.expression class is an extention of the AstNode class This class is used as a
 * supertype for all.expression objects
 * 
 * @author Jacob Bauer
 */
public abstract class Expression extends AstNode {

    /**
     * The.expression constructor takes in a positionand pas Es it to the AstNode super
     * consturctor
     * 
     * @param position the position obect to pass to the Astnode constuctor
     */

    protected Expression(Position position) { super(position); }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public abstract <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv);

}
