package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede;

import io.github.H20man13.emulator_ide.gui.GuiEde;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;

public class EdeRegVal implements Value{
    private String regString;
    private GuiEde gui;

    public EdeRegVal(String regValueString, GuiEde edeInstance){
        this.gui = edeInstance;
        this.regString = regValueString;
    }

    @Override
    public double realValue(){
        return (double)gui.getRegisterValue(regString);    
    }

    @Override
    public long longValue(){
        return (long)gui.getRegisterValue(regString);
    }
    @Override
    public int intValue(){
        return (int)gui.getRegisterValue(regString);
    }
    @Override
    public short shortValue(){
        return (short)gui.getRegisterValue(regString);
    }

    @Override
    public byte byteValue(){
        return (byte)gui.getRegisterValue(regString);
    }
    
    @Override
    public boolean boolValue(){
        return gui.getRegisterValue(regString) != 0;
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
