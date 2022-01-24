package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration;

import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public abstract class ProcedureDeclaration extends AstNode implements ModuleItem {

    private final List<ModuleItem> paramaters;
    private final Statement        stat;

    public ProcedureDeclaration(Position start, List<ModuleItem> paramaters, Statement stat) {
        super(start);
        this.paramaters = paramaters;
        this.stat = stat;
    }

    public int numberofParamaters(){ return paramaters.size(); }

    public ModuleItem getParamater(int index){ return paramaters.get(index); }

    public Statement getStatement(){ return stat; }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
