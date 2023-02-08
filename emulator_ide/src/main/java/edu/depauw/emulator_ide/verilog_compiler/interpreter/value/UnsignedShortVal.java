package edu.depauw.emulator_ide.verilog_compiler.interpreter.value;

public class UnsignedShortVal extends ShortVal implements Unsigned{

    public UnsignedShortVal(short value){
        super(value);
    }

    public String toString(){
        short value = super.shortValue();
        return Integer.toUnsignedString(value);
    }
    
    @Override
    public boolean isShortValue(){
        return false;
    }

    @Override
    public boolean isUnsignedShortValue(){
        return true;
    }
}
