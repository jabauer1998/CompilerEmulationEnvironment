package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;


import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Assignment;

import java.util.List;
import java.util.Collections;

public class AssignmentList extends AstNode {

    private final List<Assignment> assignList;

    public AssignmentList(List<Assignment> assignList) {
        super(assignList.isEmpty() ? null : assignList.get(0).getPosition());
        this.assignList = Collections.unmodifiableList(assignList);
    }

    public Assignment getAssignment(int index){ return assignList.get(index); }

    public int getSize(){ return assignList.size(); }

}
