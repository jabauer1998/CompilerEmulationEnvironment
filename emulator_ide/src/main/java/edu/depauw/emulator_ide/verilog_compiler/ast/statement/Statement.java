package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.common.Position;

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

    public abstract <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv);
    
}
