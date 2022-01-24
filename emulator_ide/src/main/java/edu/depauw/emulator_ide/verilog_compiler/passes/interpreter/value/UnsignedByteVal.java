package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedByteVal extends ByteVal {

    public UnsignedByteVal(byte value){
        super(value);
    }

    public String toString(){
        byte value = super.getValue();
        return Integer.toUnsignedString(value);
    }
    
}
