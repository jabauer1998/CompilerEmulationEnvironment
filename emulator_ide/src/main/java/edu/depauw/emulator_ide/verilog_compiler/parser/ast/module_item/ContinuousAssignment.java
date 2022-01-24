package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item;

import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.BlockingAssignment;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class ContinuousAssignment extends AstNode implements ModuleItem {

    private final List<BlockingAssignment> assignmentList;

    public ContinuousAssignment(Position start, List<BlockingAssignment> assignmentList) {
        super(start);
        this.assignmentList = assignmentList;
    }

    public BlockingAssignment getAssignment(int index){ return assignmentList.get(index); }

    public int numAssignments(){ return assignmentList.size(); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }
}
