package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public abstract class IdentDeclaration extends AstNode implements ModuleItem{

    private final String declarationIdentifier;

    protected IdentDeclaration(Position start, String declarationIdentifier){
        super(start);
        this.declarationIdentifier = declarationIdentifier;
    }

    public String getIdentifier(){
        return declarationIdentifier;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
