package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

import java.util.ArrayList;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;

public class ArrayVal<ArrayType extends Value> implements Value {
    private final ArrayList<ArrayType> ArrList;

    public ArrayVal(int Size){
        this.ArrList = new ArrayList<ArrayType>(Size);
    }

    public ArrayType ElemAtIndex(int Index){
        return ArrList.get(Index);
    }

    public void SetElemAtIndex(int Index, ArrayType Elem){
        ArrList.set(Index, Elem);
    }

    public void AddElem(ArrayType Elem){
        ArrList.add(Elem);
    }

    @Override
    public double realValue(){
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public long longValue(){
         // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public int intValue(){
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public short shortValue(){ 
        // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public byte byteValue(){ // TODO Auto-generated method stub
        return -1; 
    }

    @Override
    public boolean boolValue(){ // TODO Auto-generated method stub
        return false; 
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
        return false; 
    }

    @Override
    public boolean isRegister(){ 
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }
    
}
