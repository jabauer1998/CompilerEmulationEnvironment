package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class NandGate extends Gate {
    
    private ArrayList<MiscElem> inputs;
    
    public NandGate(CircuitElem input1, CircuitElem input2, CircuitElem... optional){
	super(inputs);
    }
    
    protected void update(){
	if(outputSignal == true){
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = false;
		super.updateOutputs();
	} else {
		for(CircuitElem input : inputs){
			if(input.getSignal() == false){
				outputSignal = true;
				super.updateOutputs();
				break;
			}
		}
	}
    }
}
