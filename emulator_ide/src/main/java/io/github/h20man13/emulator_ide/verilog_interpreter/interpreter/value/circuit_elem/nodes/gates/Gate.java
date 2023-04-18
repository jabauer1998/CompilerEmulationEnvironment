package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates;


import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.Node;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.*;

public abstract class Gate extends Node {

	protected Gate(Web output) {
		super(output);
	}

	protected void updateOutput(){ // method used to update the output

		// If the current update is not equal to know schedule it for an update
		output.update();

	}

	abstract public void update(); // every gate class must have an update method however they are implimented differently

	abstract public String toString();
}
