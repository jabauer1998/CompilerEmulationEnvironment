package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;

public class CaseStatement extends Statement{

    private final Expression exp;
    private final CaseItemList itemList;
    
    public CaseStatement(Expression exp, CaseItemList itemList){
	super(exp.getPosition());
	this.exp = exp;
	this.itemList = itemList;
    }

    public CaseItem getCaseItem(int index){
	return itemList.getCaseItem(index);
    }

    public int  numCaseItems(){
	return itemList.getSize();
    }

    public Expression getExpression(){
	return exp;
    }

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <ModVisitType, StatVisitType, ExprVisitType> StatVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor, Object... argv){
	return astNodeVisitor.visit(this, argv);
    }
}
