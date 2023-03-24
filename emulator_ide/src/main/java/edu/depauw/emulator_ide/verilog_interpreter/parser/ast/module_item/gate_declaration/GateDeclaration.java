package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class GateDeclaration <OutputType> extends AstNode implements ModuleItem {
    public final OutputType gateConnections;

    protected GateDeclaration(Position start, OutputType gateConnections) {
        super(start);
        this.gateConnections = gateConnections;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
