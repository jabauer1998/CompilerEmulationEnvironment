package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class StrVal implements Value{
    
    private String value;

    public StrVal(String value){
        this.value = value;
    }

    public double realValue(){
        return Double.parseDouble(value);
    }

    public long longValue(){
        return Long.parseLong(value);
    }

    public int intValue(){
        return Integer.parseInt(value);
    }

    public short shortValue(){
        return Short.parseShort(value);
    }

    public byte byteValue(){
        return Byte.parseByte(value);
    }

    public boolean boolValue(){
        return Boolean.parseBoolean(value);
    }

    public String toString(){
        return value;
    }
    
}
