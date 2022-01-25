package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem;

import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
/**
 * The generic class for Circuit Objects
 * 
 * @author Jacob Bauer
 */
public abstract class CircuitElem implements Value{

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

    public abstract String toString();

    public double realValue(){
        return getStateSignal() ? 1.0 : 0.0;
    }

    public long longValue(){
        return getStateSignal() ? 1 : 0;
    }

    public int intValue(){
        return getStateSignal() ? 1 : 0;
    }

    public short shortValue(){
        return (short)(getStateSignal() ? 1 : 0);
    }

    public byte byteValue(){
        return (byte)(getStateSignal() ? 1 : 0);
    }

    public boolean boolValue(){
        return getStateSignal();
    }

}
