package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.CaseItem;

import java.util.List;
import java.util.Collections;

public class CaseItemList extends AstNode {
    
    private final List<CaseItem> caseItemList;
    
    public CaseItemList(List<CaseItem> caseItemList){
        super(caseItemList.isEmpty() ? null : caseItemList.get(0).getPosition());
        this.caseItemList = Collections.unmodifiableList(caseItemList);
    }

    public CaseItem  getCaseItem(int index){
    	return caseItemList.get(index);
    }

    public int getSize(){
    	return caseItemList.size();
    }
    
}
