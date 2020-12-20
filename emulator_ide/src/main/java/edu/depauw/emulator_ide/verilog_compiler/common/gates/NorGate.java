package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class NorGate extends Gate {

    public NorGate(CircuitElem input1, CircuitElem input2, CircuitElem... inputs){
	super(input1, input2, inputs);
    }
    
    protected void update(){
	if(outputSignal == true){
	    for(CircuitElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = false;
		    super.updateOutputs();
		    break;
		}
	    }	
	} else {
	    for(CircuitElem input : inputs){
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
