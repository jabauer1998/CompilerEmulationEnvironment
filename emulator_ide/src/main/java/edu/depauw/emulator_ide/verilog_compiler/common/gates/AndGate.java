package edu.depauw.emulator_ide.verilog_compiler.common.gates;

/**
 * The AndGate class is an instance of the the Gate class that is used to simulate and AndGate. These classes will be usefull when trying to create a graph in the Interpreter phase of the compiler. The interpreter is importanct because it will help validate if the code generator actually works and we retrieve the expected results.
 * @author Jacob Bauer
 */
public class AndGate extends Gate {
    /**
     * The and gate constructor creates a new and gate. It ca take in a variable number of inputs with a minimum of two inputs
     * @param input1: the first input into the andgate
     * @param input2: the second input into the andgate
     * @param inputs: these are optional inputs to morph the andgate into a multiple input and gate 
     * @author Jacob Bauer
     */
    public AndGate(CircuitElem input1, CircuitElem input2, CircuitElem... inputs){
	super(input1, input2, inputs);
    }

    /**
     * The update method is actually the magic behind how the gate gets its output. These methods actually only check for the minimum change needed to change the output and then if that change is detected it will change the output
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
		super.updateOutputs();
	} else {
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				outputSignal = false;
				super.updateOutputs();
				break;
			}
		}
	}
    }
}
