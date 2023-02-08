package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public abstract class ArrayDeclaration extends IdentDeclaration{
    public final Expression arrayIndex1;
    public final Expression arrayIndex2;
    
    public ArrayDeclaration(Position start, String arrayLabel, Expression arrayIndex1, Expression arrayIndex2){
        super(start, arrayLabel);
        this.arrayIndex1 = arrayIndex1;
        this.arrayIndex2 = arrayIndex2;
    }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
