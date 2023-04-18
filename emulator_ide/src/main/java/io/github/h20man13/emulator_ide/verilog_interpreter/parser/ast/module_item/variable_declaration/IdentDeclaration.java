package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;


import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class IdentDeclaration extends AstNode implements ModuleItem{
    public final String declarationIdentifier;

    protected IdentDeclaration(Position start, String declarationIdentifier){
        super(start);
        this.declarationIdentifier = declarationIdentifier;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
