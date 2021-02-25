package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public class AndGateDeclaration extends GateDeclaration{
    
    public AndGateDeclaration(ExpressionList exprList){
	super(exprList);
    }

    public ExpressionList getExpressionList(){
	return super.getExpressionList();
    }
    
}
