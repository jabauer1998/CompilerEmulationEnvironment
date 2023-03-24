package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class ModuleInstance extends AstNode implements ModuleItem {

    public final String     instanceName;
    public final List<Expression> expList;

    public ModuleInstance(Position start, String instanceName, List<Expression> expList) {
        super(start);
        this.instanceName = instanceName;
        this.expList = expList;
    }

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
