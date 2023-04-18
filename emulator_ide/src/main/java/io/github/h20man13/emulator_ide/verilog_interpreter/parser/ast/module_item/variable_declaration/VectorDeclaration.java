package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;

public class VectorDeclaration {
    public final Expression vectorIndex1;
    public final Expression vectorIndex2;

    public VectorDeclaration(Expression vectorIndex1, Expression vectorIndex2){
        this.vectorIndex1 = vectorIndex1;
        this.vectorIndex2 = vectorIndex2;
    }
}
