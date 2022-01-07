package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Node;
import java.util.HashSet;

/**
 * The wire class is used to attach all of the CircuitElems together. The wire class
 * should work just like a Wire Net in Verilog
 * 
 * @author Jacob Bauer
 */

public class Wire extends Web {

    private HashSet<CircuitElem> outputs; // Outputs of the wire object
    private CircuitElem          input;

    /**
     * This is the constructor of the wire class
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public Wire(CircuitElem input) {
        this.outputs = new HashSet<>();
        this.input = input;
    }

    public Wire() { this(null); }

    /**
     * The update method is used to update the wires outputs.
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public void update(){

        for (CircuitElem output : outputs) { output.update(); }

    }

    /**
     * The hasOutput is used to check if the output allready exists inside the Linkedlist
     * 
     * @param  output: the output you want to see if it exists
     * @author         Jacob Bauer
     */
    public boolean hasOutput(CircuitElem output){ return outputs.contains(output); }

    /**
     * Add output method adds non duplicate circuit elem to the hashset
     */
    public void addOutput(CircuitElem output){ outputs.add(output); }

    public boolean getStateSignal(){
        if (input == null) return false;

        return input.getStateSignal();
    }

    /**
     * The assign input method is used to attach an input to the Wire
     * @param input
     */

    public void assignInput(CircuitElem input){
        this.input = input;
        
        if(this.input instanceof Web){
            ((Web)this.input).addOutput(this);
        } else {
            ((Node)this.input).attachOutput(this);
        }
    }
}
