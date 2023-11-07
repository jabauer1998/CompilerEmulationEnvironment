package io.github.H20man13.emulator_ide.verilog_interpreter;

import java.io.File;
import javax.management.RuntimeErrorException;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.BoolVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.RealVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.StrVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedByteVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedIntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedLongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.UnsignedShortVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.array.ArrayVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry.HalfAdder;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry.RippleCarryAdder;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry.RippleCarrySubtractor;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.AndGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NotGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.OrGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XnorGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XorGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern.Pattern;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Int;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;

public class Utils {
	/**
	 * The checksize method is used to check if the size on the right hand side of the ' is
	 * equal to the amount of characters on the left hand size of the equals. Ex:
	 * "8'b10101010" (Returns True) 2'b10101010(Returns False)
	 * 
	 * @param val the string representation of the number passed in
	 */
	public static boolean checkSize(String val){ return getSize(val) == (val.substring(val.indexOf('\'') + 2).length()); }

	/*
	 * The get size method returns the Size of the string located on the right of the '
	 * 20'b00000000... the size of this is 20
	 */
	private static int getSize(String val){
		int upTo = val.indexOf('\'');
		return Integer.parseInt(val.substring(0, upTo));
	}

	/**
	 * The getBase method returns the base of the number given the base letter b is binary d
	 * is decimal 0 is octal etc...
	 * 
	 * @param val string representation of the number
	 */

	public static int getBase(String val){
		int id = val.indexOf('\'') + 1;

		switch(Character.toLowerCase(val.charAt(id))){
			case 'd':
				return 10;
			case 'h':
				return 16;
			case 'o':
				return 8;
			case 'b':
				return 2;
		}

		return -1;
	}

	/**
	 * Returns the numerical version of the number
	 * 
	 * @param val the string representation of the number
	 */
	public static long getBinary(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 2);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Hexidecimal version of the number
	 * 
	 * @param val Number representation of the number
	 */

	public static long getHexidecimal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 16);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Octal version of the number
	 * 
	 * @param val String representation of the number
	 */
	public static long getOctal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 8);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Decimal version of the number
	 * 
	 * @param val String representing the number
	 */
	public static long getDecimal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2));
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Below are some errorHandling routines
	 * @throws Exception
	 */
	public static void errorAndExit(String errorParam) throws Exception{ 
		throw new Exception(errorParam);
	}

	public static void errorAndExit(String errorParam, io.github.H20man13.emulator_ide.common.Position position) throws Exception{
		Utils.errorAndExit(errorParam + position.toString());
	}

	public static String GetRuntimeDir(){
		return new File("").getAbsolutePath();
	}

	public static IntVal errorOccured(){
		return new IntVal(-1);
	}

	public static IntVal success(){
		return new IntVal(0);
	}

	public static StrVal fetchFunctionName(ModuleItem functionDeclaration) throws Exception{
		if(functionDeclaration instanceof Reg.Scalar.Ident){
			return new StrVal(((Reg.Scalar.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Reg.Vector.Ident) {
			return new StrVal(((Reg.Vector.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Int.Ident){
			return new StrVal(((Int.Ident)functionDeclaration).declarationIdentifier);
		} else {
			Utils.errorAndExit("Unknown Function Type found " + functionDeclaration.toString());
			return null;
		}
	}

    public static Value add(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new RealVal(left.realValue() + right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new RealVal(left.realValue() + right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new RealVal(left.realValue() + right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRealValue() && right.isVector()) return new RealVal(left.realValue() + right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() + right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() + right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() + right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() + right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() + right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() + right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() + right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() + right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() + right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() + right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() + right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() + right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() + right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() + right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isVector() && right.isVector()) return addVector((VectorVal)left, (VectorVal)right);
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	private static Value addVector(VectorVal left, VectorVal right) throws Exception{
		int leftSize = left.getSize();
		int rightSize = right.getSize();

		int largestSize = (leftSize >= rightSize)? leftSize : rightSize;

		VectorVal resultVec = new VectorVal(0, largestSize - 1);

		int startLeft = left.getStart();
		int startRight = right.getStart();
		int startResult = resultVec.getStart();

		int endLeft = left.getEnd();
		int endRight = right.getEnd();
		int endResult = resultVec.getEnd();

		boolean carryIn = false;
		for(int i = 0; i < largestSize; i++){
			boolean leftBit = false;
			if(startLeft <= endLeft)
				leftBit = left.getValue(startLeft).getStateSignal();
			
			boolean rightBit = false;
			if(startRight <= endRight)
				rightBit = right.getValue(startRight).getStateSignal();

			if(leftBit == true && rightBit == true && carryIn == true){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = true;
			} else if(leftBit == true && rightBit == true && carryIn == false){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == true && rightBit == false && carryIn == true){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == false && rightBit == true && carryIn == true){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == false && rightBit == false && carryIn == true){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == false && rightBit == true && carryIn == false){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == true && rightBit == false && carryIn == false){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == false && rightBit == false && carryIn == false){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = false;
			} else {
				errorAndExit("Unable to add vector signals in an addition operation with left=" + leftBit + " right=" + rightBit + " carryIn=" + carryIn);
			}

			startLeft++;
			startRight++;
			startResult++;
		}

		return resultVec;
	}

	public static Value createAdder(Value left, Value right) throws Exception{
		return createRippleCarryAdder(left, right);
	}

	public static Value createRippleCarryAdder(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Input1 = (WireVal)left;
			WireVal Input2 = (WireVal)right;

			WireVal output = new WireVal();
			WireVal carry = new WireVal();

			new HalfAdder(output, carry, Input1, Input2);

			return output;
		} else if (left.isVector() && right.isVector()){
			WireVal CarryOut = new WireVal();
			VectorVal Output = new VectorVal(0, ((VectorVal)right).getSize() - 1); 
			new RippleCarryAdder(CarryOut, Output, ((VectorVal)right), (VectorVal)left);

			return Output;
		} else {
			Utils.errorAndExit("Error Invalid Types for creating an Adder (Left -> " + left.getClass().getName() + " | Right -> " + right.getClass().getName() + ")");
		}

		return Utils.errorOccured();
	}

    public static Value minus(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new RealVal(left.realValue() - right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new RealVal(left.realValue() - right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new RealVal(left.realValue() - right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRealValue() && right.isVector()) return new RealVal(left.realValue() - right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() - right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() - right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() - right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() - right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() - right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() - right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() - right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() - right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() - right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() - right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() - right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() - right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() - right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() - right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isVector() && right.isVector()) return subVector((VectorVal)left, (VectorVal)right);
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	private static Value subVector(VectorVal left, VectorVal right) throws Exception{
		right = invertVector(right);
		return addVector(left, right);
	}

	private static VectorVal invertVector(VectorVal left){
		VectorVal vec = new VectorVal(0, left.getSize() - 1);
		int vecStart = vec.getStart();
		int leftStart = left.getStart();

		int vecEnd = vec.getEnd();
		int leftEnd = left.getEnd();

		for(int i = vecStart, j = leftStart; i <= vecEnd && j <= leftEnd; i++, j++){
			boolean signal = left.getValue(j).getStateSignal();
			vec.setValue(i, new RegVal(!signal));
		}

		return vec;
	}

	public static Value createSubtractor(Value left, Value right) throws Exception{
		if(left.isVector() && right.isVector()){
			//If they are both vectors we will create a RippleAdder
			WireVal CarryOut = new WireVal();
			VectorVal Output = new VectorVal(0, ((VectorVal)right).getSize() - 1); 
			new RippleCarrySubtractor(CarryOut, Output, ((VectorVal)right), (VectorVal)left);
			
			return Output;
		} else if(left.isWire() && right.isWire()) {
			WireVal leftWire = (WireVal)left;
			WireVal rightWire = (WireVal)right;
			WireVal realRight = new WireVal();

			//To Implement Subtraction we will just put one of the Wires through a Not Gate
			new NotGate(realRight, rightWire);
			rightWire = realRight;
			WireVal Output = new WireVal();
			WireVal Carry = new WireVal(); //Summy Variable Carry Out will not be returned as part of the Asssignment

			new HalfAdder(Output, Carry, leftWire, rightWire);

			return Output;
		} else {
			Utils.errorAndExit("Can't create a subtractor with the types " + right.getClass().getName() + " and " + left.getClass().getName());
			return Utils.errorOccured();
		}
	}

    public static Value times(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new RealVal(left.realValue() * right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new RealVal(left.realValue() * right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new RealVal(left.realValue() * right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRealValue() && right.isVector()) return new RealVal(left.realValue() * right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() * right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() * right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() * right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() * right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() * right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() * right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() * right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() * right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() * right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() * right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() * right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() * right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value div(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new RealVal(left.realValue() / right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new RealVal(left.realValue() / right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new RealVal(left.realValue() / right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRealValue() && right.isVector()) return new RealVal(left.realValue() / right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new RealVal(left.shortValue() / right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new RealVal(left.shortValue() / right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new RealVal(left.shortValue() / right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new RealVal(left.shortValue() / right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new RealVal(left.intValue() / right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new RealVal(left.intValue() / right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new RealVal(left.longValue() / right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new RealVal(left.longValue() / right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new RealVal(left.longValue() / right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLongValue() && right.isVector()) return new RealVal(left.longValue() / right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isRegister() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isVector() && right.isIntValue()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isVector() && right.isLongValue()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isVector() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isVector() && right.isVector()) return new RealVal(left.longValue() / right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

    public static Value mod(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() % right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() % right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() % right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() % right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() % right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() % right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() % right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() % right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value lazyEquality(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() == right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() == right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() == right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() == right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() == right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() == right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value strictEquality(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() == right.realValue());
		
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() == right.byteValue());

        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());
		else {
			errorAndExit("Inavlid === operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value lazyInequality(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() != right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() != right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() != right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() != right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() != right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() != right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value strictInequality(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() != right.realValue());
		
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() != right.byteValue());

        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());
		else {
			errorAndExit("Inavlid === operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value logicalAnd(Value left, Value right){
        return new BoolVal(left.boolValue() && right.boolValue());
    }

    public static Value logicalOr(Value left, Value right){
        return new BoolVal(left.boolValue() || right.boolValue());
    }

    public static Value lessThanOrEqualTo(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() <= right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() <= right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() <= right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() <= right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() <= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() <= right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() <= right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() <= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() <= right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() <= right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() <= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() <= right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() <= right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() <= right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() <= right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() <= right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() <= right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() <= right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() <= right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() <= right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() <= right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() <= right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() <= right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() <= right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() <= right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() <= right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() <= right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() <= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() <= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() <= right.intValue());
			else return new BoolVal(left.longValue() <= right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() <= right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() <= right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() <= right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() <= right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value lessThan(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() < right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() < right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() < right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() < right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() < right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() < right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() < right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() < right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() < right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() < right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() < right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() < right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() < right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() < right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() < right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() < right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() < right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() < right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() < right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() < right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() < right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() < right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() < right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() < right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() < right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() < right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() < right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() < right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() < right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() < right.intValue());
			else return new BoolVal(left.longValue() < right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() < right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() < right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() < right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() < right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value greaterThanOrEqualTo(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() >= right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() >= right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() >= right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() >= right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() >= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() >= right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() >= right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() >= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() >= right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() >= right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() >= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() >= right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() >= right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() >= right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() >= right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() >= right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() >= right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() >= right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() >= right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() >= right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() >= right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() >= right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() >= right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() >= right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() >= right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() >= right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() >= right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() >= right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() >= right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() >= right.intValue());
			else return new BoolVal(left.longValue() >= right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() >= right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() >= right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() >= right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() >= right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value greaterThan(Value left, Value right) throws Exception{
        if(left.isRealValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isBoolValue()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isRealValue() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() > right.realValue()); 
        else if(left.isRealValue() && right.isByteValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isUnsignedIntValue()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isRealValue() && right.isIntValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isUnsignedShortValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isShortValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isUnsignedLongValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isLongValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isRegister()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRealValue() && right.isVector()) return new BoolVal(left.realValue() > right.realValue());
		
		else if(left.isBoolValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isBoolValue() && right.isBoolValue()) return new BoolVal(left.byteValue() > right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() > right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() > right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() > right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() > right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() > right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isByteValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isByteValue() && right.isBoolValue()) return new BoolVal(left.byteValue() > right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() > right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() > right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() > right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() > right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isShortValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isShortValue() && right.isBoolValue()) return new BoolVal(left.shortValue() > right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() > right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() > right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isIntValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isIntValue() && right.isBoolValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() > right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() > right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() > right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() > right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new BoolVal(left.longValue() > right.longValue());

		else if(left.isLongValue() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isLongValue() && right.isBoolValue()) return new BoolVal(left.longValue() > right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() > right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new BoolVal(left.longValue() > right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLongValue() && right.isVector()) return new BoolVal(left.longValue() > right.longValue());

		else if(left.isRegister() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isRegister() && right.isBoolValue()) return new BoolVal(left.byteValue() > right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() > right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() > right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() > right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() > right.intValue());
			else return new BoolVal(left.longValue() > right.longValue());
		}

		else if(left.isVector() && right.isRealValue()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isVector() && right.isBoolValue()) return new BoolVal(left.byteValue() > right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() > right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new BoolVal(left.intValue() > right.intValue());
        else if(left.isVector() && right.isIntValue()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isVector() && right.isLongValue()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() > right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value bitwiseAnd(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() & right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() & right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() & right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() & right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() & right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() & right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() & right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() & right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else {
			errorAndExit("Inavlid bitwise and operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value bitwiseAndCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new AndGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new AndGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value bitwiseOr(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() | right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() | right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() | right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() | right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() | right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() | right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() | right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() | right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value bitwiseOrCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new OrGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new OrGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value exclusiveOr(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() ^ right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value bitwiseXorCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new XorGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new XorGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value exclusiveNor(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue())); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue())); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue())); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue())); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue())); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue())); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else {
			errorAndExit("Inavlid ^ operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

	public static Value bitwiseXnorCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new XnorGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new XnorGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value leftShift(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() << right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() << right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() << right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() << right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() << right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() << right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() << right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() << right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value rightShift(Value left, Value right) throws Exception{
        if(left.isBoolValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isBoolValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isBoolValue() && right.isByteValue()) return new ByteVal(left.byteValue() >> right.byteValue());
		else if(left.isBoolValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isBoolValue() && right.isIntValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isBoolValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isBoolValue() && right.isShortValue()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isBoolValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isBoolValue() && right.isLongValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isBoolValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isBoolValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedByteValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedByteValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		if(left.isByteValue() && right.isBoolValue()) return new ByteVal(left.byteValue() >> right.byteValue());
        else if(left.isByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isByteValue() && right.isByteValue()) return new ByteVal(left.byteValue() >> right.byteValue());
		else if(left.isByteValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isByteValue() && right.isIntValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isByteValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isByteValue() && right.isShortValue()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isByteValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isByteValue() && right.isLongValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
        else if(left.isUnsignedShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue()); 
        else if(left.isUnsignedShortValue() && right.isByteValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedShortValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShortValue() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedShortValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedShortValue() && right.isRegister()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isShortValue() && right.isBoolValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
        else if(left.isShortValue() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue()); 
        else if(left.isShortValue() && right.isByteValue()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShortValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isShortValue() && right.isIntValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isShortValue() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShortValue() && right.isShortValue()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShortValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isShortValue() && right.isLongValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isShortValue() && right.isRegister()) return new UnsignedShortVal(left.byteValue() >> right.byteValue());
		else if(left.isShortValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() >> right.intValue()); 
        else if(left.isUnsignedIntValue() && right.isByteValue()) return new UnsignedIntVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedIntValue() && right.isIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedIntValue() && right.isShortValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedIntValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isIntValue() && right.isBoolValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isIntValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() >> right.intValue()); 
        else if(left.isIntValue() && right.isByteValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isIntValue() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isIntValue() && right.isIntValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isIntValue() && right.isUnsignedShortValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isIntValue() && right.isShortValue()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isIntValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isIntValue() && right.isLongValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isIntValue() && right.isRegister()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isIntValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isUnsignedLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() >> right.longValue()); 
        else if(left.isUnsignedLongValue() && right.isByteValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isUnsignedLongValue() && right.isIntValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isShortValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());

		else if(left.isLongValue() && right.isBoolValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isLongValue() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() >> right.longValue()); 
        else if(left.isLongValue() && right.isByteValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isUnsignedIntValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isLongValue() && right.isIntValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isUnsignedShortValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isShortValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isLongValue()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isRegister()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLongValue() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());

		else if(left.isRegister() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isRegister() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isRegister() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isRegister() && right.isIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isRegister() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isRegister() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isRegister() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isRegister() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isVector() && right.isBoolValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isVector() && right.isByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isVector() && right.isUnsignedIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isVector() && right.isIntValue()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isVector() && right.isUnsignedShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isVector() && right.isShortValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isVector() && right.isUnsignedLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isVector() && right.isLongValue()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value negation(Value right) throws Exception{
        if (right.isRealValue()) return new RealVal(-right.realValue());
        else if(right.isUnsignedByteValue()) return new UnsignedByteVal(-right.byteValue());
		else if(right.isUnsignedShortValue()) return new UnsignedShortVal(-right.shortValue());
		else if(right.isUnsignedIntValue()) return new UnsignedIntVal(-right.intValue());
		else if(right.isUnsignedLongValue()) return new UnsignedLongVal(-right.longValue());
		else if(right.isByteValue()) return new ByteVal(-right.byteValue());
		else if(right.isShortValue()) return new ShortVal(-right.shortValue());
		else if(right.isIntValue()) return new IntVal(-right.intValue());
		else if(right.isLongValue()) return new LongVal(-right.longValue());
		else if(right.isVector()){
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(-vec.byteValue());
			else if(size <= 16) return new UnsignedShortVal(-vec.shortValue());
			else if(size <= 32) return new UnsignedIntVal(-vec.intValue());
			else return new UnsignedLongVal(-vec.longValue());
		} else {
			errorAndExit("Error invalid paramater type for negation operation of type " + right.getClass().getSimpleName());
			return null;
		}
    }

	public static Value notGateCircuit(Value right) throws Exception{
		if(right.isWire()){
			WireVal Output = new WireVal();
			WireVal rightW = (WireVal)right;
			new NotGate(Output, rightW);

			return Output;
		} else if(right.isVector()){
			VectorVal rightV = (VectorVal)right;
			VectorVal Output = new VectorVal(0, rightV.getSize() - 1);

			int vecStart = rightV.getStart();
			int vecEnd = rightV.getEnd();

			int outStart = Output.getStart();
			int outEnd = Output.getEnd();

			while(vecStart != vecEnd){
				WireVal Input = new WireVal();
				Input.assignInput(rightV.getValue(vecStart));

				WireVal OutW = new WireVal();
				OutW.addOutput(Output.getValue(outStart));

				new NotGate(OutW, Input);

				outStart++;
				vecStart++;
			}

			return Output;
		} else {
			Utils.errorAndExit("Error invlid type for NotGate");
			return Utils.errorOccured();
		}
	}

    public static Value logicalNegation(Value right){
        return new BoolVal(!right.boolValue());
    }

    public static Value bitwiseNegation(Value right) throws Exception{
		if(right.isVector()) return bitwiseNegation((VectorVal)right);
		else if(right.isRegister()) return bitwiseNegation((RegVal)right);
		else if(right.isUnsignedByteValue()) return new UnsignedByteVal(~right.byteValue());
		else if(right.isUnsignedShortValue()) return new UnsignedShortVal(~right.shortValue());
		else if(right.isUnsignedIntValue()) return new UnsignedIntVal(~right.intValue());
		else if(right.isUnsignedLongValue()) return new UnsignedLongVal(~right.longValue());
		else if(right.isByteValue()) return new ByteVal(~right.byteValue());
		else if(right.isShortValue()) return new ShortVal(~right.shortValue());
		else if(right.isIntValue()) return new IntVal(~right.intValue());
		else if(right.isLongValue()) return new LongVal(~right.longValue());
		else {
			errorAndExit("Error Unknown type for bitwise negation " + right.getClass().getSimpleName());
			return null;
		}
    }

	private static VectorVal bitwiseNegation(VectorVal vec){
		VectorVal vec2 = new VectorVal(vec.getIndex1(), vec.getIndex2());

		int start = vec.getStart();
		int end = vec.getEnd();
		for(int i = start; i <= end; i++){
			boolean state = vec.getValue(i).getStateSignal();
			vec2.setValue(i, new RegVal(state));
		}

		return vec2;
	}

	private static RegVal bitwiseNegation(RegVal reg){
		boolean signal = reg.getStateSignal();
		return new RegVal(!signal);
	}

	/**
	 * This is used to visit casedz statements in verilog
	 * 
	 * @param assign
	 * @throws Exception
	 */

	public static boolean caseBoolean(Value target, Value Val) throws Exception{
		if (Val instanceof Pattern) {
			Pattern pat = (Pattern)Val;
			return pat.match(target);
		} else {
			return target.longValue() == Val.longValue();
		}
	}

	public static String getParamaterName(ModuleItem Item){
		if(Item instanceof Input.Reg.Scalar.Ident){
			Input.Reg.Scalar.Ident InputItem = (Input.Reg.Scalar.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Reg.Vector.Ident){
			Input.Reg.Vector.Ident InputItem = (Input.Reg.Vector.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Wire.Scalar.Ident){
			Input.Wire.Scalar.Ident InputItem = (Input.Wire.Scalar.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Wire.Vector.Ident){
			Input.Wire.Vector.Ident InputItem = (Input.Wire.Vector.Ident)Item;
			return InputItem.declarationIdentifier;
		} else {
			return null;
		}
	}

    /**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the value is coppied
	 * over from one register to another a register can hold a True or a False value
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the value from one register to the value of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the value was assigned to. In other
	 * words it makes a copy.
	 */

	public static void shallowAssign(VectorVal vec1, VectorVal vec2){
		if (vec1.getSize() == vec2.getSize()) {
			int start1 = vec1.getStart();
			int start2 = vec2.getStart();
			int end1 = vec1.getEnd();
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else if (vec1.getSize() < vec2.getSize()) {
			int start1 = vec1.getStart();
			int start2 = vec2.getStart();
			int end1 = vec1.getEnd();
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - vec1.getSize());

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				elem1.setSignal(false);
				sIndex1++;
			}

		}

	}

	/**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the value is coppied
	 * over from one register to another a register can hold a True or a False value
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the value from one register to the value of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the value was assigned to. In other
	 * words it makes a copy.
	 */
	public static void shallowAssign(VectorVal vec1, int index, VectorVal vec2){
		int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
		((RegVal)vec1.getValue(index)).setSignal(vec2.getValue(start2).getStateSignal());
	}

	public static void shallowAssign(VectorVal vec1, String str2){

		if (vec1.getSize() == str2.length()) {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex2 = start2;

			if (start1 <= end1) {

				for (int i = start1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			}

		} else if (vec1.getSize() < str2.length()) {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex2 = start2 + (str2.length() - vec1.getSize());

			if (start1 <= end1) {

				for (int i = start1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			}

		} else {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex1 = start1;
			int sIndex2 = start2;
			int incr = vec1.getIndex1() <= vec1.getIndex2() ? 1 : -1;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
				elem1.setSignal(signal);
				sIndex1 += incr;
				sIndex2++;
			}

			if (start1 <= end1) {

				for (int i = sIndex1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					elem1.setSignal(false);
				}

			} else {

				for (int i = sIndex1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					elem1.setSignal(false);
				}

			}

		}

	}

	/**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the value is coppied
	 * over from one register to another a register can hold a True or a False value
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the value from one register to the value of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the value was assigned to. In other
	 * words it makes a copy.
	 */
	public static void shallowAssign(VectorVal vec1, int index1, int index2, VectorVal vec2){

		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else if (sliceSize < vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - sliceSize);

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				elem1.setSignal(false);
				sIndex1++;
			}

		}

	}
	
	/**
	 * In a deep assignment the value is assigned by reference. So the assignment works by
	 * literally replacing a register that is in their with another register. This is useful
	 * for a continuous assignment in verilog. As opposed to observing the expression on the
	 * right hand side with a loop and copying changes to the left hand side I am actually
	 * making the objects that are on the right hand side the things that are on the left
	 * hand side so changes come across in both variables.
	 * 
	 * @author Jacob Bauer
	 * @throws Exception
	 */

	public static void deepAssign(VectorVal vec1, int index, Value vec2) throws Exception{
		CircuitElem assignTo = vec1.getValue(index);
		Utils.deepAssign(assignTo, vec2);
	}

	public static void deepAssign(ArrayVal<Value> arr1, int index, Value vec2) throws Exception{
		Value arrVal = arr1.ElemAtIndex(index);
		Utils.deepAssign((CircuitElem)arrVal, vec2);
	}

	public static void deepAssign(VectorVal vec1, int index1, int index2, Value elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		Utils.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, CircuitElem elem2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		Utils.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, int index1, CircuitElem elem2){ 
		WireVal vec1Value = new WireVal();
		vec1Value.addOutput(vec1.getValue(index1));
		vec1Value.assignInput(elem2);
	}

	public static void deepAssign(CircuitElem elemTo, Value vec2) throws Exception{
		if(vec2.isWire()){
			WireVal connector = new WireVal();
			WireVal vec2Wire = (WireVal)vec2;
			connector.assignInput(elemTo);
			connector.addOutput(vec2Wire);
		} else if(vec2.isRegister()){
			WireVal connector = new WireVal();
			RegVal vec2Reg = (RegVal)vec2;
			connector.assignInput(elemTo);
			connector.addOutput(vec2Reg);
		} else {
			Utils.errorAndExit("Error and exit  : " + elemTo.getClass().getName() + " and " + vec2.getClass().getName());
		}
	}

	public static void deepAssign(VectorVal vec, VectorVal vector){
		if (vec.getSize() == vector.getSize()) {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}
		} else if (vec.getSize() < vector.getSize()) {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();
			int end2 = vector.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vector.getSize() - vec.getSize());

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();
			int end2 = vector.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
			}
		}
	}

	public static void deepAssign(VectorVal vec1, int index1, int index2, VectorVal vec2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else if (sliceSize < vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - sliceSize);

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
			}
		}
	}

	public static void shallowAssign(CircuitElem elem1, VectorVal vec2) throws Exception{
		int index = vec2.getStart();
		Utils.shallowAssign(elem1, vec2.getValue(index));
	}

	public static void shallowAssign(VectorVal vec1, CircuitElem elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		Utils.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.shallowAssign(vec1.getValue(sIndex1), elem2);
			sIndex1++;
		}
	}

	public static void shallowAssign(CircuitElem elem1, CircuitElem elem2) throws Exception{
		if(elem1.isRegister()){
			RegVal reg1 = (RegVal)elem1;
			reg1.setSignal(elem2.getStateSignal());
		} else {
			Utils.errorAndExit("CircuitElement shallow asignment cannoot take place with types " + elem1.getClass().getName() + " and " + elem2.getClass().getName());
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, CircuitElem elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		
		Utils.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.shallowAssign(vec1.getValue(sIndex1), false);
			sIndex1++;
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, CircuitElem elem2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		reg1.setSignal(elem2.getStateSignal());
	}

	public static void shallowAssign(VectorVal vec1, long int2){

		if (vec1.getSize() <= 64) {
			int over = 0;
			int start = vec1.getStart();
			int end = vec1.getEnd();

			for (int i = start; i <= end; i++) {
				boolean signal = ((int2 >> over) & 1) > 0;
				Utils.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = vec1.getStart();
			int end = vec1.getEnd();
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = ((int2 >> i) & 1) > 0;
				Utils.shallowAssign(vec1, sIndex1, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				Utils.shallowAssign(vec1, sIndex1, false);
				sIndex1++;
			}
		}

	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, long int2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize <= 64) {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int over = 0;

			for (int i = start; i <= end; i++) {
				boolean signal = ((int2 >> over)& 1) > 0;
				Utils.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = ((int2 >> i) & 1) > 0;
				Utils.shallowAssign(vec1, sIndex1, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				Utils.shallowAssign(vec1, sIndex1, false);
				sIndex1++;
			}
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, long int2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		boolean signal = (int2 & 1) > 0;
		reg1.setSignal(signal);
	}

	public static void shallowAssign(CircuitElem elem1, long int2){
		RegVal reg1 = (RegVal)elem1;
		boolean signal = (int2 & 1) > 0; 
		reg1.setSignal(signal); 
	}

	public static void shallowAssign(VectorVal vec1, boolean bool2){
		int start = vec1.getStart();
		int end = vec1.getEnd();

		Utils.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			Utils.shallowAssign(vec1, i, false);
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, boolean bool2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		Utils.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			Utils.shallowAssign(vec1, i, false);
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, boolean bool2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		reg1.setSignal(bool2);
	}

	public static void shallowAssign(CircuitElem elem1, boolean bool2){ 
		RegVal reg1 = (RegVal)elem1;
		reg1.setSignal(bool2);
	}

	public static boolean numberIsPattern(String Number){
		return Number.contains("x") || Number.contains("X") || Number.contains("z") || Number.contains("Z");
	}

	public static Object getRawValue(Value val){
		if(val.isBoolValue()) return val.intValue();
		else if(val.isRealValue()) return val.realValue();
		else if(val.isStringValue()) return val.toString();
		else if(val.isByteValue()) return val.byteValue();
		else if(val.isUnsignedByteValue()) return val.shortValue();
		else if(val.isShortValue()) return val.shortValue();
		else if(val.isUnsignedShortValue()) return val.intValue();
		else if(val.isIntValue()) return val.intValue();
		else return val.longValue();
	}

	public static Value convertToRawValue(Object obj){
		if(obj instanceof Integer) return new IntVal((int)obj);
		else if(obj instanceof Boolean) return new BoolVal((boolean)obj);
		else if(obj instanceof Long) return new LongVal((long)obj);
		else if(obj instanceof Short) return new ShortVal((short)obj);
		else if(obj instanceof Byte) return new ByteVal((byte)obj);
		else return new StrVal((String)obj);
	}

	public static Value getOptimalForm(VectorVal vec){
		if(vec.getSize() == 1) return new RegVal(vec.getValue(vec.getStart()).getStateSignal());
		else if(vec.getSize() == 8) return new UnsignedByteVal(vec.byteValue());
		else if(vec.getSize() == 16) return new UnsignedShortVal(vec.shortValue());
		else if(vec.getSize() == 32) return new UnsignedIntVal(vec.intValue());
		else if(vec.getSize() == 64) return new UnsignedLongVal(vec.longValue());
		else return vec;
	}
}
