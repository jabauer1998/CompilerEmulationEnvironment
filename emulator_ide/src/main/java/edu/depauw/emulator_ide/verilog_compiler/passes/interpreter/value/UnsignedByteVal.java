package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class UnsignedByteVal extends ByteVal implements Unsigned{

    public UnsignedByteVal(byte value){
        super(value);
    }

    public String toString(){
        byte value = super.byteValue();
        return Integer.toUnsignedString(value);
    }
    
}
