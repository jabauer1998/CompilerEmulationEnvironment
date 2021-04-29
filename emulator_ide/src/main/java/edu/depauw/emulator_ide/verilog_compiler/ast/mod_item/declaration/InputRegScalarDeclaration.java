package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.IdentifierList;

public class InputRegScalarDeclaration extends ScalarDeclaration{

    private final IdentifierList identList;
    
    public InputRegScalarDeclaration(IdentifierList identList){
	super(identList.getIdentifier(0).getPosition());
	this.identList = identList;
    }

    public Identifier getIdentifier(int index){
	return identList.getIdentifier(index);
    }

    public int numIdentifiers(){
	return this.identList.getSize();
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
	return modVisitor.visit(this, argv);
    }
    
}
