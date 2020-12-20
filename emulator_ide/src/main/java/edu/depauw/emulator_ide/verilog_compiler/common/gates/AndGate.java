package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class AndGate extends Gate {

    public AndGate(CircuitElem input1, CircuitElem input2, CircuitElem... inputs){
	super(input1, input2, inputs);
    }
    
    public void update(){
	if(outputSignal == false){
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = true;
		super.updateOutputs();
	} else {
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				outputSignal = false;
				super.updateOutputs();
				break;
			}
		}
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
