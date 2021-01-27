package edu.depauw.emulator_ide.verilog_compiler.;

public class Register extends MiscElem{
    private CircuitElem output;
    
    public Register(boolean signal){
	outputSignal = signal;
    }

    public void setSignal(boolean signal){
	if(signal != outputSignal){
	    outputSignal = signal;
	    output.update();
	}
    }
}
