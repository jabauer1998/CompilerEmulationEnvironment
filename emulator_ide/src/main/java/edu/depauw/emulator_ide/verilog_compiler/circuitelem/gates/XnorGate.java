package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;

import java.util.ArrayList;

public class XnorGate extends Gate {

    public XnorGate(MiscElem input1, MiscElem input2, MiscElem... optional){
	this.inputs = new ArrayList<>();
	inputs.add(input1);
	inputs.add(input2);
	for(MiscElem input: optional){
	    inputs.add(input);
	}
	super(inputs);
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
		super.updateOutput();
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
		super.updateOutput();
	    }
	}
    }
}
