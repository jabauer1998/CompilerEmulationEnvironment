package edu.depauw.emulator_ide.verilog_interpreter.interpreter.value;

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

    @Override
    public boolean isBoolValue(){ 
        return false; 
    }

    @Override
    public boolean isShortValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedShortValue(){ 
        return false; 
    }

    @Override
    public boolean isByteValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedByteValue(){ 
        return false; 
    }

    @Override
    public boolean isIntValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedIntValue(){ 
        return false; 
    }

    @Override
    public boolean isLongValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedLongValue(){ 
        return false; 
    }

    @Override
    public boolean isRealValue(){ 
        return false; 
    }

    @Override
    public boolean isStringValue(){ 
        return true; 
    }

    @Override
    public boolean isVector(){ 
        return false; 
    }

    @Override
    public boolean isRegister(){ 
        return false; 
    }

    @Override
    public boolean isWire(){ 
        return false; 
    }
}
