package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class BoolVal implements Value{
    
    private boolean value;

    public BoolVal(boolean value){
        this.value = value;
    }

    public boolean getValue(){
        return value;
    }

    public String toString(){
        return Boolean.toString(value);
    }
    
}
