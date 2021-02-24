package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;

public class CaseStatement extends Statement{

    private final Expression exp;
    private final CaseItemList itemList;
    
    public CaseStatement(Expression exp, CaseItemList itemList){
	super(exp.getPosition());
	this.exp = exp;
	this.itemList = itemList;
    }

    public CaseItemList getItemList(){
	return itemList;
    }

    public Expression getExpression(){
	return exp;
    }
    
}
