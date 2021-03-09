package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;

import java.util.List;
import java.util.Collections;

public class ExpressionList extends AstNode {
    
    private final List<Expression> expList;
    
    public ExpressionList(List<Expression> expList){
        super(expList.get(0).getPosition());
	this.expList = Collections.unmodifiableList(expList);
    }

    public Expression getExpression(int index){
	return expList.get(index);
    }

    public int getSize(){
	return expList.size();
    }
    
}
