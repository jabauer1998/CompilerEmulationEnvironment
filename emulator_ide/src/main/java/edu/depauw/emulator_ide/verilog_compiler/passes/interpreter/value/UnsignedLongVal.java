package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedLongVal extends LongVal{

    public UnsignedLongVal(long value){
        super(value);
    }

    public String toString(){
        long value = super.getValue();
        return Long.toUnsignedString(value);
    }
    
}
