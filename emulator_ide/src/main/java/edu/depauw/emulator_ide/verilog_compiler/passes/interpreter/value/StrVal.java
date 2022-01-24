package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class StrVal implements Value{
    
    private String value;

    public StrVal(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public String toString(){
        return value;
    }
    
}
