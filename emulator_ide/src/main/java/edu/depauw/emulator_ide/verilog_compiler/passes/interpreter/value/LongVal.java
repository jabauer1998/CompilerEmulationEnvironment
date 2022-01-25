package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class LongVal implements Value{
    
    private long value;

    public LongVal(long value){
        this.value = value;
    }

    public double realValue(){
        return (double)value;
    }

    public long longValue(){
        return value;
    }

    public int intValue(){
        return (int)value;
    }

    public short shortValue(){
        return (short)value;
    }

    public byte byteValue(){
        return (byte)value;
    }

    public boolean boolValue(){
        return value != 0;
    }


    public String toString(){
        return Long.toString(value);
    }
    
}
