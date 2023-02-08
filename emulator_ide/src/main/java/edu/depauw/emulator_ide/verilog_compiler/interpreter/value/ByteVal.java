package edu.depauw.emulator_ide.verilog_compiler.interpreter.value;

public class ByteVal implements Value{
    
    private byte value;

    public ByteVal(byte value){
        this.value = value;
    }

    public double realValue(){
        return (double)value;
    }

    public long longValue(){
        return (long)value;
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
        return Byte.toString(value);
    }

    @Override
    public boolean isBoolValue(){ 
        // TODO Auto-generated method stub
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
        return true; 
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
