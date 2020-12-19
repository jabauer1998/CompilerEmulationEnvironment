package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class Register extends Gate{
    
    public Register(Gate output, Gate input){
	super(output, input);
    }

    public Register(Gate output, Boolean outputSignal){
	super(output);
	super.outputSignal = outputSignal;
	this.update();
    }

    public void update(){
	if(inputs.size() > 0){
	    outputSignal = inputs.get(0).getSignal();
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
