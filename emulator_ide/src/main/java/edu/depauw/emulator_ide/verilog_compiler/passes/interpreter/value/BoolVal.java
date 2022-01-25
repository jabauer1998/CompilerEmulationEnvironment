package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class BoolVal implements Value{
    
    private boolean value;

    public BoolVal(boolean value){
        this.value = value;
    }

    public double realValue(){
        return (double)(value ? 1 : 0);
    }

    public long longValue(){
        return value ? 1 : 0;
    }

    public int intValue(){
        return value ? 1 : 0;
    }

    public short shortValue(){
        return (short)(value ? 1 : 0);
    }

    public byte byteValue(){
        return (byte)(value ? 1 : 0);
    }

    public boolean boolValue(){
        return value;
    }


    public String toString(){
        return Boolean.toString(value);
    }
    
}
