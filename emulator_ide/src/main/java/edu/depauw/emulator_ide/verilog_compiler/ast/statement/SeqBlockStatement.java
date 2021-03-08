package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;

public class SeqBlockStatement extends Statement{

    private final StatementList statList;
    
    public SeqBlockStatement(StatementList statList){
	super(statList.getPosition());
	this.statList = statList;
    }

    public int numStatements(){
	return statList.getSize();
    }

    public Statement getStatement(int index){
	return statList.getStatement(index);
    }

    public <ModVisitType, StatVisitType, ExprVisitType> StatVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
