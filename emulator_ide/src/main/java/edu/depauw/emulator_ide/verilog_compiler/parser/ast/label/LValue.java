package edu.depauw.emulator_ide.verilog_compiler.parser.ast.label;

import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;

/**
 * The Identifier class is used to define an Identifier An identifier is any set of
 * characters that isnt considered a verilog keyword. This includes variable names,
 * function names, module names Etc...
 */

public interface LValue {
    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> environment);
}
