package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

public class UnsignedLongVal implements Value, Unsigned{

    private long value;

    public UnsignedLongVal(long value){
        this.value = value;
    }

    public String toString(){
        long value = longValue();
        return Long.toUnsignedString(value);
    }

    @Override
    public boolean isLongValue(){
        return false;
    }

    @Override
    public boolean isUnsignedLongValue(){
        return true;
    }

    @Override
    public double realValue(){ // TODO Auto-generated method stub
        return (double)value;
    }

    @Override
    public long longValue(){ // TODO Auto-generated method stub
        return value;
    }

    @Override
    public int intValue(){ // TODO Auto-generated method stub
        return (int)value;
    }

    @Override
    public short shortValue(){ // TODO Auto-generated method stub
        return (short)value;
    }

    @Override
    public byte byteValue(){ // TODO Auto-generated method stub
        return (byte)value;
    }

    @Override
    public boolean boolValue(){ // TODO Auto-generated method stub
        return value != 0;
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
    public boolean isRealValue(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStringValue(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    } 
}
