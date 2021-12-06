package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;

import java.util.LinkedList;

/**
 * This is the register class it will be used to simulate regs in the verilog language
 * These are kind of like variables but they can be set and it will update the wires
 * accordingly
 * 
 * @author Jacob Bauer
 *
 */

public class Register extends Node {

    /**
     * The Register constructor creates a register object
     * 
     * @param  signal: initial signal you want to be sent out
     * @author         Jacob Bauer
     */
    public Register(boolean signal) {
        super(null);
        super.stateSignal = signal;
    }

    /**
     * The update method samples the inputs and updates the output of the gate.
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public void update(){

        if (super.output != null) { toUpdate.add(super.output); }

        if (toUpdate.peek() != null) { toUpdate.remove().update(); }

    }

    /**
     * The set signal method is used to change the input signal of a register
     * 
     * @param  signal: a boolean with true representing HI and false representing low
     * @author         Jacob Bauer
     */

    public void setSignal(boolean signal){

        if (signal != stateSignal) {
            stateSignal = signal;
            update();
        }

    }
}
