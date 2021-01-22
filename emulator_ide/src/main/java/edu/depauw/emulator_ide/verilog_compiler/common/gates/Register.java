package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public class Register extends CircuitElem{
    private CircuitElem input;
    
    public Register(boolean signal){
	outputs = new LinkedList<>();
	input = null;
	outputSignal = signal;
    }

    public Register(CircuitElem input){
	outputs = new LinkedList<>();
	this.input = input;
	outputSignal = input.getSignal();
    }

    public void setSignal(boolean signal){
	outputSignal = signal;
	update();
    }
    
    protected void update(){
	for(CircuitElem output : outputs){
		output.update();
	}
	if (input != null) {
	    outputSignal = input.getSignal();
	}
    }
    
    public boolean getSignal(){
	return outputSignal;
    }
}
