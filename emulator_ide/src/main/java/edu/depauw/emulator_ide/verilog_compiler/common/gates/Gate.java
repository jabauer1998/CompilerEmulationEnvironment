package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

public abstract class Gate extends CircuitElem{
    
    protected LinkedList<CircuitElem> inputs;
    
    protected Gate(CircuitElem input1, CircuitElem input2, CircuitElem... optionalInputs){
	outputs = new LinkedList<>();
	inputs = new LinkedList<>();
	inputs.add(input1);
	inputs.add(input2);
	for(CircuitElem input : optionalInputs){
	    inputs.add(input);
	}
	connectInputs();
	outputSignal = false;
	update();
    }

    abstract protected void update();

    protected void updateOutputs(){
	for(CircuitElem output : outputs){
		output.update();
	}
    }

    private void connectInputs(){
	for(CircuitElem input : inputs){
	    input.outputs.add(this);
	}
    }

    public boolean getSignal(){
	return outputSignal;
    }
}
