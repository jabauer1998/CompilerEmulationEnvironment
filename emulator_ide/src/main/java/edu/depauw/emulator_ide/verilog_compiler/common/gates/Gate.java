package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public class Gate implements CurcuitElem{
    
    protected LinkedList<CurcuitElem> inputs;
    protected LinkedList<CircuitElem> outputs;

    protected boolean outputSignal;
    
    protected Gate(CurcuitElem output, CurcuitElem input1, CircuitElem input2, CircuitElem optionalInputs){
	this.outputs = new LinkedList<>();
	this.inputs = new LinkedList<>();
	inputs.add(input1);
	inputs.add(input2);
	for(Gate input : optionalInputs){
	    this.inputs.add(input);
	}
	outputs.add(output);
	outputSignal = false;
	update();
    }

    protected void update(){
	this.update();
    }

    protected void updateOutputs(){
	for(CircuitElem output : outputs){
		output.update();
	}
    }

    protected boolean getSignal(){
	return outputSignal;
    }
}
