package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

import java.util.ArrayList;

public abstract class Gate extends CircuitElem{
    
    private Wire output;
    
    protected Gate(Wire output){
	this.outputSignal = false; //initial output signal is false (may change after update method is called)
	this.output = output; //set the output of the gate
	this.output.setInput(this); //connect the outputs input to the gate
    }

    protected void updateOutput(){ //method used to update the output
	if(output != null){
	    this.output.update();
	}
    }
    
    abstract public void update(); //every gate class must have an update method
}
