package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.gates;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;

/**
 * The AndGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */
public class AndGate extends Gate {

	private HashSet<Web> inputs;

	/**
	 * The and gate constructor creates a new and gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the andgate
	 * @param  input2:   the second input into the andgate
	 * @param  optional: these are optional inputs to morph the andgate into a multiple
	 *                   input and gate
	 * @author           Jacob Bauer
	 */

	public AndGate(Web output, Web input1, Web input2, Web... optional) {
		super(output); // Set the output of the gate

		this.inputs = new HashSet<Web>(); // initialize an array for inputs

		this.inputs.add(input1); // Add input 1 and 2 to the LinkedList of Gates
		this.inputs.add(input2);

		List<Web> asList = Arrays.asList(optional);
		this.inputs.addAll(asList);

		for (Web input : inputs) { // Loop through all of newly created outputs and add this as an input
			input.addOutput(this);
		}

		this.update(); // update the output
	}

	/**
	 * The update method samples the inputs and updates the output of the gate.
	 * 
	 * @param  None
	 * @author      Jacob Bauer
	 */

	public void update(){

		if (super.stateSignal == false) {

			for (CircuitElem input : inputs) { if (input.getStateSignal() == false) return; }

			super.stateSignal = true;
			super.updateOutput();
		} else {

			for (CircuitElem input : inputs) {

				if (input.getStateSignal() == false) {
					super.stateSignal = false;
					super.updateOutput();
					break;
				}

			}

		}

	}
}
