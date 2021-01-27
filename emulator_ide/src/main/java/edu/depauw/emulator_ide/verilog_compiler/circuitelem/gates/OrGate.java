package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.ArrayList;

public class OrGate extends Gate{

    private ArrayList<MiscElem> inputs;
    
    public OrGate(CircuitElem input1, CircuitElem input2, CircuitElem... optionalInputs){
	inputs.add(input1);
	inputs.add(input2);
	inputs.addAll(optionalInputs);
	super(inputs);
    }
    
    protected void update(){
	if(outputSignal == false){
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = true;
		    super.updateOutput();
		    break;
		}
	    }	
	} else {
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    return;
		}
	    }
	    outputSignal = false;
	    super.updateOutput();
	}
    }
}
