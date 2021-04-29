package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;

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

    public Expression getExpression(int index){
	return expList.getExpression(index);
    }

    public void setExpression(int index, Expression exp){
	this.expList.setExpression(index, exp);
    }

    public int numExpressions(){
	return expList.getSize();
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
	return modVisitor.visit(this, argv);
    }
    
}
