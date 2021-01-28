package edu.depauw.emulator_ide.verilog_compiler.circuitelem.testutils;

import static org.junit.Assert.*;

class Primative {
    public ArrayList<ArrayList<Boolean>> primativeInputs;
    public ArrayList<ArrayList<Boolean>> primativeOutputs;
    private final int inputs;
    private final int outptus;
    
    public Primative(int inputs, int outputs){
	this.inputs = inputs;
	this.outputs = outputs;
	primativeInputs = new ArrayList<>();
	primativeOutptus = new ArrayList<>();
    }

    public void addRow(Inputs inputRow, Output outputRow){
	AssertTrue("Input length does not match primative input size " + inputs, inputRow.size() == inputs);
	AssertTrue("Output length does not match primative output size " + outputs, outputRow.size() == outputs);
	primativeInputs.add(inputRow.getList());
	primaticeOutputs.add(outputRow.getList());
    }

    public int getNumInputs(){
	return inputs;
    }

    public int getNumOutputs(){
	return outputs;
    }

    public int getNumRows(){
	return primativeOutputs.size();
    }

    public ArrayList<Boolean> getRowInputs(int rowNum){
	return primativeInputs.get(rowNum);
    }

    public ArrayList<Booelan> getRowOutputs(int rowNum){
	return primativeOutputs.get(rowNum);
    }
}
