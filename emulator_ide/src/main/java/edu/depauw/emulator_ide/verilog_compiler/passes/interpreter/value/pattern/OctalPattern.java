package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.pattern;


import java.lang.Integer;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ByteVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ShortVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class OctalPattern extends Pattern{

    public OctalPattern(String pattern) { super(pattern); }

    public boolean match(LongVal value){

        String pattern = super.getPattern();
        long val = value.longValue();

        int patternLength = pattern.length();

        if(patternLength * 3 < Long.toBinaryString(val).length()){
            long shiftedValue = val >> (patternLength * 3);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 8);
            long shiftedValLong = val >> 3*(patternLength - i) - 3;
            long maskedVal = shiftedValLong & 07; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(IntVal value){

        String pattern = super.getPattern();
        int val = value.intValue();

        int patternLength = pattern.length();

        if(patternLength > 8){
            int num = value.intValue();
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength * 3 < Integer.toBinaryString(val).length()){
            int shiftedValue = val >> (patternLength * 3);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 8);
            int shiftedValInt = val >> 3*(patternLength - i) - 3;
            int maskedVal = shiftedValInt & 07; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(ShortVal value){

        String pattern = super.getPattern();
        short val = value.shortValue();

        int patternLength = pattern.length();

        if(patternLength > 4){
            short num = value.shortValue();
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength * 3 < Integer.toBinaryString(val).length()){
            short shiftedValue = (short)(val >> (patternLength * 3));
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 8);
            short shiftedValShort = (short)(val >> 3*(patternLength - i) - 3);
            short maskedVal = (short)(shiftedValShort & 07);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(ByteVal value){

        String pattern = super.getPattern();
        byte val = value.byteValue();

        int patternLength = pattern.length();

        if(patternLength > 2){
            byte num = value.byteValue();
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength * 3 < Integer.toBinaryString(val).length()){
            byte shiftedValue = (byte)(val >> (patternLength * 3));
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 8);
            byte shiftedValByte = (byte)(val >> 3*(patternLength - i) - 3);
            byte maskedVal = (byte)(shiftedValByte & 07);

            if(maskedVal != patternPieceAsByte)
                return false;
        }

        return true;
    }

    public boolean match(CircuitElem elem){
        byte elemSignalAsByte = (byte)(elem.getStateSignal() ? 1 : 0);
        ByteVal retByte = new ByteVal(elemSignalAsByte);
        return match(retByte);
    }

    public boolean match(Vector value){

        String pattern = super.getPattern();
        int patternLength = pattern.length();

        int bitIncr = (value.getIndex1() < value.getIndex2()) ? 1 : -1;
        int octIncr = bitIncr * 4;
        int endOverflow = value.getIndex2() + ((value.getIndex1() > value.getIndex2())? patternLength - 1 : -patternLength + 1);

        if(patternLength * 4 < value.getSize()){
            for(int i = value.getIndex1(); i != endOverflow; i+=bitIncr){
                //If the vector length is grater then the pattern length * 4 then all of the signals need to be set to false
                //The reasoning is that if one of them is set true then it is impossible for the two elements to match because the one on the right is a bigger number
                if(value.getValue(i).getStateSignal()){
                    return false;
                }
            }
        }

        for (int i = 0, vi = endOverflow; i < patternLength; i++, vi+=octIncr) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 8);

            for(int j = 0; j < 3; j++){
                byte matchBit = (byte)(patternPieceAsByte >> (2 - j));
                boolean matchSignal = matchBit != 0;

                if(matchSignal != value.getValue(vi + j * bitIncr).getStateSignal()){
                    return false;
                }
            }   
            
        }

        return true;
    }
}
