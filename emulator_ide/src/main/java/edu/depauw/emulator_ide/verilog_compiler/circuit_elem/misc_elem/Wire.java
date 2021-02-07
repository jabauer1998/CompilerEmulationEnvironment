package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

import java.util.LinkedList;

/**
 * The wire class is used to attach all of the Circuit Elems together
 * The wire class should work just like a Wire Net in html
 * @author Jacob Bauer
 */

public class Wire extends CircuitElem{
    public CircuitElem input; //Input into the wire
    private LinkedList<CircuitElem> outputs; //Outputs of the wire object

    /**
     * This is the constructor of the wire class
     * @param None
     * @author Jacob Bauer
     */
    public Wire(){
	outputs = new LinkedList<>();
	outputSignal = false;
    }
    /**
     * The update method is used to update the wires outputs.
     * @param None
     * @author Jacob Bauer
     */
    public void update(){
        if(this.input.getSignal() != outputSignal){
	    outputSignal = input.getSignal();
	    for(CircuitElem output : outputs){
		output.update();
	    }
	}
    }

    /**
     * The setIntput method is used to set the input for a wire
     * @param input: the gates output that is connected to the wire object
     * @author Jacob Bauer
     */
    public void setInput(CircuitElem input){
	if(input != null){
	    this.input = input;
	}
    }

    /**
     * The addOutput method is used to add an output to the wire
     * @param output: the circuit element to be connected to the wire
     * @author Jacob Bauer
     */
    public void addOutput(CircuitElem output){
	if(output != null){
	    this.outputs.add(output);
	}
    }

    /**
     * The hasOutput is used to check if the output allready exists inside the Linkedlist
     * In most cases if it allready exists then it doesnt need to be added twice. The exception is for Xnor and Xor gates.
     * @param output: the output you want to see if it exists
     * @author Jacob Bauer
     */
    public boolean hasOutput(CircuitElem output){
	return outputs.contains(output);
    }
}
