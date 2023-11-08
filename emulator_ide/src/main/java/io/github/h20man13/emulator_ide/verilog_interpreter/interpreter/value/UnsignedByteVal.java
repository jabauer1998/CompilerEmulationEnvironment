package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

public class UnsignedByteVal implements Value, Unsigned{
    private Byte value;

    public UnsignedByteVal(byte value){
        this.value = value;
    }

    public UnsignedByteVal(int value){
        this.value = (byte)value;
    }

    public String toString(){
        byte value = this.byteValue();
        return Integer.toUnsignedString(value);
    }

    @Override
    public boolean isByteValue(){
        return false;
    }

    @Override
    public boolean isUnsignedByteValue(){
        return true;
    }

    public long longValue(){
        return Byte.toUnsignedLong(value);
    }

    public int intValue(){
        return Byte.toUnsignedInt(value);
    }

    public short shortValue(){
        return (short)Byte.toUnsignedInt(value);
    }

    public byte byteValue(){
        return value;
    }

    @Override
    public double realValue(){ // TODO Auto-generated method stub
    return (double)value; }

    @Override
    public boolean boolValue(){ // TODO Auto-generated method stub
    return value != 0; }

    @Override
    public boolean isBoolValue(){
        return false;
    }

    @Override
    public boolean isShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRealValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isStringValue(){ // TODO Auto-generated method stub
    return false; }

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
