package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.RegValueList;

public class RegScalarDeclaration extends ScalarDeclaration{

    private final RegValueList regValueList;
    
    public RegScalarDeclaration(RegValueList regValueList){
	super(regValueList.getPosition());
	this.regValueList = regValueList;
    }

    public RegValue getRegValue(int index){
	return this.regValueList.getRegValue(index);
    }

    public int numRegValues(int index){
	return this.regValueList.getSize();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
