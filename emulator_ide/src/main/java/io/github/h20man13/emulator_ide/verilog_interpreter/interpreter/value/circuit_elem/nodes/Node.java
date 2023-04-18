package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes;


import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.Web;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;

public abstract class Node extends CircuitElem {

    protected boolean stateSignal; // the current state of a signal true means high and false means low

    protected Web output; // Every node class is required to have atleast one output

    protected Node(Web output) {
        
        if(output instanceof WireVal){
            ((WireVal)output).assignInput(this);
        } else if (output != null){
            System.out.println("Unknown Web type " + output);
        }
        
        this.stateSignal = false;
    }

    public void attachOutput(Web output){
        this.output = output;
    }

    /**
     * Public helper and getter method to retrive the state signal from the given hardware
     * component
     * 
     * @return A Boolean representing a current state of the Node.
     */
    public boolean getStateSignal(){ return stateSignal; }

    public abstract String toString();

}
