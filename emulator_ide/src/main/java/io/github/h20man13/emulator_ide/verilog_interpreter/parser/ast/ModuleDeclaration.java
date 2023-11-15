package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast;

import java.util.Collections;
import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;

public class ModuleDeclaration extends AstNode {
    public final String      moduleName;
    public final List<ModuleItem> moduleItemList;

    public ModuleDeclaration(Position start, String moduleName, List<ModuleItem> moduleItemList) {
        super(start);
        this.moduleName = moduleName;
        this.moduleItemList = Collections.unmodifiableList(moduleItemList);
    }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("module ");
        sb.append(moduleName);
        sb.append(";\n");
        for(ModuleItem item : moduleItemList){
            sb.append(item.toString());
            sb.append("\n");
        }
        sb.append("endmodule\n");
        return sb.toString();
    }
}
