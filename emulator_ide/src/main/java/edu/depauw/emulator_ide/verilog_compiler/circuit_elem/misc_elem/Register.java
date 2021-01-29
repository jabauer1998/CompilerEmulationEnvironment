package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

/**
 * This is the register class it will be used to simulate regs in the verilog language
 * @author
 *
 */

public class Register extends CircuitElem{
    private Wire output;
    
    public Register(boolean signal){
	outputSignal = signal;
    }

    public void setOutput(Wire output){
	this.output = output;
	this.output.setInput(this);
	update();
    };

    public void update(){
	this.output.update();
    }

    public void setSignal(boolean signal){
	if(signal != outputSignal){
	    outputSignal = signal;
	    this.output.update();
	}
    }
}
