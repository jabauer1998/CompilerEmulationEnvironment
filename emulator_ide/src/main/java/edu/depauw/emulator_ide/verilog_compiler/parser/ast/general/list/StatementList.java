package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list;


import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import java.util.Collections;

public class StatementList extends AstNode {

    private final List<Statement> statList;

    public StatementList(List<Statement> statList) {
        super(statList.isEmpty() ? null : statList.get(0).getPosition());
        this.statList = Collections.unmodifiableList(statList);
    }

    public Statement getStatement(int index){ return statList.get(index); }

    public int getSize(){ return statList.size(); }

}
