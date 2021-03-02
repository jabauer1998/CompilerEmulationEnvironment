package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.RegValueList;

public class IntegerDeclaration extends Declaration{

    private final RegValueList regValueList;
    
    public IntegerDeclaration(RegValueList regValueList){
	super(regValueList.getPosition());
	this.regValueList = regValueList;
    }

    public RegValueList getRegValueList(){
	return regValueList;
    }
}
