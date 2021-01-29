package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import java.util.ArrayList;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

/**
 * The XnorGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */

public class XnorGate extends Gate {

    private ArrayList<Wire> inputs;

    /**
     * The and gate constructor creates a new and gate. It can take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the xorgate
     * @param input2: the second input into the xorgate
     * @param optional: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    
    public XnorGate(Wire output, Wire input1, Wire input2, Wire... optional){
	super(output);
	this.inputs = new ArrayList<>();
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
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
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
	    if(numTrue % 2 == 0){
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
	    if(numTrue % 2 == 1){
		outputSignal = false;
		super.updateOutput();
	    }
	}
    }
}
