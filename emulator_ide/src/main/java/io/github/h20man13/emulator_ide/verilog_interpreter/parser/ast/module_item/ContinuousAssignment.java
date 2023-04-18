package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class ContinuousAssignment extends AstNode implements ModuleItem {
    public final List<BlockingAssignment> assignmentList;

    public ContinuousAssignment(Position start, List<BlockingAssignment> assignmentList) {
        super(start);
        this.assignmentList = assignmentList;
    }

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
