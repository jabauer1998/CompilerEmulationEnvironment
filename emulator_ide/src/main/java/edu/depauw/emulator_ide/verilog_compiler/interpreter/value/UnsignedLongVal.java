package edu.depauw.emulator_ide.verilog_compiler.interpreter.value;

public class UnsignedLongVal extends LongVal implements Unsigned{

    public UnsignedLongVal(long value){
        super(value);
    }

    public String toString(){
        long value = super.longValue();
        return Long.toUnsignedString(value);
    }

    @Override
    public boolean islongValue(){
        return false;
    }

    @Override
    public boolean isUnsignedLongValue(){
        return true;
    }
    
}
