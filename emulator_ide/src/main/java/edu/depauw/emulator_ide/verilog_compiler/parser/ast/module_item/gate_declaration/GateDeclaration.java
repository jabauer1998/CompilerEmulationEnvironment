package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public abstract class GateDeclaration <InputType> extends AstNode implements ModuleItem {

    protected final InputType gateInput;

    protected GateDeclaration(Position start, InputType gateInput) {
        super(start);
        this.gateInput = gateInput;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
