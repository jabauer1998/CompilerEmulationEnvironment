package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;

import java.util.List;

public class StatementList extends AstNode {
    
    private final List<Statement> statList;
    
    public StatementList(List<Statement> statList){
        super(statList.get(0).getPosition());
	this.statList = statList;
    }

    public Statement getStatement(int index){
	return statList.get(index);
    }

    public int getSize(){
	return statList.size();
    }
    
}
