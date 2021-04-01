package edu.depauw.emulator_ide.verilog_compiler.ast;

import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.Declaration;

import java.util.ArrayList;
    
public class ModuleDeclaration extends AstNode{

    private final Identifier ident;
    private final DeclarationList declList;
    private final ModItemList modItemList;
    
    public ModuleDeclaration(Identifier ident){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = new DeclarationList(new ArrayList<>());
	this.modItemList = new ModItemList(new ArrayList<>());
    }

    public ModuleDeclaration(Identifier ident, DeclarationList declList){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = declList;
	this.modItemList = new ModItemList(new ArrayList<>());
    }

    public ModuleDeclaration(Identifier ident, ModItemList modItemList){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = new DeclarationList(new ArrayList<>());
	this.modItemList = modItemList;
    }

    public ModuleDeclaration(Identifier ident, DeclarationList declList, ModItemList modItemList){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = declList;
	this.modItemList = modItemList;
    }

    public Identifier getModuleName(){
	return ident;
    }

    public Declaration getParameter(int index){
	return declList.getDeclaration(index);
    }

    public int  numParameters(){
	return declList.getSize();
    }

    public ModItem getModItem(int index){
	return modItemList.getModItem(index);
    }

    public int numModItems(){
	return modItemList.getSize();
    }
}
