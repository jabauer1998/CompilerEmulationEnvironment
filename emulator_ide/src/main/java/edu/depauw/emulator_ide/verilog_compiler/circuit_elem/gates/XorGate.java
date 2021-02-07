package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import java.util.LinkedList;

/**
 * The XorGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class XorGate extends Gate{

    
    private LinkedList<Wire> inputs;

    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the xorgate
     * @param input2: the second input into the xorgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public XorGate(Wire output, Wire input1, Wire input2, Wire... optional){
	super(output);
	this.inputs = new LinkedList<>();
	inputs.add(input1);
	inputs.add(input2);
	for(Wire input: optional){
	    inputs.add(input);
	}
	for(Wire input : inputs){
	    if(!input.hasOutput(this)){
		input.addOutput(this);
	    }
	}
	this.update();
    }

    /**
     * The update method samples the inputs and updates the output of the gate.
     * @param None
     * @author Jacob Bauer
     */
    
    public void update(){
	if(outputSignal == false){
	    int numTrue = 0;
	    for(Wire input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 1){
		outputSignal = true;
		super.updateOutput();
	    }
	} else {
	    int numTrue = 0;
	    for(Wire input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 0){
		outputSignal = false;
		super.updateOutput();
	    }
	}
    }
}
