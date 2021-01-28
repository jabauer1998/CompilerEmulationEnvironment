package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;

import static org.junit.Assert.*;

class Primitive {
    public ArrayList<ArrayList<Boolean>> primitiveInputs;
    public ArrayList<ArrayList<Boolean>> primitiveOutputs;
    private final int inputs;
    private final int outptus;
    
    public Primitive(int inputs, int outputs){
	this.inputs = inputs;
	this.outputs = outputs;
	primitiveInputs = new ArrayList<>();
	primitiveOutptus = new ArrayList<>();
    }

    public void addRow(Inputs inputRow, Output outputRow){
	AssertTrue("Input length does not match primative input size " + inputs, inputRow.size() == inputs);
	AssertTrue("Output length does not match primative output size " + outputs, outputRow.size() == outputs);
	primitiveInputs.add(inputRow.getList());
	primiticeOutputs.add(outputRow.getList());
    }

    public int getNumInputs(){
	return inputs;
    }

    public int getNumOutputs(){
	return outputs;
    }

    public int getNumRows(){
	return primitiveOutputs.size();
    }

    public ArrayList<Boolean> getRowInputs(int rowNum){
	return primitiveInputs.get(rowNum);
    }

    public ArrayList<Booelan> getRowOutputs(int rowNum){
	return primitiveOutputs.get(rowNum);
    }
}
