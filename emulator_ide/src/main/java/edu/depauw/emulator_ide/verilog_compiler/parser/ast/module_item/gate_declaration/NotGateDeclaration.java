package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class NotGateDeclaration extends GateDeclaration<Expression> {

    public NotGateDeclaration(Position start, Expression gateInput) { super(start, gateInput); }

    public Expression getGateInput(){
        return gateInput;
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

