package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;

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
    public boolean isLongValue(){ // TODO Auto-generated method stub
        return true; 
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

    @Override
    public Value getShallowSlice(int startIndex, int endIndex) throws Exception{
        if(startIndex > 64 || startIndex < 0){
            throw new UnsupportedOperationException("Error startIndex is out of bounds at " +startIndex);
        }

        if(endIndex > 64 || endIndex < 0){
            throw new UnsupportedOperationException("Error endIndex is outof bounds at " + endIndex);
        }

        int start = (startIndex <= endIndex) ? startIndex : endIndex;
        int end = (startIndex >= endIndex) ? startIndex : endIndex;

        int size = end - start + 1;

        long val = (value >> startIndex);
        long toKeepMask = ((1 << size) - 1);

        return Utils.getOptimalUnsignedForm(val & toKeepMask);
    }
}
