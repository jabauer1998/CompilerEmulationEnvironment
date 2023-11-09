package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern;


import java.lang.Integer;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedLongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class BinaryPattern extends Pattern{

    public BinaryPattern(String pattern) { super(pattern); }

    public boolean match(LongVal value){

        String pattern = super.getPattern();
        long val = value.longValue();

        int patternLength = pattern.length();

        if(patternLength < Long.toBinaryString(val).length()){
            long shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 2);
            long shiftedValLong = val >> patternLength - i - 1;
            long maskedVal = shiftedValLong & 0b1; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedLongVal value){

        String pattern = super.getPattern();
        long val = value.longValue();

        int patternLength = pattern.length();

        if(patternLength < Long.toBinaryString(val).length()){
            long shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 2);
            long shiftedValLong = val >> patternLength - i - 1;
            long maskedVal = shiftedValLong & 0b1; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(IntVal value){

        String pattern = super.getPattern();
        int val = value.intValue();

        int patternLength = pattern.length();

        if(patternLength > 32){
            int num = value.intValue();
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            int shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 2);
            int shiftedValInt = val >> patternLength - i - 1;
            int maskedVal = shiftedValInt & 0b1; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedIntVal value){

        String pattern = super.getPattern();
        int val = value.intValue();

        int patternLength = pattern.length();

        if(patternLength > 32){
            int num = value.intValue();
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            int shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 2);
            int shiftedValInt = val >> patternLength - i - 1;
            int maskedVal = shiftedValInt & 0b1; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(ShortVal value){

        String pattern = super.getPattern();
        short val = value.shortValue();

        int patternLength = pattern.length();

        if(patternLength > 16){
            short num = value.shortValue();
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            short shiftedValue = (short)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 2);
            short shiftedValShort = (short)(val >> patternLength - i - 1);
            short maskedVal = (short)(shiftedValShort & 0b1);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedShortVal value){

        String pattern = super.getPattern();
        short val = value.shortValue();

        int patternLength = pattern.length();

        if(patternLength > 16){
            short num = value.shortValue();
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength  < Integer.toBinaryString(val).length()){
            short shiftedValue = (short)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 2);
            short shiftedValShort = (short)(val >> patternLength - i - 1);
            short maskedVal = (short)(shiftedValShort & 0b1);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(ByteVal value){

        String pattern = super.getPattern();
        byte val = value.byteValue();

        int patternLength = pattern.length();

        if(patternLength > 8){
            byte num = value.byteValue();
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            byte shiftedValue = (byte)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            byte shiftedValByte = (byte)(val >> patternLength - i - 1);
            byte maskedVal = (byte)(shiftedValByte & 0b1);

            if(maskedVal != patternPieceAsByte)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedByteVal value){

        String pattern = super.getPattern();
        byte val = value.byteValue();

        int patternLength = pattern.length();

        if(patternLength > 8){
            byte num = value.byteValue();
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            byte shiftedValue = (byte)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            byte shiftedValByte = (byte)(val >> patternLength - i - 1);
            byte maskedVal = (byte)(shiftedValByte & 0b1);

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

    public boolean match(VectorVal value){

        String pattern = super.getPattern();
        int patternLength = pattern.length();

        int bitIncr = (value.getIndex1() < value.getIndex2()) ? 1 : -1;
        int binIncr = bitIncr;

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

        for (int i = 0, vi = endOverflow; i < patternLength; i++, vi+=binIncr) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            boolean patternSignal = patternPieceAsByte != 0;

            if(patternSignal != value.getValue(vi).getStateSignal()){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isBoolValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isByteValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRealValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isStringValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return false; }
}
