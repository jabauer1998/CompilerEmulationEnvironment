package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.RegValue;

import java.util.List;
import java.util.Collections;

public class RegValueList extends AstNode {
    
    private final List<RegValue> regValList;
    
    public RegValueList(List<RegValue> regValList){
        super(regValList.isEmpty() ? null : regValList.get(0).getPosition());
	this.regValList = Collections.unmodifiableList(regValList);
    }

    public RegValue getRegValue(int index){
	return regValList.get(index);
    }

    public int getSize(){
	return regValList.size();
    }
    
}
