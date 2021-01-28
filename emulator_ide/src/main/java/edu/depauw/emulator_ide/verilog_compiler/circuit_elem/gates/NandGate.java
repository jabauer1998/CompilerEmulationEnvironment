package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import java.util.ArrayList;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

/**
 * The XnorGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class NandGate extends Gate {
    
    private ArrayList<Wire> inputs;

     /**
     * The and gate constructor creates a new nand gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the andgate
     * @param input2: the second input into the andgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public NandGate(Wire output, Wire input1, Wire input2, Wire... optional){
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
	super(output); //call the common gate constructor to deeal with configuring outputs
    }

     /**
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
     * @author Jacob Bauer
     */
    
    protected void update(){
	if(outputSignal == true){
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = false;
		super.updateOutput();
	} else {
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				outputSignal = true;
				super.updateOutput();
				break;
			}
		}
	}
    }
}
