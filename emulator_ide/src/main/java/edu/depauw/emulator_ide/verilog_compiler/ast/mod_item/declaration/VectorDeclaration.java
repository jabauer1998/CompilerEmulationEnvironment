package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public abstract class VectorDeclaration extends Declaration{

    private ConstantExpression exp1;
    private ConstantExpression exp2;
    
    protected VectorDeclaration(ConstantExpression exp1, ConstantExpression exp2){
	super(exp1.getPosition());
	this.exp1 = exp1;
	this.exp2 = exp2;
    }

    public ConstantExpression getExpression1(){
	return exp1;
    }

    public ConstantExpression getExpression2(){
	return exp2;
    }

    public void setExpression1(Expression exp){
	this.exp1 = new ConstantExpression(exp);
    }

    public void setExpression2(Expression exp){
	this.exp2 = new ConstantExpression(exp);
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
    
}
