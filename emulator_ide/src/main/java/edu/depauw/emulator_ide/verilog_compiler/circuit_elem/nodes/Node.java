package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.Web;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

public abstract class Node extends CircuitElem {

    protected volatile boolean stateSignal; // the current state of a signal true means high and false means low

    protected Web output; // Every node class is required to have atleast one output

    protected Node(Web output) {
        this.output = output;
        this.stateSignal = false;
    }

    /**
     * Public helper and getter method to retrive the state signal from the given hardware
     * component
     * 
     * @return A Boolean representing a current state of the Node.
     */
    public boolean getStateSignal(){ return stateSignal; }

}
