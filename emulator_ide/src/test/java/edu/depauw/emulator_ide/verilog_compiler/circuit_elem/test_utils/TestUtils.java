package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;

import static org.junit.Assert.*;
import java.lang.StringBuilder;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

public class TestUtils{
    
    private static void setRowInputs(ArrayList<Boolean> inputs, ArrayList<Register> realInputs){
	AssertTrue("Total Registers provided does not match primative input size " + inputs.size(), realInputs.size() == inputs.size());
	int size = inputs.size();
	for(int i = 0; i < size; i++){
	    realInputs.get(i).setSignal(inputs.get(i));
	}
    }

    private static void checkRowOutputs(ArrayList<Boolean> outputs, ArrayList<Register> realOutputs, int row){
	AssertTrue("Total Registers provided does not match primative input size " + inputs.size(), realInputs.size() == inputs.size());
	int size = outputs.size();
	for(int i = 0; i < size; i++){
	    StringBuilder sb = new StringBuilder("Error: unexpected output at row: ");
	    sb.append(row);
            sb.append("index: ");
	    sb.append(i);
	    sb.append(" [Got => ");
	    sb.append(realOutputs.get(i));
	    sb.append(" | Expected => ");
	    sb.append(outputs.get(i));
	    sb.append(']');
	    AssertTrue(sb.toString(), realOutputs.get(i) == outputs.get(i));
	}
    }
    
    public static void primitiveVerify(Primitive table, RealInputs inputs, RealOutputs outputs){
	AssertTrue("Total Registers provided does not match primative input size " + table.getNumInputs(), table.getNumInputs() == inputs.size());
	AssertTrue("Outputs provided does not match primative output size " + table.getNumOutputs(), table.getNumOutputs() == outputs.size());
	int rows = table.getNumRows();
	ArrayList<Register> realInputs = inputs.getList();
	ArrayList<Register> realOutputs = outputs.getList();
	for(int i = 0; i < rows; i++){
	    setRowInputs(table.getInputRow(i), realInputs); //Set the inputs to the next vales
	    checkRowOutputs(table.getOutputRow(i), realOutputs, i); //Check if the outputs match the expected values
	}
    }
}
