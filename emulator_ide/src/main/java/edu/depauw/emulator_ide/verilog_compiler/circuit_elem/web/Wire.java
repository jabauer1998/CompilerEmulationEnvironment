package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

import java.util.HashSet;

/**
 * The wire class is used to attach all of the Circuit Elems together The wire class
 * should work just like a Wire Net in html
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

        for (CircuitElem output : outputs) { toUpdate.add(output); }

        if (toUpdate.peek() != null) { toUpdate.remove().update(); }

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

    public void setInput(CircuitElem input){
        this.input = input;
        this.input.update();
    }
}
