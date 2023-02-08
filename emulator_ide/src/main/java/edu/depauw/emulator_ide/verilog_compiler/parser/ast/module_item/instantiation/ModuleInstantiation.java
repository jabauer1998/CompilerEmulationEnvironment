package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public class ModuleInstantiation extends AstNode implements ModuleItem{

    public final String      moduleType;
    public final List<ModuleInstance> modList;

    public ModuleInstantiation(Position start, String moduleType, List<ModuleInstance> modList) {
        super(start);
        this.moduleType = moduleType;
        this.modList = modList;
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
