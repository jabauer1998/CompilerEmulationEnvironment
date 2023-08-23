package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede;

import io.github.H20man13.emulator_ide.gui.GuiEde;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;

public class EdeMemVal implements Value{
    private GuiEde gui;

    public EdeMemVal(GuiEde edeInstance){
        this.gui = edeInstance;
    }

    public long elemAtIndex(int index){
        return this.gui.getMemoryValue(index);
    }

    public void setElemAtIndex(int index, int value){
        this.gui.setMemoryValue(index, value);
    }

    @Override
    public double realValue(){
        return -1;    
    }

    @Override
    public long longValue(){
        return -1;
    }
    @Override
    public int intValue(){
        return -1;
    }
    @Override
    public short shortValue(){
        return -1;
    }

    @Override
    public byte byteValue(){
        return -1;
    }
    
    @Override
    public boolean boolValue(){
        return false;
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
        return false;
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
    public boolean isWire(){ // TODO Auto-generated method stub
        return false;
    }

    
}
