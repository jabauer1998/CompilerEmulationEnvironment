package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web;


import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;

public abstract class Web extends CircuitElem {
    // For now this is just a place holder but this is to provide more specific support for
    // the CircuitElem class

    public abstract void addOutput(CircuitElem elem);

    public abstract boolean getStateSignal();
    
}
