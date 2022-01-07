package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ModInstanceList;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class ModInstantiation extends ModItem {

    private final Identifier      ident;
    private final ModInstanceList modList;

    public ModInstantiation(Identifier ident, ModInstanceList modList) {
        super(ident.getPosition());
        this.ident = ident;
        this.modList = modList;
    }

    public Identifier getModuleName(){ return ident; }

    public ModInstance getModInstance(int index){ return modList.getModInstance(index); }

    public int numModInstances(){ return modList.getSize(); }

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
