package edu.depauw.emulator_ide.verilog_interpreter.interpreter.value;

public class UnsignedIntVal extends IntVal implements Unsigned {

    public UnsignedIntVal(int value){
        super(value);
    }

    public String toString(){
        int value = super.intValue();
        return Integer.toUnsignedString(value);
    }

    @Override
    public boolean isIntValue(){
        return false;
    }

    @Override
    public boolean isUnsignedIntValue(){
        return true;
    }
}
