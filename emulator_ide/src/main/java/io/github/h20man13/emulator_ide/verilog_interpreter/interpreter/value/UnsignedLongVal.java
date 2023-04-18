package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

public class UnsignedLongVal extends LongVal implements Unsigned{

    public UnsignedLongVal(long value){
        super(value);
    }

    public String toString(){
        long value = super.longValue();
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
    
}
