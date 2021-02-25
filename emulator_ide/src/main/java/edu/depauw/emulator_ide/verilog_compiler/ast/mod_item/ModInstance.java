package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class ModInstance extends ModItem{

    private final Identifier ident;
    private final ExpressionList expList;
    
    public ModInstance(Identifier ident, ExpressionList expList){
	super(ident.getPosition());
	this.ident = ident;
	this.expList = expList;
    }

    public Identifier getIdentifier(){
	return ident;
    }

    public ExpressionList getExpressionList(){
	return expList;
    }
    
}
