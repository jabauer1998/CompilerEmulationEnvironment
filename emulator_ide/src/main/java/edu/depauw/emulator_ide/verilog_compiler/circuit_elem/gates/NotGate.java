package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import java.util.ArrayList;
/**
 * The NorGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class NotGate extends Gate{
    private Wire input;

    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the norgate
     * @param input2: the second input into the norgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public NotGate(Wire output, Wire input){
	super(output);
	this.input = input;
	this.input.addOutput(this);
	this.update(); //update the output
    }

    /**
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
     * @author Jacob Bauer
     */
    
    public void update(){
	if(outputSignal == input.getSignal()){
	    outputSignal = !outputSignal;
	    super.updateOutput();
	}
    }
}
