package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern;

import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.BoolVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedLongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;

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

    public boolean match(Value value) throws Exception{
        if(value.isBoolValue()) return match((BoolVal)value);
        else if(value.isUnsignedByteValue()) return match((UnsignedByteVal)value);
        else if(value.isByteValue()) return match((ByteVal)value);
        else if(value.isUnsignedIntValue()) return match((UnsignedIntVal)value);
        else if(value.isIntValue()) return match((IntVal)value);
        else if(value.isUnsignedLongValue()) return match((UnsignedLongVal)value);
        else if(value.isLongValue()) return match((LongVal)value);
        else if(value.isUnsignedShortValue()) return match((UnsignedShortVal)value);
        else if(value.isShortValue()) return match((ShortVal)value);
        else if(value.isVector()) return match((VectorVal)value);
        else if(value instanceof CircuitElem) return match((CircuitElem)value);
        else {
            Utils.errorAndExit("Error invalid Type for pattern match " + value.getClass().getName());
            return false;
        }
    }

    public abstract boolean match(LongVal value);
    public abstract boolean match(UnsignedLongVal value);
    public abstract boolean match(IntVal value);
    public abstract boolean match(UnsignedIntVal value);
    public abstract boolean match(ShortVal value);
    public abstract boolean match(UnsignedShortVal value);
    public abstract boolean match(ByteVal value);
    public abstract boolean match(UnsignedByteVal value);
    public abstract boolean match(VectorVal value);
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
