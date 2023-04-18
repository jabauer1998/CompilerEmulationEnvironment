package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.*;

/**
 * The OrGate class is an instance of the the Gate class that is used to simulate and
 * OrGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler aswell as the code generation phase. The
 * interpreter is importanct because it will help validate if the code generator
 * actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */

public class OrGate extends Gate {

	private HashSet<Web> inputs;

	/**
	 * The and gate constructor creates a new and gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the orgate
	 * @param  input2:   the second input into the orgate
	 * @param  optional: these are optional inputs to morph the orgate into a multiple input
	 *                   and orgate
	 * @author           Jacob Bauer
	 */

	public OrGate(Web output, Web input1, Web input2, Web... optional) {
		super(output); // call the common gate constructor to deeal with configuring outputs

		this.inputs = new HashSet<Web>(); // Initialize the array for inputs
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

		if (super.stateSignal == false) {

			for (CircuitElem input : inputs) {

				if (input.getStateSignal() == true) {
					super.stateSignal = true;
					super.updateOutput();
					break;
				}

			}

		} else {

			for (CircuitElem input : inputs) { if (input.getStateSignal() == true) { return; } }

			super.stateSignal = false;
			super.updateOutput();
		}

	}

	public String toString(){
		return "OrGate with " + inputs.size() + " inputs";
	}

	@Override
	public boolean isBoolValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isShortValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isUnsignedShortValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isByteValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isIntValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isUnsignedIntValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isLongValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isUnsignedLongValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isRealValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isStringValue(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isVector(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isRegister(){ // TODO Auto-generated method stub
	return false; }

	@Override
	public boolean isWire(){ // TODO Auto-generated method stub
	return false; }
}
