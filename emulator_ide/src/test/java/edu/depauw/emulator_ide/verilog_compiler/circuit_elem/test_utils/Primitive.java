package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;


import static org.junit.Assert.*;

import java.util.ArrayList;

public class Primitive {

    public ArrayList<ArrayList<Boolean>> primitiveInputs;
    public ArrayList<ArrayList<Boolean>> primitiveOutputs;
    private final int                    inputs;
    private final int                    outputs;

    public Primitive(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        primitiveInputs = new ArrayList<>();
        primitiveOutputs = new ArrayList<>();
    }

    public void addRow(Tuple<Boolean> inputRow, Tuple<Boolean> outputRow){
        assertTrue("Input length does not match primative input size " + inputs, inputRow.size() == inputs);
        assertTrue("Output length does not match primative output size " + outputs, outputRow.size() == outputs);
        primitiveInputs.add(inputRow.getList());
        primitiveOutputs.add(outputRow.getList());
    }

    public int getNumInputs(){ return inputs; }

    public int getNumOutputs(){ return outputs; }

    public int getNumRows(){ return primitiveOutputs.size(); }

    public ArrayList<Boolean> getRowInputs(int rowNum){ return primitiveInputs.get(rowNum); }

    public ArrayList<Boolean> getRowOutputs(int rowNum){ return primitiveOutputs.get(rowNum); }
}
