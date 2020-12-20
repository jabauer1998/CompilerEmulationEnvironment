package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public class Register extends CircuitElem{

    public Register(boolean signal){
	outputs = new LinkedList<>();
	outputSignal = signal;
    }

    public void setSignal(boolean signal){
	outputSignal = signal;
	update();
    }
    
    protected void update(){
	for(CircuitElem output : outputs){
	    output.update();
	}
    }
    
    public boolean getSignal(){
	return outputSignal;
    }
}
