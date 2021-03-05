package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

/** The empty expression class is designed as a place holder to parse an empty expression
 * @author Jacob Bauer
 */
public class EmptyExpression extends Expression{

    /** The empty expression constructor only takes a position then it passes that up to the Expression constructor
     *  @param position Position of the empty expression
     */
    
    public EmptyExpression(Position position){
	super(position);
    }

    /** The accept method is required to visit empty expressions with the visitor design pattern
     * @param astNodeVisitor the visitor object we want to use to visit the Expression
     */
    public <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
