package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class NandGate extends Gate{

    public NandGate(Gate output, Gate... inputs){
	super(output, inputs);
    }
    
    public void update(){
	if(outputSignal == true){
		for(Gate input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = false;
		super.updateOutputs();
	} else {
		for(Gate input : inputs){
			if(input.getSignal() == false){
				outputSignal = true;
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
