package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

/** The Statement abstract class is used to represent Statement abstract syntax tree nodes in the verilog
 * language. This will come in handy when creating graphical user interfaces.
 * @author Jacob Bauer
 */
public abstract class Statement extends AstNode{

    /** 
     * The Statement constructor is used to Construct a Statement AstNode
     * @param position the position of the start of a statement
     */
    protected Statement(Position position){
	super(position);
    }

    public abstract <ModVisitType, StatVisitType, ExprVisitType> ExprVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor);
    
}
