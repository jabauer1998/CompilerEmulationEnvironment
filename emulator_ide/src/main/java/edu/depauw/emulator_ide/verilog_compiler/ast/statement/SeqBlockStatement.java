package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
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

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
	return statVisitor.visit(this, argv);
    }
    
}
