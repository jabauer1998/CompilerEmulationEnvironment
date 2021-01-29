package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import java.util.ArrayList;

/**
 * The AndGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */
public class AndGate extends Gate {
    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the andgate
     * @param input2: the second input into the andgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    private ArrayList<Wire> inputs;
    
    public AndGate(Wire output, Wire input1, Wire input2, Wire... optional){
	super(output); //call the common gate constructor to deeal with configuring outputs
	this.inputs = new ArrayList<>(); //Initialize the array for inputs
	this.inputs.add(input1); //add all of the inputs to the array by removing duplicates
	if(!inputs.contains(input2)){ 
	    this.inputs.add(input2); 
	}
	for(Wire input: optional){
	    if(!inputs.contains(input)){
		this.inputs.add(input);
	    }
	}
	for(Wire input : inputs){
	    if(!input.hasOutput(this)){
		input.addOutput(this);
	    }
	}
	this.update(); //update the output
    }

    /**
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
     * @author Jacob Bauer
     */
    
    public void update(){
	if(outputSignal == false){
		for(Wire input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = true;
		updateOutput();
	} else {
		for(Wire input : inputs){
			if(input.getSignal() == false){
				outputSignal = false;
				updateOutput();
				break;
			}
		}
	}
    }
}
