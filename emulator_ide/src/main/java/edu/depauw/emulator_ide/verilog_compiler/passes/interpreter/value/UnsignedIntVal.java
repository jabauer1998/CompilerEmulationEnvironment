package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedIntVal extends IntVal {

    public UnsignedIntVal(int value){
        super(value);
    }

    public String toString(){
        int value = super.getValue();
        return Integer.toUnsignedString(value);
    }
    
}
