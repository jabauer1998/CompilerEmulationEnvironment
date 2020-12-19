package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class NorGate extends Gate{

    public NorGate(Gate output, Gate... inputs){
	super(output, inputs);
    }
    
    public void update(){
	if(outputSignal == true){
	    for(Gate input : inputs){
		if(input.getSignal() == true){
		    outputSignal = false;
		    super.updateOutputs();
		    break;
		}
	    }	
	} else {
	    for(Gate input : inputs){
		if(input.getSignal() == true){
		    return;
		}
	    }
	    outputSignal = true;
	    super.updateOutputs();
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
