package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

public class AllwaysStatement extends ModItem{

    private final Statement stat;
    
    public AllwaysStatement(Statement stat){
	super(stat.getPosition());
	this.stat = stat;
    }

    public Statement getStatement(){
	return stat;
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
