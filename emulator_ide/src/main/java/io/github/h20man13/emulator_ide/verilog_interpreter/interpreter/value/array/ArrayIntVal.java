package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;

public class ArrayIntVal extends ArrayVal<UnsignedIntVal> {
    public ArrayIntVal(int size){
        super(size);
        for(int i = 0; i < size; i++){
            this.AddElem(new UnsignedIntVal(0));
        }
    }
}
