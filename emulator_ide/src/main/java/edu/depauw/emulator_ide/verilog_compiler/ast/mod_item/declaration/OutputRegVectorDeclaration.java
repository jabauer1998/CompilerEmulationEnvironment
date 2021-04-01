package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.RegValueList;

public class OutputRegVectorDeclaration extends VectorDeclaration{

    private final RegValueList regValueList;
    
    public OutputRegVectorDeclaration(ConstantExpression exp1, ConstantExpression exp2, RegValueList regValueList){
	super(exp1, exp2);
	this.regValueList = regValueList;
    }

    public Expression getRegValue(int index){
	return this.regValueList.getRegValue(index);
    }

    public int numRegValues(){
	return this.regValueList.getSize();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
