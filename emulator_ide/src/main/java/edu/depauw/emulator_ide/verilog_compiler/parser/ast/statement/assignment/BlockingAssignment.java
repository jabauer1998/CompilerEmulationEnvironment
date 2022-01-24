package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.LValue;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The Block Assignment class is used to parse blocking assignments These assignments
 * act like traditional assignments in a programming list
 * 
 * @author Jacob Bauer
 */

public class BlockingAssignment extends Assignment<LValue, Expression> {

    /**
     * The BlockAssign constuctor takes in two expressions:
     * 
     * @param lValue the value to be assigned to
     * @param exp    the expression on the right hand side of the equals
     */
    public BlockingAssignment(Position start, LValue lValue, Expression exp) {
        super(start, lValue, exp);
    }

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
