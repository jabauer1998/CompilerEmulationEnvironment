package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.MiscElem;

public abstract class Gate extends CircuitElem{
    
    /** Gates Single Output
     *
     */
    private MiscElem output;
    
    protected Gate(MiscElem input){
	input.output = this;
	outputSignal = false;
	update();
    }

    protected Gate(Collection inputs){
	for(MiscElem input : inputs){
		input.output = this;
	}
	outputSignal = false;
	update();
    }

    protected void updateOutput(){
	if(output != null){
	    output.update();
	}
    }
    
    abstract protected void update();
}
