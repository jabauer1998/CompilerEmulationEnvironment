package edu.depauw.emulator_ide.verilog_compiler.circuit_elem;

public abstract class CircuitElem{

    protected boolean outputSignal;
    
    abstract protected void update();
    
    public boolean getSignal(){
	return outputSignal;
    }
    
}
