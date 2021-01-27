package edu.depauw.emulator_ide.verilog_compiler.circuitelem;

public abstract class CircuitElem{

    protected boolean outputSignal;
    
    abstract protected void update();
    
    public boolean getSignal(){
	return outputSignal;
    }
    
}
