package edu.depauw.emulator_ide.verilog_compiler.circuit_elem;
/**
 * The generic class for Circuit Objects
 * @author Jacob Bauer
 */
public abstract class CircuitElem{

    protected boolean outputSignal; //the output state of the component

    /**
     * method to update the output of the component
     * @param None
     * @author Jacob Bauer
     */
    
    abstract public void update(); //the update state of the component

    /**
     * method to get the current signal state of an input
     * @param None
     * @author Jacob Bauer
     */
    public boolean getSignal(){ //the signal method of the component
	return outputSignal;
    }
    
}
