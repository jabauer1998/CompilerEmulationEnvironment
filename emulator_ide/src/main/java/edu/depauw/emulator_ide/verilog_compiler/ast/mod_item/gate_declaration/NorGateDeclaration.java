package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public class NorGateDeclaration extends GateDeclaration{
    
    public NorGateDeclaration(ExpressionList exprList){
	super(exprList);
    }

    public ExpressionList getExpressionList(){
	return super.getExpressionList();
    }
    
}
