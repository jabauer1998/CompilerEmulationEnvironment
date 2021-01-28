package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.Wire;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.CircuitElem;

import java.util.ArrayList;

public abstract class Gate extends CircuitElem{
    
    private Wire output;
    
    protected Gate(Wire output){
	outputSignal = false; //initial output signal is false (may change after update method is called)
	this.output = output; //set the output of the gate
	this.output.setInput(this); //connect the outputs input to the gate
	update(); //update the output
    }

    protected void updateOutput(){ //method used to update the output
	if(output != null){
	    output.update();
	}
    }
    
    abstract protected void update(); //every gate class must have an update method
}
