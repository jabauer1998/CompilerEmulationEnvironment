package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list;


import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.ModItem;
import java.util.Collections;

public class ModItemList extends AstNode {

    private final List<ModItem> modList;

    public ModItemList(List<ModItem> modList) {
        super(modList.isEmpty() ? null : modList.get(0).getPosition());
        this.modList = Collections.unmodifiableList(modList);
    }

    public ModItem getModItem(int index){ return modList.get(index); }

    public int getSize(){ return modList.size(); }

}
