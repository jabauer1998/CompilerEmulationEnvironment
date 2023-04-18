package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates;


import java.util.LinkedList;
import java.util.List;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.*;
import java.util.Arrays;

/**
 * The XorGate class is an instance of the the Gate class that is used to simulate and
 * AndGate. These classes will be usefull when trying to create a graph in the
 * Interpreter phase of the compiler. The interpreter is importanct because it will help
 * validate if the code generator actually works and we retrieve the expected results.
 * 
 * @author Jacob Bauer
 */

public class XorGate extends Gate {

	private LinkedList<Web> inputs;

	/**
	 * The and gate constructor creates a new and gate. It can take in a variable number of
	 * inputs with a minimum of two inputs
	 * 
	 * @param  input1:   the first input into the xorgate
	 * @param  input2:   the second input into the xorgate
	 * @param  optional: these are optional inputs to morph the andgate into a multiple
	 *                   input and gate
	 * @author           Jacob Bauer
	 */

	public XorGate(Web output, Web input1, Web input2, Web... optional) {
		super(output);

		this.inputs = new LinkedList<>();
		this.inputs.add(input1);
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
			int numTrue = 0;

			for (Web input : inputs) { if (input.getStateSignal() == true) { numTrue++; } }

			if (numTrue%2 == 1) {
				stateSignal = true;
				super.updateOutput();
			}

		} else {
			int numTrue = 0;

			for (Web input : inputs) { if (input.getStateSignal() == true) { numTrue++; } }

			if (numTrue%2 == 0) {
				super.stateSignal = false;
				super.updateOutput();
			}

		}

	}

	public String toString(){
		return "XorGate with " + inputs.size() + " inputs";
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
