package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;


import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Assignment;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.AssignmentList;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;

public class ContinuousAssignment extends ModItem {

    private final AssignmentList assignList;

    public ContinuousAssignment(AssignmentList assignList) {
        super(assignList.getPosition());
        this.assignList = assignList;
    }

    public Assignment getAssignment(int index){ return assignList.getAssignment(index); }

    public int numAssignments(){ return assignList.getSize(); }

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
