package edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.common.Position;

public class DefCaseItem extends CaseItem{
    
    private final Statement stat;
    
    public DefCaseItem(Statement stat){
	super(stat.getPosition());
	this.stat = stat;
    }

    public Statement getStatement(){
	return stat;
    }
}
