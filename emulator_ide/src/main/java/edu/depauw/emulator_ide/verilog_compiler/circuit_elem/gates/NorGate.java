package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.Wire;

import java.util.ArrayList;

/**
 * The NorGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class NorGate extends Gate {
    private ArrayList<Wire> inputs;

    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the norgate
     * @param input2: the second input into the norgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public NorGate(Wire output, Wire input1, Wire input2, Wire... optional){
	this.inputs = new ArrayList<>();
	this.inputs.add(input1);
	if(!input.contains(input2)){
	    this.inputs.add(input2);
	}
	for(MiscElem input : optional){
	    if(!inputs.contains(input)){
		this.inputs.add(input);
	    }
	}
	for(Wire input : inputs){
	    if(!input.hasOutput(this)){
		input.addOutput(this);
	    }
	}
	super(output);
    }

    /**
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
     * @author Jacob Bauer
     */
    
    protected void update(){
	if(outputSignal == true){
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = false;
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
	    outputSignal = true;
	    super.updateOutput();
	}
    }
}
