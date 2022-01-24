package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class ModuleInstantiation extends AstNode implements ModuleItem{

    private final String      moduleType;
    private final List<ModuleInstance> modList;

    public ModuleInstantiation(Position start, String moduleType, List<ModuleInstance> modList) {
        super(start);
        this.moduleType = moduleType;
        this.modList = modList;
    }

    public String getModuleType(){ return moduleType; }

    public ModuleInstance getModInstance(int index){ return modList.get(index); }

    public int numModInstances(){ return modList.size(); }

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
