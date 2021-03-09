package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;

import java.util.List;
import java.util.Collections;

public class RegValueList extends AstNode {
    
    private final List<Expression> regValList;
    
    public RegValueList(List<Expression> regValList){
        super(regValList.get(0).getPosition());
	this.regValList = Collections.unmodifiableList(regValList);
    }

    public Expression getRegValue(int index){
	return regValList.get(index);
    }

    public int getSize(){
	return regValList.size();
    }
    
}
