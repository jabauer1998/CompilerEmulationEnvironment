package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class NandGateDeclaration extends GateDeclaration< List<Expression> > {
    public NandGateDeclaration(Position start, List<Expression> inputList) { super(start, inputList); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("nand(");
        List<Expression> gateList = gateConnections;
        int size = gateList.size();
        for(int i = 0; i < size; i++){
            Expression exp = gateList.get(i);
            sb.append(exp.toString());
            if(i < size - 1){
                sb.append(", ");
            }
        }
        sb.append(");");
        return sb.toString();
    }

}
