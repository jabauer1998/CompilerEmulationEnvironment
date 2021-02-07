package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

/**
 * This is the register class it will be used to simulate regs in the verilog language
 * These are kind of like variables but they can be set and it will update the wires accordingly
 * @author Jacob Bauer
 *
 */

public class Register extends CircuitElem{
    private Wire output;
    
    public Register(boolean signal){
	this.outputSignal = signal;
	this.output = null;
    }

    public void setOutput(Wire output){
	this.output = output;
	if(this.output != null){
	    this.output.setInput(this);
	}
	update();
    };

    public void update(){
	if(output != null){
	    output.update();
	}
    }

    public void setSignal(boolean signal){
	if(signal != outputSignal){
	    outputSignal = signal;
	    update();
	}
    }
}
