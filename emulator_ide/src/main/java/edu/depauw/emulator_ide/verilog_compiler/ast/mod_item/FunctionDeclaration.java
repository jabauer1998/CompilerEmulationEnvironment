package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.DeclarationList;

public class FunctionDeclaration extends ModItem{

    private final Identifier ident;
    private final DeclarationList declList;
    private final Statement stat;
    
    public FunctionDeclaration(Identifier ident, DeclarationList declList, Statement stat){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = declList;
	this.stat = stat;
	
    }

    public Identifier getIdentifier(){
	return ident;
    }

    public DeclarationList getdeclList(){
	return declList;
    }
    
    public Statement getStatement(){
	return stat;
    }
    
}
