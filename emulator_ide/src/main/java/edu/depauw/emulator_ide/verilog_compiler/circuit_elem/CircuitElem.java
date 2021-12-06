package edu.depauw.emulator_ide.verilog_compiler.circuit_elem;


import java.util.Queue;
import java.util.LinkedList;
/**
 * The generic class for Circuit Objects
 * 
 * @author Jacob Bauer
 */
public abstract class CircuitElem {

    protected static Queue<CircuitElem> toUpdate = new LinkedList<>(); // queue to store next field to update

    /**
     * method to update the output of the component
     * 
     * @param  None
     * @author      Jacob Bauer
     */

    public abstract void update(); // the update state of the component

    /**
     * All circuit elements must have some method of getting the state of an object that is
     * attached to it
     * 
     * @param  None
     * @author      Jacob Bauer
     */

    public abstract boolean getStateSignal();

}
