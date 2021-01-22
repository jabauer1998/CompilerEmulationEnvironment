package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public class Wire extends CircuitElem{
    public CircuitElem input;

    public Wire(CircuitElem input){
	outputs = new LinkedList<>();
	outputSignal = input.getSignal();
	this.input = input;
    }
    
    protected void update(){
	if(input.getSignal() != outputSignal){
	    outputSignal = input.getSignal();
	    for(CircuitElem output : outputs){
		output.update();
	    }
	}
    }
    
    public boolean getSignal(){
	return outputSignal;
    }
}
