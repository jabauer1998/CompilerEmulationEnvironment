package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedIntVal extends IntVal implements Unsigned {

    public UnsignedIntVal(int value){
        super(value);
    }

    public String toString(){
        int value = super.intValue();
        return Integer.toUnsignedString(value);
    }
    
}
