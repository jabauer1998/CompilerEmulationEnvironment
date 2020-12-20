package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class XnorGate extends Gate {

    public XnorGate(CircuitElem input1, CircuitElem input2, CircuitElem... inputs){
	super(input1, input2, inputs);
    }
    
    protected void update(){
	if(outputSignal == false){
	    int numTrue = 0;
	    for(CircuitElem input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 0){
		outputSignal = true;
		super.updateOutputs();
	    }
	} else {
	    int numTrue = 0;
	    for(CircuitElem input : inputs){
		if(input.getSignal() == true){
		    numTrue++;
		}
	    }
	    if(numTrue % 2 == 1){
		outputSignal = false;
		super.updateOutputs();
	    }
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
