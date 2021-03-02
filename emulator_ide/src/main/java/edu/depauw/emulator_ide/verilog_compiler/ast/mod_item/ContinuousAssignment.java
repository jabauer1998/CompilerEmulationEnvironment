package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Assignment;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.AssignmentList;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class ContinuousAssignment extends ModItem{

    private final AssignmentList assignList;
    
    public ContinuousAssignment(AssignmentList assignList){
	super(assignList.getPosition());
	this.assignList = assignList;
    }

    public AssignmentList getAssignmentList(){
	return assignList;
    }
}
