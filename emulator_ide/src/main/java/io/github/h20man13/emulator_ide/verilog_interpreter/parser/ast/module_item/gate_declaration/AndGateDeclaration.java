package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class AndGateDeclaration extends GateDeclaration<List<Expression>> {
    public AndGateDeclaration(Position start, List<Expression> inputList) { super(start, inputList); }

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
