package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

public class NandGateDeclaration extends GateDeclaration{
    
    public NandGateDeclaration(ExpressionList exprList){
	super(exprList);
    }

    public ExpressionList getExpressionList(){
	return super.getExpressionList();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
