package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class ShortVal implements Value{
    
    private short value;

    public ShortVal(short value){
        this.value = value;
    }

    public short getValue(){
        return value;
    }

    public String toString(){
        return Short.toString(value);
    }
    
}
