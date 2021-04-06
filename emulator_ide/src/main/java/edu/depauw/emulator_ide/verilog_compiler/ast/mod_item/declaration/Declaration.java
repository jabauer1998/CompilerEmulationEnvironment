package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public abstract class Declaration extends ModItem{
    
    protected Declaration(Position position){
	super(position);
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
    
}
