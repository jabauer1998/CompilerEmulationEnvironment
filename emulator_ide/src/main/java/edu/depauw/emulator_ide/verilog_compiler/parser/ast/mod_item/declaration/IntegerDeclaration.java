package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.RegValueList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.reg_value.RegValue;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.common.Position;

public class IntegerDeclaration extends Declaration {

    private final RegValueList regValueList;

    public IntegerDeclaration(RegValueList regValueList) {
        super(regValueList.getPosition());
        this.regValueList = regValueList;
    }

    public RegValue getRegValue(int index){ return this.regValueList.getRegValue(index); }

    public int numRegValues(){ return this.regValueList.getSize(); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }
}
