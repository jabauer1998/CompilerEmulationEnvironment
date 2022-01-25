package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public interface Value {
    public String toString();

    public double realValue();

    public long longValue();

    public int intValue();

    public short shortValue();

    public byte byteValue();

    public boolean boolValue();
}
