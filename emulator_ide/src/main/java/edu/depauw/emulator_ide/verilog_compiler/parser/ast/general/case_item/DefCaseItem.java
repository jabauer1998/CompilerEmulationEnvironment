package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.case_item;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;

public class DefCaseItem extends CaseItem {

    private final Statement stat;

    public DefCaseItem(Statement stat) {
        super(stat.getPosition());
        this.stat = stat;
    }

    public Statement getStatement(){ return stat; }
}
