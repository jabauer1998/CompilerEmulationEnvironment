package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.RegValueList;

public class RegVectorDeclaration extends VectorDeclaration{

    private final RegValueList regValueList;
    
    public RegVectorDeclaration(ConstantExpression exp1, ConstantExpression exp2, RegValueList regValueList){
	super(exp1, exp2);
	this.regValueList = regValueList;
    }

    public RegValueList getRegValueList(){
	return this.regValueList;
    }
    
}
