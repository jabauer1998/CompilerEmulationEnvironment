package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.case_item;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;

public abstract class CaseItem extends AstNode {

    protected CaseItem(Position position) { super(position); }

    public abstract Statement getStatement();

}
