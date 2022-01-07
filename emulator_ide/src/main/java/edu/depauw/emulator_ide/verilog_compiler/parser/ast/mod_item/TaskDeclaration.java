package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.DeclarationList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration.Declaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class TaskDeclaration extends ModItem {

    private final Identifier      ident;
    private final DeclarationList declList;
    private final Statement       stat;

    public TaskDeclaration(Identifier ident, DeclarationList declList, Statement stat) {
        super(ident.getPosition());
        this.ident = ident;
        this.declList = declList;
        this.stat = stat;

    }

    public Identifier getTaskName(){ return ident; }

    public int numDeclarations(){ return declList.getSize(); }

    public Declaration getDeclaration(int index){ return declList.getDeclaration(index); }

    public Statement getStatement(){ return stat; }

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
