package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

import java.util.ArrayList;

public class Wire extends CircuitElem{
    public CircuitElem input;
    private ArrayList<CircuitElem> outputs;

    public Wire(){
	outputs = new ArrayList<>();
	outputSignal = false;
    }
    
    public void update(){
	if(this.input.getSignal() != outputSignal){
	    outputSignal = input.getSignal();
	    for(CircuitElem output : outputs){
		output.update();
	    }
	}
    }

    public void setInput(CircuitElem input){
	this.input = input;
    }

    public void addOutput(CircuitElem output){
	this.outputs.add(output);
    }

    public boolean hasOutput(CircuitElem output){
	return outputs.contains(output);
    }
}
