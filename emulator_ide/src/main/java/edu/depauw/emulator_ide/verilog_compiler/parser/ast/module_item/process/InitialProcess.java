package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class InitialProcess extends Process {

    public InitialProcess(Position start, Statement statement) {
        super(start, statement);
    }

    public void executeProcess(){
        statement.accept(executionEngine);
        exitTicket.release();
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
