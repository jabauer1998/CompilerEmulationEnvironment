package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;

import java.util.List;

public class ModItemList extends AstNode {
    
    private final List<ModItem> modList;
    
    public ModItemList(List<ModItem> modList){
        super(modList.get(0).getPosition());
	this.modList = modList;
    }

    public ModItem getModInstance(int index){
	return modList.get(index);
    }

    public int getSize(){
	return modList.size();
    }
    
}
