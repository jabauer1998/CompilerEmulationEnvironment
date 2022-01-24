package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class LongVal implements Value{
    
    private long value;

    public LongVal(long value){
        this.value = value;
    }

    public long getValue(){
        return value;
    }

    public String toString(){
        return Long.toString(value);
    }
    
}
