package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import java.util.ArrayList;

/**
 * The OrGate class is an instance of the the Gate class that is used to simulate and OrGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler aswell as the code generation phase. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class OrGate extends Gate{

     /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the orgate
     * @param input2: the second input into the orgate
     * @param optional: these are optional inputs to morph the orgate into a multiple input and orgate 
     * @author Jacob Bauer
     */

    private ArrayList<Wire> inputs;
    
    public OrGate(Wire output, Wire input1, Wire input2, Wire... optional){
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
    
    protected void update(){
	if(outputSignal == false){
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = true;
		    super.updateOutput();
		    break;
		}
	    }	
	} else {
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    return;
		}
	    }
	    outputSignal = false;
	    super.updateOutput();
	}
    }
}
