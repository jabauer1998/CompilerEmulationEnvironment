package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;

public abstract class SeqBlockStatement extends Statement{

    private final StatementList statList;
    
    public SeqBlockStatement(StatementList statList){
	super(statList.getPosition());
	this.statList = statList;
    }
    
}
