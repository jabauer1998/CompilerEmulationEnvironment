package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class IfElseStatement extends Statement{

    
    private final Expression exp; //expression 
    private final Statement stat1; //statement
    private final Statement stat2; //statement
    
    public IfElseStatement(Expression exp, Statement stat1, Statement stat2){
	super(exp.getPosition());
	this.exp = exp;
	this.stat1 = stat1;
	this.stat2 = stat2;
    }

    public Expression getExpression(){
	return exp;
    }

    public Statement getIfStatement(){
	return stat1;
    }

    public Statement getElseStatement(){
	return stat1;
    }

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <ModVisitType, StatVisitType, ExprVisitType> StatVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor, Object... argv){
	return astNodeVisitor.visit(this, argv);
    }
}
