package edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public abstract class CaseItem extends AstNode{
    
    protected CaseItem(Position position){
	super(position);
    }
    
}
