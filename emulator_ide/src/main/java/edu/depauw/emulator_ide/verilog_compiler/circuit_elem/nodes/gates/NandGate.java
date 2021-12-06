package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.gates;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.*;

/**
 * The XnorGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */

public class NandGate extends Gate {

	private HashSet<Web> inputs;

	/**
	 * The and gate constructor creates a new nand gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the andgate
	 * @param  input2:   the second input into the andgate
	 * @param  optional: these are optional inputs to morph the andgate into a multiple
	 *                   input and gate
	 * @author           Jacob Bauer
	 */

	public NandGate(Web output, Web input1, Web input2, Web... optional) {
		super(output); // call the common gate constructor to deeal with configuring outputs

		this.inputs = new HashSet<>(); // Initialize the array for inputs
		this.inputs.add(input1); // add all of the inputs to the array by removing duplicates
		this.inputs.add(input2);

		List<Web> asList = Arrays.asList(optional);
		this.inputs.addAll(asList);

		for (Web input : inputs) { input.addOutput(this); }

		this.update();
	}

	/**
	 * The update method samples the inputs and updates the output of the gate.
	 * 
	 * @param  None
	 * @author      Jacob Bauer
	 */

	public void update(){

		if (super.stateSignal == true) {

			for (Web input : inputs) { if (input.getStateSignal() == false) return; }

			super.stateSignal = false;
			super.updateOutput();
		} else {

			for (Web input : inputs) {

				if (input.getStateSignal() == false) {
					super.stateSignal = true;
					super.updateOutput();
					break;
				}

			}

		}

	}
}
