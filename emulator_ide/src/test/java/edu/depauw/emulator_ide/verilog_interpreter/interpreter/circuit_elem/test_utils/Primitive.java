package edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils;


import static org.junit.Assert.*;
import java.util.LinkedList;

public class Primitive {

    public LinkedList<LinkedList<Boolean>> primitiveInputs;
    public LinkedList<LinkedList<Boolean>> primitiveOutputs;
    private final int                    inputs;
    private final int                    outputs;

    public Primitive(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        primitiveInputs = new LinkedList<>();
        primitiveOutputs = new LinkedList<>();
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

    public LinkedList<Boolean> getRowInputs(int rowNum){ return primitiveInputs.get(rowNum); }

    public LinkedList<Boolean> getRowOutputs(int rowNum){ return primitiveOutputs.get(rowNum); }
}
