package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label;

import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.SymbolTable;

/**
 * The Identifier class is used to define an Identifier An identifier is any set of
 * characters that isnt considered a verilog keyword. This includes variable names,
 * function names, module names Etc...
 */

public interface LValue {
    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> environment);
}
