package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration;

import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public class FunctionDeclaration extends ProcedureDeclaration {

    public final ModuleItem      functionName;

    public FunctionDeclaration(Position start, ModuleItem functionName, List<ModuleItem> paramaters, Statement stat) {
        super(start, paramaters, stat);
        this.functionName = functionName;
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
