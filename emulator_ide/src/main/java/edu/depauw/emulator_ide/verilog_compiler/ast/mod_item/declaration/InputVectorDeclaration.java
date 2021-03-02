package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.IdentifierList;

public class InputVectorDeclaration extends VectorDeclaration{

    private final IdentifierList identList;

    public InputVectorDeclaration(ConstantExpression exp1, ConstantExpression exp2, IdentifierList identList){
	super(exp1, exp2);
	this.identList = identList;
    }
    
}
