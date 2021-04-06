package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.DeclarationList;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.Declaration;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;

public class MacroDefinition extends ModItem{

    private final Identifier ident;
    private final Expression exp;
    
    public MacroDefinition(Identifier ident, Expression exp){
	super(ident.getPosition());
	this.ident = ident;
	this.exp = exp;
    }

    public Identifier getMacroName(){
	return ident;
    }
    
    public Expression getExpression(){
	return exp;
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
	return modVisitor.visit(this, argv);
    }
    
}
