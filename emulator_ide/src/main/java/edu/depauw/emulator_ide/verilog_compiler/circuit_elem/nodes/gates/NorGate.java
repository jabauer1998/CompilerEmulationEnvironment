package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.gates;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
/**
 * The NorGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */

public class NorGate extends Gate {

	private HashSet<Web> inputs;

	/**
	 * The and gate constructor creates a new and gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the norgate
	 * @param  input2:   the second input into the norgate
	 * @param  optional: these are optional inputs to morph the andgate into a multiple
	 *                   input and gate
	 * @author           Jacob Bauer
	 */

	public NorGate(Web output, Web input1, Web input2, Web... optional) {
		super(output);

		this.inputs = new HashSet<>();
		this.inputs.add(input1);
		this.inputs.add(input2);

		List<Web> asList = Arrays.asList(optional);
		this.inputs.addAll(asList);

		for (Web input : inputs) { input.addOutput(this); }

		this.update(); // update the output
	}

	/**
	 * The update method samples the inputs and updates the output of the gate.
	 * 
	 * @param  None
	 * @author      Jacob Bauer
	 */

	public void update(){

		if (super.stateSignal == true) {

			for (CircuitElem input : inputs) {

				if (input.getStateSignal() == true) {
					super.stateSignal = false;
					super.updateOutput();
					break;
				}

			}

		} else {

			// Circle through each if the inputs and check if one of them is false
			for (CircuitElem input : inputs) { if (input.getStateSignal() == true) return; }

			super.stateSignal = true;
			super.updateOutput();
		}

	}
}
