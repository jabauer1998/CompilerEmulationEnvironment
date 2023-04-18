package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;

public interface VectorDeclarationInterface {
    Expression GetIndex1();
    Expression GetIndex2();
}
