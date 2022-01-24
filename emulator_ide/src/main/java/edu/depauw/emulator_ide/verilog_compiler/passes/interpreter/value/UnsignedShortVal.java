package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedShortVal extends ShortVal{

    public UnsignedShortVal(short value){
        super(value);
    }

    public String toString(){
        short value = super.getValue();
        return Integer.toUnsignedString(value);
    }
    
}
