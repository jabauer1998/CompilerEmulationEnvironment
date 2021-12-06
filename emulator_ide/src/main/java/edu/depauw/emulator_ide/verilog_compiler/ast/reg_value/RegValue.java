package edu.depauw.emulator_ide.verilog_compiler.ast.reg_value;


import edu.depauw.emulator_ide.verilog_compiler.visitor.RegValueVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.common.Position;

/**
 * The RegValue class is an extention of the AstNode class This class is used as a
 * supertype for all expression objects
 * 
 * @author Jacob Bauer
 */
public abstract class RegValue extends AstNode {

    /**
     * The RegValue constructor takes in a positionand passes it to the AstNode super
     * consturctor
     * 
     * @param position the position obect to pass to the Astnode constuctor
     */

    protected RegValue(Position position) { super(position); }

    /**
     * Every Expression type must contain a accept method so it can be visited by a visitor
     * 
     * @param RegValueVisitor visitor object to visit expression
     */

    public abstract <RegValVisitType> RegValVisitType accept(RegValueVisitor<RegValVisitType> regValueVisitor, Object... argv);

}
