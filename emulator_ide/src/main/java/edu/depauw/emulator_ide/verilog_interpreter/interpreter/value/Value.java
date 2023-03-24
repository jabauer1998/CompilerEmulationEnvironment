package edu.depauw.emulator_ide.verilog_interpreter.interpreter.value;

public interface Value {
    public String toString();

    public double realValue();

    public long longValue();

    public int intValue();

    public short shortValue();

    public byte byteValue();

    public boolean boolValue();

    public boolean isBoolValue();

    public boolean isShortValue();

    public boolean isUnsignedShortValue();

    public boolean isByteValue();

    public boolean isUnsignedByteValue();

    public boolean isIntValue();

    public boolean isUnsignedIntValue();

    public boolean isLongValue();

    public boolean isUnsignedLongValue();

    public boolean isRealValue();

    public boolean isStringValue();

    public boolean isVector();

    public boolean isRegister();

    public boolean isWire();
}
