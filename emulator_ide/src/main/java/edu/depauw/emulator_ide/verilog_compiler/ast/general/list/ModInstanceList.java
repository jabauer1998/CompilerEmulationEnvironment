package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModInstance;

import java.util.List;

public class ModInstanceList extends AstNode {
    
    private final List<ModInstance> modList;
    
    public ModInstanceList(List<ModInstance> modList){
        super(modList.get(0).getPosition());
	this.modList = modList;
    }

    public ModInstance getModInstance(int index){
	return modList.get(index);
    }

    public int getSize(){
	return modList.size();
    }
    
}
