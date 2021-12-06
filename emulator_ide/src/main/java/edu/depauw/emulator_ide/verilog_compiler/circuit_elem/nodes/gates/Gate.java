package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.gates;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Node;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.*;

public abstract class Gate extends Node {

	protected Gate(Web output) {
		super(output);
		this.stateSignal = false; // set the elements output signal to default output signal
	}

	protected void updateOutput(){ // method used to update the output

		// If the current update is not equal to know schedule it for an update
		if (super.output != null) toUpdate.add(this.output);

		// If their are any updates that are scheduled then execute those first
		if (toUpdate.peek() != null) toUpdate.remove().update();

	}

	abstract public void update(); // every gate class must have an update method however they are implimented differently
}
