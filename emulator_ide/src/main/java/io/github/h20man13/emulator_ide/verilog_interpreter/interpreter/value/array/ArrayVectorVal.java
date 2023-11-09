package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.array;

import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;

public class ArrayVectorVal extends ArrayVal<VectorVal>{
    public ArrayVectorVal(int size, int vectorIndex1, int vectorIndex2){
        super(size);
        for(int i = 0; i < size; i++){
            this.AddElem(new VectorVal(vectorIndex1, vectorIndex2));
        }
    }
}
