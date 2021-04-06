package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;

public class AllwaysStatement extends ModItem{

    private final Statement stat;
    
    public AllwaysStatement(Statement stat){
	super(stat.getPosition());
	this.stat = stat;
    }

    public Statement getStatement(){
	return stat;
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
	return modVisitor.visit(this, argv);
    }
}
