package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

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

    public int numExpressions(){
	return expList.getSize();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
