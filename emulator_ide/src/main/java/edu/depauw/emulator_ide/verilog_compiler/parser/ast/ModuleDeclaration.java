package edu.depauw.emulator_ide.verilog_compiler.parser.ast;

import java.util.Collections;
import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;

public class ModuleDeclaration extends AstNode {
    public final String      moduleName;
    public final List<ModuleItem> moduleItemList;

    public ModuleDeclaration(Position start, String moduleName, List<ModuleItem> moduleItemList) {
        super(start);
        this.moduleName = moduleName;
        this.moduleItemList = Collections.unmodifiableList(moduleItemList);
    }
}
