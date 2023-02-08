package edu.depauw.emulator_ide.verilog_compiler.interpreter.value;

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
    public boolean isBoolValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isShortValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isUnsignedShortValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isByteValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isIntValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isUnsignedIntValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean islongValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isUnsignedLongValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isRealValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isStringValue(){ // TODO Auto-generated method stub
        return false; 
    }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return false; }
}
