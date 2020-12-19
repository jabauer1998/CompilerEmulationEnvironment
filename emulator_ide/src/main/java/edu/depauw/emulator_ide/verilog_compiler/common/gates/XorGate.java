package edu.depauw.emulator_ide.verilog_compiler.common.gates;


public class XorGate extends Gate{

    public XorGate(Gate output, Gate... inputs){
	super(output, inputs);
    }
    
    public void update(){
	if(outputSignal == false){
	    int numTrue = 0;
	    for(Gate input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 1){
		outputSignal = true;
		super.updateOutputs();
	    }
	} else {
	    int numTrue = 0;
	    for(Gate input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 0){
		outputSignal = false;
		super.updateOutputs();
	    }
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
