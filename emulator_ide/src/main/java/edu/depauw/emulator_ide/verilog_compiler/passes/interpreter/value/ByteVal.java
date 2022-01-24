package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class ByteVal implements Value{
    
    private byte value;

    public ByteVal(byte value){
        this.value = value;
    }

    public byte getValue(){
        return value;
    }

    public String toString(){
        return Byte.toString(value);
    }
    
}
