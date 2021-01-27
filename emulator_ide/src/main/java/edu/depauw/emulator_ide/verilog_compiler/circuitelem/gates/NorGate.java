package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.MiscElem;

import java.util.ArrayList;

public class NorGate extends Gate {
    private ArrayList<MiscElem> inputs;
    
    public NorGate(MiscElem input1, MiscElem input2, MiscElem... optional){
	this.inputs = new ArrayList<>();
	this.inputs.add(input1);
	if(!input.contains(input2)){
	    this.inputs.add(input2);
	}
	for(MiscElem input : optional){
	    if(!inputs.contains(input)){
		this.inputs.add(input);
	    }
	}
	super(this.inputs);
    }
    
    protected void update(){
	if(outputSignal == true){
	    for(MiscElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = false;
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
	    outputSignal = true;
	    super.updateOutput();
	}
    }
}
