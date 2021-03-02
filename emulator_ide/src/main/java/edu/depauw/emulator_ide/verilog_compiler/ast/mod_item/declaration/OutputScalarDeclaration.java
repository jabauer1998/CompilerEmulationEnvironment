package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.IdentifierList;

public class OutputScalarDeclaration extends ScalarDeclaration{

    private final IdentifierList identList;
    
    public OutputScalarDeclaration(IdentifierList identList){
	super(identList.getIdentifier(0).getPosition());
	this.identList = identList;
    }

    public IdentifierList getIdentifierList(){
	return this.identList;
    }
    
}
