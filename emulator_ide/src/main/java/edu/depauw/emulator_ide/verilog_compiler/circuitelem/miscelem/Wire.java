package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.ArrayList;

public class Wire extends MiscElem{
    public CircuitElem input;
    private ArrayList<CircuitElem> outputs;

    public Wire(CircuitElem input){
	outputs = new ArrayList<>();
	this.input = input;
	if (input instanceof Wire){
	    this.input.outputs.add(this);
	} else {
	    this.input.output = this;
	}
	outputSignal = this.input.getSignal();
    }
    
    protected void update(){
	if(this.input.getSignal() != outputSignal){
	    outputSignal = input.getSignal();
	    for(CircuitElem output : outputs){
		output.update();
	    }
	}
    }
    
    public boolean getSignal(){
	return super.getSignal();
    }
}
