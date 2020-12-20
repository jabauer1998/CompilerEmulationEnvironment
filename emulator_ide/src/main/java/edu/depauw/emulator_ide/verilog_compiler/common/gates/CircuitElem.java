package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public abstract class CircuitElem{

    protected boolean outputSignal;
    protected LinkedList<CircuitElem> outputs;

    abstract protected void update();
    abstract public boolean getSignal();
    
}
