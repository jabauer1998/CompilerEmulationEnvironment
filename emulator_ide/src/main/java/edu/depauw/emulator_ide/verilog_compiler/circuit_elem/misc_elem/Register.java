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

    /**
     * The Register constructor creates a register object
     * @param signal: initial signal you want to be sent out
     * @author Jacob Bauer
     */
    public Register(boolean signal){
	this.outputSignal = signal;
	this.output = null;
    }

    /**
     * The setOutput function is used to connect register to a wire
     * @param output: the wire you want the register to "drive" 
     * @author Jacob Bauer
     */
    public void setOutput(Wire output){
	this.output = output;
	if(this.output != null){
	    this.output.setInput(this);
	}
	update();
    };

    /**
     * The update method samples the inputs and updates the output of the gate.
     * @param None
     * @author Jacob Bauer
     */
    
    public void update(){
	if(output != null){
	    output.update();
	}
    }

    /**
     * The set signal method is used to change the input signal of a register
     * @param signal: a boolean with true representing HI and false representing low
     * @author Jacob Bauer
     */
    
    public void setSignal(boolean signal){
	if(signal != outputSignal){
	    outputSignal = signal;
	    update();
	}
    }
}
