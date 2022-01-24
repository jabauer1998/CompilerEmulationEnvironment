package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class IntVal implements Value{
    
    private int value;

    public IntVal(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public String toString(){
        return Integer.toString(value);
    }
    
}
