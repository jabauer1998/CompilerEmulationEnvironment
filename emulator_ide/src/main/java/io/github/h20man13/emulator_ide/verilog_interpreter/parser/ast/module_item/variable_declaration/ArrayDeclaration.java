package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class ArrayDeclaration extends IdentDeclaration{
    public final Expression arrayIndex1;
    public final Expression arrayIndex2;
    
    public ArrayDeclaration(Position start, String arrayLabel, Expression arrayIndex1, Expression arrayIndex2){
        super(start, arrayLabel);
        this.arrayIndex1 = arrayIndex1;
        this.arrayIndex2 = arrayIndex2;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append('[');
        sb.append(this.arrayIndex1);
        sb.append(':');
        sb.append(this.arrayIndex2);
        sb.append(']');
        return sb.toString();
    }
}
