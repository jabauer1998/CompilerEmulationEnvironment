package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.RegValue;
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

    public int numRegValues(){
	return this.regValueList.getSize();
    }

    /** The ast node visitor will allow the user to pass down data through the argument vector. The accept method is needed to know which visit method to run.
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
	return modVisitor.visit(this, argv);
    }
    
}
