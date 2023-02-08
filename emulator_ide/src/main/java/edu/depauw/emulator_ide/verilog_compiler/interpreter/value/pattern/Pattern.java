package edu.depauw.emulator_ide.verilog_compiler.interpreter.value.pattern;

import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ByteVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ShortVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public abstract class Pattern implements Value{

    private final String pattern;

    /**
     * The Range is a data structure to verify case statement numbers
     * 
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    protected Pattern(String pattern) { this.pattern = pattern; }

    protected String getPattern(){
        return pattern;
    }

    public abstract boolean match(LongVal value);

    public abstract boolean match(IntVal value);

    public abstract boolean match(ShortVal value);

    public abstract boolean match(ByteVal value);

    public abstract boolean match(Vector value);

    public abstract boolean match(CircuitElem value);

    public double realValue(){
        return -1.0;
    }

    public long longValue(){
        return -1;
    }

    public int intValue(){
        return -1;
    }

    public short shortValue(){
        return -1;
    }

    public byte byteValue(){
        return -1;
    }

    public boolean boolValue(){
        return false;
    }
}
