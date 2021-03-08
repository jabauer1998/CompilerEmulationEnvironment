package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ModInstanceList;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

public class ModInstantiation extends ModItem{

    private final Identifier ident;
    private final ModInstanceList modList;
    
    public ModInstantiation(Identifier ident, ModInstanceList modList){
	super(ident.getPosition());
	this.ident = ident;
	this.modList = modList;
    }

    public Identifier getModuleName(){
	return ident;
    }

    public ModInstance getModInstance(int index){
	return modList.getModInstance(index);
    }

    public int numModInstances(){
	return modList.getSize();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
