package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

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

    @Override
    public boolean isBoolValue(){ 
        return true;// TODO Auto-generated method stub
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
    public boolean isLongValue(){ // TODO Auto-generated method stub
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
