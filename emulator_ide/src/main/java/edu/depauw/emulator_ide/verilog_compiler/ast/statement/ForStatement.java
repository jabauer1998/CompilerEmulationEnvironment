package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class ForStatement extends Statement{
    
    private final Assignment init;
    private final Expression exp;
    private final Assignment change;
    private final Statement stat;
    
    public ForStatement(Assignment init, Expression exp, Assignment change, Statement stat){
	super(init.getPosition());
	this.exp = exp;
	this.init = init;
	this.change = change;
	this.stat = stat;
    }

    public Assignment getInit(){
	return init;
    }

    public Expression getExpression(){
	return exp;
    }

    public Assignment getChange(){
	return change;
    }

    public Statement getStatement(){
	return stat;
    }

    public <ModVisitType, StatVisitType, ExprVisitType> StatVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
}
