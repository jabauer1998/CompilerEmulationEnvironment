package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.*;

import java.util.LinkedList;


/**
 * The AndGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */
public class AndGate extends Gate{
    private LinkedList<CircuitElem> inputs;
    
    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the andgate
     * @param input2: the second input into the andgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public AndGate(CircuitElem output, CircuitElem input1, CircuitElem input2, CircuitElem... optional){
	super(output); //call the common gate constructor to deeal with configuring outputs
	this.inputs = new LinkedList<>(); //Initialize the array for inputs
	this.inputs.add(input1); //add all of the inputs to the array by removing duplicates
	if(!inputs.contains(input2)){ 
	    this.inputs.add(input2); 
	}
	for(CircuitElem input: optional){
	    if(!inputs.contains(input)){
		this.inputs.add(input);
	    }
	}
	for(CircuitElem input : inputs){
	    if(!input.hasOutput(this)){
		if(input instanceof Wire){
		    ((Wire)input).addOutput(this);
		} else {
		    ((Register)input).addOutput(this);
		}
	    }
	}
	this.update(); //update the output
    }

    /**
     * The update method samples the inputs and updates the output of the gate.
     * @param None
     * @author Jacob Bauer
     */
    
    public void update(){
	if(outputSignal == false){
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = true;
		updateOutput();
	} else {
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				outputSignal = false;
				updateOutput();
				break;
			}
		}
	}
    }
}
