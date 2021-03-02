package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;

public abstract class VectorDeclaration extends Declaration{

    private final ConstantExpression exp1;
    private final ConstantExpression exp2;
    
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
    
}
