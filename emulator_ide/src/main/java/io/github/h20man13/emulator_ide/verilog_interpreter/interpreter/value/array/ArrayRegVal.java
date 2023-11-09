package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;

public class ArrayRegVal extends ArrayVal<RegVal> {
    public ArrayRegVal(int size){
        super(size);
        for(int i = 0; i < size; i++){
            this.AddElem(new RegVal(false));
        }
    }
}
