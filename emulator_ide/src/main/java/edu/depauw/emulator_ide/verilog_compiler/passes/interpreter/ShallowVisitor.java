package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.Add;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BasicEquality;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BasicInequality;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitshiftLeft;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitshiftRight;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseAnd;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseNand;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseNor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseOr;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseXnor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.BitwiseXor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.Divide;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.GreaterThan;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.GreaterThanOrEqualTo;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.LessThan;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.LessThanOrEqualTo;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.LogicalAnd;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.LogicalOr;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.Modulo;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.Multiply;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.StrictEquality;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.StrictInequality;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.binary.Subtract;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.BitwiseNegation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.LogicalNegation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.Negation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionAnd;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionNand;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionNor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionOr;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionXnor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.ReductionXor;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.UnaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.BoolVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ByteVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.RealVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ShortVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.UnsignedByteVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.UnsignedIntVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.UnsignedLongVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.UnsignedShortVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.pattern.Pattern;
import edu.depauw.emulator_ide.verilog_compiler.passes.util.*;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.*;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.gui.Main;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;

import java.util.concurrent.Semaphore;
import javax.management.RuntimeErrorException;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;

public class ShallowVisitor implements ExpressionVisitor<Value> {

	private final ErrorLog                               errorLog;
	private Environment environment;
	private Interpreter interpreter;

	public ShallowVisitor(ErrorLog errorLog, Environment environment){
		this.errorLog = errorLog;
		this.environment = environment;
		this.interpreter = new Interpreter(errorLog);
	}

	private void errorAndExit(String errorParam){ 
		Error error = new Error(errorParam);
		throw new RuntimeErrorException(error);
	}

	private void errorAndExit(String error, Position pos){
		errorAndExit(error + pos.toString());
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(Add node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			return new StrVal(left.toString() + right.toString());
		} else if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(rLeft.getValue() + rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new RealVal(rLeft.getValue() + lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new RealVal(rLeft.getValue() + iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new RealVal(rLeft.getValue() + sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new RealVal(rLeft.getValue() + bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new RealVal(rLeft.getValue() + ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() + sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() + bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() + (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(uiLeft.getValue() + rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() + iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() + sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() + bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() + (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() + bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() + (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() + sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() + (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() + rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() + lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() + iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() + sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() + bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() + bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() + (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) + (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) + bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) + bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) + bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) + bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) + bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) + bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) + bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) + bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}


	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(Subtract node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(rLeft.getValue() - rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new RealVal(rLeft.getValue() - lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new RealVal(rLeft.getValue() - iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new RealVal(rLeft.getValue() - sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new RealVal(rLeft.getValue() - bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new RealVal(rLeft.getValue() - ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() - sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() - bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() - (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(uiLeft.getValue() - rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() - iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() - sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() - bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() - (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() - bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() - (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() - sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() - (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() - rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() - lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() - iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() - sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() - bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() - bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() - (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) - (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) - bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) - bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) - bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) - bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) - bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) - bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) - bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) - bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(Multiply node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(rLeft.getValue() * rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new RealVal(rLeft.getValue() * lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new RealVal(rLeft.getValue() * iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new RealVal(rLeft.getValue() * sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new RealVal(rLeft.getValue() * bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new RealVal(rLeft.getValue() * ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() * sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() * bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() * (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(uiLeft.getValue() * rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() * iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() * sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() * bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() * (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() * bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() * (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() * sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() * (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() * rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() * lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() * iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() * sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() * bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() * bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() * (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) * (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) * bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) * bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) * bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) * bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) * bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) * bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) * bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) * bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(Divide node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(rLeft.getValue() / rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new RealVal(rLeft.getValue() / lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new RealVal(rLeft.getValue() / iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new RealVal(rLeft.getValue() / sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new RealVal(rLeft.getValue() / bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new RealVal(rLeft.getValue() / ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() / sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() / bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() / (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(uiLeft.getValue() / rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() / iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() / sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() / bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() / (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() / bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() / (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() / sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() / (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() / rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() / lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() / iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() / sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() / bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() / bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() / (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) / (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) / bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) / bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) / bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) / bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) / bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) / bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) / bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) / bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(Modulo node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(rLeft.getValue() % rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new RealVal(rLeft.getValue() % lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new RealVal(rLeft.getValue() % iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new RealVal(rLeft.getValue() % sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new RealVal(rLeft.getValue() % bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new RealVal(rLeft.getValue() % ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() % sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() % bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() % (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(uiLeft.getValue() % rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() % iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() % sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() % bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() % (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() % bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() % (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() % sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() % (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new RealVal(ulLeft.getValue() % rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() % lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() % iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() % sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() % bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() % bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() % (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) % (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) % bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) % bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) % bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) % bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) % bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) % bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) % bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) % bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitwiseAnd node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() & sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() & bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() & (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() & iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() & sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() & bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() & (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() & (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() & lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() & iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) & (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) & bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) & bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) & bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) & bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Value visit(BitwiseNand node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(~(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(iLeft.getValue() & iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(~(ulLeft.getValue() & sRight.getValue()));
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(~(ulLeft.getValue() & bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & bRight.getValue())));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() & bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() & sRight.getValue())));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() & (bRight.getValue() ? 1: 0))));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() & lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() & iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() & sRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() & bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() & bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() & (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) & (bright.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal(~((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal(~((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal(~((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal(~((bleft.getValue() ? 1 : 0) & bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~((bleft.getValue() ? 1 : 0) & bright.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)(~((bleft.getValue() ? 1 : 0) & bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~((bleft.getValue() ? 1 : 0) & bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) & bright.getValue())));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitwiseOr node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() | sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() | bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() | (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() | iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() | sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() | bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() | (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() | (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() | lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() | iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) | (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) | bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) | bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) | bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) | bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Value visit(BitwiseNor node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(~(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(iLeft.getValue() | iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(~(ulLeft.getValue() | sRight.getValue()));
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(~(ulLeft.getValue() | bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | bRight.getValue())));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() | bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() | sRight.getValue())));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() | (bRight.getValue() ? 1: 0))));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() | lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() | iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() | sRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() | bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() | bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() | (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) | (bright.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal(~((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal(~((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal(~((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal(~((bleft.getValue() ? 1 : 0) | bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~((bleft.getValue() ? 1 : 0) | bright.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)(~((bleft.getValue() ? 1 : 0) | bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~((bleft.getValue() ? 1 : 0) | bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) | bright.getValue())));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitwiseXor node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() ^ sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() ^ bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() ^ sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() ^ bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() ^ (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() ^ lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() ^ iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) ^ (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) ^ bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) ^ bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) ^ bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) ^ bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Value visit(BitwiseXnor node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(~(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(iLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(~(ulLeft.getValue() ^ sRight.getValue()));
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(~(ulLeft.getValue() ^ bRight.getValue()));
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ bRight.getValue())));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() ^ bRight.getValue())));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() ^ (bRight.getValue() ? 1: 0))));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(~(ulLeft.getValue() ^ lRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(~(ulLeft.getValue() ^ iRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(~(ulLeft.getValue() ^ sRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~(ulLeft.getValue() ^ bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() ^ bRight.getValue())));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(~(ulLeft.getValue() ^ (bRight.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) ^ (bright.getValue() ? 1 : 0))));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal(~((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal(~((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal(~((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal(~((bleft.getValue() ? 1 : 0) ^ bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(~((bleft.getValue() ? 1 : 0) ^ bright.getValue())));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)(~((bleft.getValue() ? 1 : 0) ^ bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(~((bleft.getValue() ? 1 : 0) ^ bright.getValue())));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)(~((bleft.getValue() ? 1 : 0) ^ bright.getValue())));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(LessThan node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() < rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() < lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() < iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() < sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() < bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() < ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() < (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() < (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() < rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() < (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() < iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() < sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() < (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() < bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() < (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() < bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() < bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() < (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() < (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() < rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() < lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() < iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() < sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() < bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() < bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() < (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) < (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) < bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) < bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) < bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) < bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) < bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) < bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) < bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) < bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}


	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(LessThanOrEqualTo node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() <= rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() <= lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() <= iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() <= sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() <= bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() <= ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() <= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() <= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() <= rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() <= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() <= iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() <= sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() <= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() <= bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() <= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() <= bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() <= bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() <= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() <= (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() <= rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() <= lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() <= iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() <= sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() <= bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() <= bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() <= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) <= (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) <= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) <= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) <= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) <= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) <= bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) <= bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) <= bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) <= bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(GreaterThan node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() > rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() > lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() > iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() > sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() > bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() > ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() > (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() > (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() > rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() > (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() > iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() > sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() > (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() > bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() > (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() > bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() > bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() > (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() > (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() > rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() > lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() > iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() > sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() > bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() > bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() > (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) > (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) > bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) > bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) > bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) > bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) > bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) > bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) > bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) > bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(GreaterThanOrEqualTo node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() >= rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() >= lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() >= iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() >= sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() >= bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() >= ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() >= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() >= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() >= rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() >= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() >= iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() >= sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() >= (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() >= bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() >= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() >= bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() >= bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() >= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() >= (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() >= rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() >= lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() >= iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() >= sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() >= bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() >= bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() >= (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) >= (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) >= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) >= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) >= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) >= bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) >= bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) >= bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) >= bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) >= bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(BasicEquality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() == rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() == lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() == iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() == sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() == bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() == ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() == (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() == (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() == rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() == (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() == iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() == sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() == (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() == bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() == (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() == bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() == bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() == (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() == (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() == bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() == bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() == (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) == bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) == bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) == bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) == bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(StrictEquality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() == rRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal rRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() == rRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() == iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() == sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() == bRight.getValue()));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) == (bright.getValue() ? 1 : 0)));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(BasicInequality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() != rRight.getValue());
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(rLeft.getValue() != lRight.getValue());
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(rLeft.getValue() != iRight.getValue());
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(rLeft.getValue() != sRight.getValue());
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(rLeft.getValue() != bRight.getValue());
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(rLeft.getValue() != ((bRight.getValue()) ? 1 : 0));
		} else if (left instanceof UnsignedLongVal && right instanceof RealVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof RealVal) {
			UnsignedIntVal uiLeft = (UnsignedIntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(uiLeft.getValue() != rRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(iLeft.getValue() != iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof RealVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != bRight.getValue());
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() != bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof RealVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new BoolVal((ulLeft.getValue() != bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) != bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) != bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) != bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal((bleft.getValue() ? 1 : 0) != bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(StrictInequality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(rLeft.getValue() != rRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal rRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != rRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != bRight.getValue()));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(((bleft.getValue() ? 1 : 0) != (bright.getValue() ? 1 : 0)));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(LogicalAnd node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal((rLeft.getValue() != 0) && (rRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal((rLeft.getValue() != 0) && (lRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal((rLeft.getValue() != 0) && (iRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((rLeft.getValue()) != 0 && (sRight.getValue() != 0));
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((rLeft.getValue() != 0) && (bRight.getValue() != 0));
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((rLeft.getValue() != 0)  && bRight.getValue());
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal((ulLeft.getValue() != 0) &&  rRight.getValue() != 0);
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal((ulLeft.getValue() != 0) && (lRight.getValue() != 0));
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal((ulLeft.getValue() != 0) && (iRight.getValue() != 0));
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && sRight.getValue() != 0);
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && bRight.getValue() != 0);
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && bRight.getValue());
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && rRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && lRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && iRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && sRight.getValue() != 0);
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && bRight.getValue() != 0);
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && bRight.getValue());
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && rRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && lRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && iRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && sRight.getValue() != 0));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && bRight.getValue() != 0));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && bRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && rRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && lRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 && iRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && sRight.getValue() != 0));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && bRight.getValue() != 0));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != 0 && bRight.getValue()));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(bleft.getValue() && bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal(bleft.getValue() && bright.getValue() != 0);
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal(bleft.getValue() && bright.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal((bleft.getValue() && bright.getValue() != 0));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal((bleft.getValue() && bright.getValue() != 0));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(LogicalOr node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof RealVal && right instanceof RealVal) {
			RealVal rLeft = (RealVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal((rLeft.getValue() != 0) || (rRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof LongVal) {
			RealVal rLeft = (RealVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal((rLeft.getValue() != 0) || (lRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof IntVal) {
			RealVal rLeft = (RealVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal((rLeft.getValue() != 0) || (iRight.getValue() != 0));
		} else if (left instanceof RealVal && right instanceof ShortVal){
			RealVal rLeft = (RealVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((rLeft.getValue()) != 0 || (sRight.getValue() != 0));
		} else if(left instanceof RealVal && right instanceof ByteVal){
			RealVal rLeft = (RealVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((rLeft.getValue() != 0) || (bRight.getValue() != 0));
		} else if(left instanceof RealVal && right instanceof BoolVal){
			RealVal rLeft = (RealVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((rLeft.getValue() != 0)  || bRight.getValue());
		} else if (left instanceof LongVal && right instanceof RealVal) {
			LongVal ulLeft = (LongVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal((ulLeft.getValue() != 0) || rRight.getValue() != 0);
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal((ulLeft.getValue() != 0) || (lRight.getValue() != 0));
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal((ulLeft.getValue() != 0) || (iRight.getValue() != 0));
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || sRight.getValue() != 0);
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || bRight.getValue() != 0);
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || bRight.getValue());
		} else if (left instanceof IntVal && right instanceof RealVal) {
			IntVal ulLeft = (IntVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || rRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || lRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || iRight.getValue() != 0);
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || sRight.getValue() != 0);
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || bRight.getValue() != 0);
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || bRight.getValue());
		} else if (left instanceof ShortVal && right instanceof RealVal) {
			ShortVal ulLeft = (ShortVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || rRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || lRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || iRight.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || sRight.getValue() != 0));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || bRight.getValue() != 0));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || bRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof RealVal) {
			ByteVal ulLeft = (ByteVal)left;
			RealVal rRight = (RealVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || rRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || lRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new BoolVal(ulLeft.getValue() != 0 || iRight.getValue() != 0);
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || sRight.getValue() != 0));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || bRight.getValue() != 0));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new BoolVal((ulLeft.getValue() != 0 || bRight.getValue()));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new BoolVal(bleft.getValue() || bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new BoolVal(bleft.getValue() || bright.getValue() != 0);
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new BoolVal(bleft.getValue() || bright.getValue() != 0);
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new BoolVal((bleft.getValue() || bright.getValue() != 0));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new BoolVal((bleft.getValue() || bright.getValue() != 0));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitshiftLeft node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() << sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() << bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() << (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() << iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() << sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() << bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() << (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() << bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() << (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() << sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() << (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() << lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() << iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() << sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() << bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() << bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() << (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) << (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) << bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) << bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) << bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) << bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) << bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) << bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) << bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) << bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitshiftRight node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if (left instanceof UnsignedLongVal && right instanceof LongVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof IntVal) {
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof UnsignedLongVal && right instanceof ShortVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> sRight.getValue());
		} else if(left instanceof UnsignedLongVal && right instanceof BoolVal){
			UnsignedLongVal ulLeft = (UnsignedLongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0));
		} else if (left instanceof LongVal && right instanceof UnsignedLongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof LongVal && right instanceof LongVal) {
			LongVal ulLeft = (LongVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedIntVal) {
			LongVal ulLeft = (LongVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof LongVal && right instanceof IntVal) {
			LongVal ulLeft = (LongVal)left;
			IntVal iRight = (IntVal)right;
			return new LongVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof LongVal && right instanceof UnsignedShortVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> sRight.getValue());
		} else if (left instanceof LongVal && right instanceof ShortVal){
			LongVal ulLeft = (LongVal)left;
			ShortVal sRight = (ShortVal)right;
			return new LongVal(ulLeft.getValue() >> sRight.getValue());
		} else if(left instanceof LongVal && right instanceof UnsignedByteVal){
			LongVal ulLeft = (LongVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> bRight.getValue());
		} else if(left instanceof LongVal && right instanceof ByteVal){
			LongVal ulLeft = (LongVal)left;
			ByteVal bRight = (ByteVal)right;
			return new LongVal(ulLeft.getValue() >> bRight.getValue());
		} else if(left instanceof LongVal && right instanceof BoolVal){
			LongVal ulLeft = (LongVal)left;
			BoolVal bRight = (BoolVal)right;
			return new LongVal(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedIntVal && right instanceof LongVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof IntVal) {
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof UnsignedIntVal && right instanceof ShortVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> sRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof ByteVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> bRight.getValue());
		} else if(left instanceof UnsignedIntVal && right instanceof BoolVal){
			UnsignedIntVal ulLeft = (UnsignedIntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0));
		} else if (left instanceof IntVal && right instanceof UnsignedLongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof IntVal && right instanceof LongVal) {
			IntVal ulLeft = (IntVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedIntVal) {
			IntVal iLeft = (IntVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(iLeft.getValue() >> iRight.getValue());
		} else if (left instanceof IntVal && right instanceof IntVal) {
			IntVal ulLeft = (IntVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof IntVal && right instanceof UnsignedShortVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> sRight.getValue());
		} else if (left instanceof IntVal && right instanceof ShortVal){
			IntVal ulLeft = (IntVal)left;
			ShortVal sRight = (ShortVal)right;
			return new IntVal(ulLeft.getValue() >> sRight.getValue());
		} else if(left instanceof IntVal && right instanceof UnsignedByteVal){
			IntVal ulLeft = (IntVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> bRight.getValue());
		} else if(left instanceof IntVal && right instanceof ByteVal){
			IntVal ulLeft = (IntVal)left;
			ByteVal bRight = (ByteVal)right;
			return new IntVal(ulLeft.getValue() >> bRight.getValue());
		} else if(left instanceof IntVal && right instanceof BoolVal){
			IntVal ulLeft = (IntVal)left;
			BoolVal bRight = (BoolVal)right;
			return new IntVal(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0));
		} else if (left instanceof UnsignedShortVal && right instanceof LongVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof IntVal) {
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof UnsignedShortVal && right instanceof ShortVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if(left instanceof UnsignedShortVal && right instanceof ByteVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> bRight.getValue()));
		}  else if(left instanceof UnsignedShortVal && right instanceof BoolVal){
			UnsignedShortVal ulLeft = (UnsignedShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof ShortVal && right instanceof UnsignedLongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof LongVal) {
			ShortVal ulLeft = (ShortVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedIntVal) {
			ShortVal ulLeft = (ShortVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof IntVal) {
			ShortVal ulLeft = (ShortVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof ShortVal && right instanceof UnsignedShortVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal){
			ShortVal ulLeft = (ShortVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof UnsignedByteVal){
			ShortVal ulLeft = (ShortVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof ByteVal){
			ShortVal ulLeft = (ShortVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ShortVal((short)(ulLeft.getValue() >> bRight.getValue()));
		} else if(left instanceof ShortVal && right instanceof BoolVal){
			ShortVal ulLeft = (ShortVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ShortVal((short)(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof UnsignedByteVal && right instanceof LongVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			LongVal lRight = (LongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof IntVal) {
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			IntVal iRight = (IntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof UnsignedByteVal && right instanceof ShortVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if (left instanceof UnsignedByteVal && right instanceof ByteVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			ByteVal sRight = (ByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() >> sRight.getValue()));
		} else if(left instanceof UnsignedByteVal && right instanceof BoolVal){
			UnsignedByteVal ulLeft = (UnsignedByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() >> (bRight.getValue() ? 1: 0)));
		} else if (left instanceof ByteVal && right instanceof UnsignedLongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof LongVal) {
			ByteVal ulLeft = (ByteVal)left;
			LongVal lRight = (LongVal)right;
			return new LongVal(ulLeft.getValue() >> lRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedIntVal) {
			ByteVal ulLeft = (ByteVal)left;
			UnsignedIntVal iRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof IntVal) {
			ByteVal ulLeft = (ByteVal)left;
			IntVal iRight = (IntVal)right;
			return new IntVal(ulLeft.getValue() >> iRight.getValue());
		} else if (left instanceof ByteVal && right instanceof UnsignedShortVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedShortVal sRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if (left instanceof ByteVal && right instanceof ShortVal){
			ByteVal ulLeft = (ByteVal)left;
			ShortVal sRight = (ShortVal)right;
			return new ShortVal((short)(ulLeft.getValue() >> sRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof UnsignedByteVal){
			ByteVal ulLeft = (ByteVal)left;
			UnsignedByteVal bRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(ulLeft.getValue() >> bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			ByteVal ulLeft = (ByteVal)left;
			ByteVal bRight = (ByteVal)right;
			return new ByteVal((byte)(ulLeft.getValue() >> bRight.getValue()));
		} else if(left instanceof ByteVal && right instanceof BoolVal){
			ByteVal ulLeft = (ByteVal)left;
			BoolVal bRight = (BoolVal)right;
			return new ByteVal((byte)(ulLeft.getValue() >> (bRight.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof BoolVal) {
			BoolVal bleft = (BoolVal)left;
			BoolVal bright = (BoolVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) >> (bright.getValue() ? 1 : 0)));
		} else if (left instanceof BoolVal && right instanceof UnsignedLongVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedLongVal bright = (UnsignedLongVal)right;
			return new UnsignedLongVal((bleft.getValue() ? 1 : 0) >> bright.getValue());
		} else if (left instanceof BoolVal && right instanceof LongVal) {
			BoolVal bleft = (BoolVal)left;
			LongVal bright = (LongVal)right;
			return new LongVal((bleft.getValue() ? 1 : 0) >> bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedIntVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedIntVal bright = (UnsignedIntVal)right;
			return new UnsignedIntVal((bleft.getValue() ? 1 : 0) >> bright.getValue());
		} else if (left instanceof BoolVal && right instanceof IntVal) {
			BoolVal bleft = (BoolVal)left;
			IntVal bright = (IntVal)right;
			return new IntVal((bleft.getValue() ? 1 : 0) >> bright.getValue());
		} else if (left instanceof BoolVal && right instanceof UnsignedShortVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedShortVal bright = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)((bleft.getValue() ? 1 : 0) >> bright.getValue()));
		} else if (left instanceof ShortVal && right instanceof ShortVal) {
			BoolVal bleft = (BoolVal)left;
			ShortVal bright = (ShortVal)right;
			return new ShortVal((short)((bleft.getValue() ? 1 : 0) >> bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof UnsignedByteVal) {
			BoolVal bleft = (BoolVal)left;
			UnsignedByteVal bright = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)((bleft.getValue() ? 1 : 0) >> bright.getValue()));
		} else if (left instanceof BoolVal && right instanceof ByteVal) {
			BoolVal bleft = (BoolVal)left;
			ByteVal bright = (ByteVal)right;
			return new ByteVal((byte)((bleft.getValue() ? 1 : 0) >> bright.getValue()));
		} else {
			errorAndExit("Could not add types " + left.getClass() + " and " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(Negation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof RealVal){
			RealVal rRight = (RealVal)right;
			return new RealVal(-rRight.getValue());
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(-rRight.getValue());
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(-rRight.getValue());
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)-rRight.getValue());
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)-rRight.getValue());
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)-(rRight.getValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitwiseNegation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(~rRight.getValue());
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(~rRight.getValue());
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(~rRight.getValue());
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(~rRight.getValue());
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)~rRight.getValue());
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)~rRight.getValue());
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)~rRight.getValue());
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)~rRight.getValue());
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)~(rRight.getValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(LogicalNegation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof RealVal){
			RealVal rRight = (RealVal)right;
			return new BoolVal(rRight.getValue() == 0);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new BoolVal(rRight.getValue() == 0);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new BoolVal(rRight.getValue() == 0);
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new BoolVal(rRight.getValue() == 0);
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new BoolVal(rRight.getValue() == 0);
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new BoolVal(!rRight.getValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionAnd node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(rRight.getValue() == -1 ? 1 : 0);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(rRight.getValue() == -1 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(rRight.getValue() == -1 ? 1 : 0);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(rRight.getValue() == -1 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(rRight.getValue() == -1 ? 1 : 0));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)(rRight.getValue() == -1 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(rRight.getValue() == -1 ? 1 : 0));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)(rRight.getValue() == -1 ? 1 : 0));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionNand node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(rRight.getValue() == -1 ? 0 : 1);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(rRight.getValue() == -1 ? 0 : 1);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(rRight.getValue() == -1 ? 0 : 1);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(rRight.getValue() == -1 ? 0 : 1);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(rRight.getValue() == -1 ? 0 : 1));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)(rRight.getValue() == -1 ? 0 : 1));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(rRight.getValue() == -1 ? 0 : 1));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)(rRight.getValue() == -1 ? 0 : 1));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 0 : 1));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionOr node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(rRight.getValue() == 0 ? 0 : 1);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(rRight.getValue() == 0 ? 0 : 1);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(rRight.getValue() == 0 ? 0 : 1);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(rRight.getValue() == 0 ? 0 : 1);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(rRight.getValue() == 0 ? 0 : 1));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)(rRight.getValue() == 0 ? 0 : 1));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(rRight.getValue() == 0 ? 0 : 1));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)(rRight.getValue() == 0 ? 0 : 1));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 0 : 1));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionNor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			return new UnsignedLongVal(rRight.getValue() == 0 ? 1 : 0);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			return new LongVal(rRight.getValue() == 0 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			return new UnsignedIntVal(rRight.getValue() == 0 ? 1 : 0);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			return new IntVal(rRight.getValue() == 0 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			return new UnsignedShortVal((short)(rRight.getValue() == 0 ? 1 : 0));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			return new ShortVal((short)(rRight.getValue() == 0 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			return new UnsignedByteVal((byte)(rRight.getValue() == 0 ? 1 : 0));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			return new ByteVal((byte)(rRight.getValue() == 0 ? 1 : 0));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionXor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedLongVal(num1s % 2);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new LongVal(num1s % 2);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedIntVal(num1s % 2);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new IntVal(num1s % 2);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedShortVal((short)(num1s % 2));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new ShortVal((short)(num1s % 2));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedByteVal((byte)(num1s % 2));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new ByteVal((byte)(num1s % 2));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionXnor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			UnsignedLongVal rRight = (UnsignedLongVal)right;
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedLongVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof LongVal){
			LongVal rRight = (LongVal)right;
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new LongVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			UnsignedIntVal rRight = (UnsignedIntVal)right;
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedIntVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new IntVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			UnsignedShortVal rRight = (UnsignedShortVal)right;
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedShortVal((short)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof ShortVal){
			ShortVal rRight = (ShortVal)right;
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new ShortVal((short)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			UnsignedByteVal rRight = (UnsignedByteVal)right;
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedByteVal((byte)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof ByteVal){
			ByteVal rRight = (ByteVal)right;
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (rRight.getValue() >> i == 1 ? 1 : 0);
			}
			return new ByteVal((byte)((num1s % 2) == 0 ? 1 : 0));
		} else if(right instanceof BoolVal){
			BoolVal rRight = (BoolVal)right;
			return new ByteVal((byte)(rRight.getValue() ? 0 : 1));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	public Value visit(Concatenation concat, Object... argv){
		int size = 0;

		for (int expr = 0; expr < concat.getNumCircuitElementExpressions(); expr++) {
			Value obj = concat.getCircuitElementExpression(expr).accept(this);

			if (obj instanceof Vector) {
				Vector objVector = (Vector)obj;
				size += objVector.getSize();
			} else if (obj instanceof CircuitElem){
				size++;
			} else {
				errorAndExit("Unexpected value of type " + obj.getClass() + ". In concatenation expressions we must only pass in REgister or Wire Values", concat.getPosition());
			}

		}

		Vector newVec = new Vector(size - 1, 0);
		int total = size - 1;

		for (int expr = 0; expr < concat.getNumCircuitElementExpressions(); expr++) {
			Value obj = concat.getCircuitElementExpression(expr).accept(this);

			if (obj instanceof Vector) {
				Vector vec = (Vector)obj;

				if (vec.getIndex1() <= vec.getIndex2()) {

					for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
						newVec.setValue(total, vec.getValue(v));
					}

				} else {

					for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
						newVec.setValue(total, vec.getValue(v));
					}

				}

			} else {
				CircuitElem circ = (CircuitElem)obj;
				newVec.setValue(total, circ);
				total--;
			}

		}

		return newVec;
	}

	/**
	 * This is the code for visiting Constant Expressions
	 * 
	 * @param expr
	 */

	public Value visit(ConstantExpression expr, Object... argv){ 
		return expr.expression.accept(this); 
	}

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	public Value visit(EmptyExpression expr, Object... argv){ return null; }

	public Value visit(FunctionCall call, Object... argv){
		String functionName = call.getSystemFunctionName();

		if (environment.functionExists(functionName)) {
			// Collect symbol table data from the function
			FunctionDeclaration decl = environment.lookupFunction(functionName);

			environment.addScope();
			decl.getFunctionName().accept(interpreter); //declare function variable

			Pointer<Value> returnData = environment.lookupVariable(functionName); // get return object

			for (int i = 0; i < decl.numberofParamaters(); i++) { decl.getParamater(i).accept(interpreter); }

			if (call.getNumberOfArguments() == decl.numberofParamaters()) {

				// Assign parameter values
				for (int i = 0; i < call.getNumberOfArguments(); i++) {
					Pointer<Value> varData = environment.lookupVariable();
					Value exp = call.getArgument(i).accept(this);
					varData.assign(exp);
				}

			} else {
				errorAndExit("Argument amount mismatch " + functionName + " [Expected -> " + decl.numberofParamaters()
					+ " | Got -> " + call.getNumberOfArguments() + " ]", call.getPosition());
				return null;
			}

			decl.getStatement().accept(interpreter);
			environment.removeScope();

			return returnData.deRefrence();
		} else {
			errorAndExit("No function found with the name " + functionName + " ", call.getPosition());
			return null;
		}

	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public Value visit(SystemFunctionCall call, Object... argv){
		String functionName = call.getSystemFunctionName();

		if (functionName.getLexeme().equals("fopen")) {

			if(call.getNumberOfArguments() != 2){
				errorAndExit("function fopen requires 2 arguments but found " + call.getNumberOfArguments(), call.getPosition());
			}

			String basePath = new File("").getAbsolutePath();

			StrVal fname = (StrVal)call.getArgument(0).accept(this);
			StrVal access = (StrVal)call.getArgument(1).accept(this);

			if (fname.toString().equals("GuiByteStream")) {
				String str = Main.getByteInputStream().toString();
				return new StrVal(str);
			} else {
				String absoluteFileName = basePath + '/' + fname;
				return new StrVal(absoluteFileName);
				return null;
			}

		} else if (functionName.getLexeme().equals("feof")) {
			if(call.getNumberOfArguments() != 1){
				errorAndExit("Function feof requires 1 argument but found " + call.getNumberOfArguments(), call.getPosition());
			}
			StrVal fReader = (StrVal)call.getArgument(0).accept(this);
			boolean hasNextLine = fReader.hasNextLine();
			return !hasNextLine;
		} else if (functionName.getLexeme().equals("fscanf")) {
			Scanner fReader = (Scanner)call.getExpression(0).accept(this);
			String fString = (String)call.getExpression(1).accept(this);
			Vector<CircuitElem> location = (Vector<CircuitElem>)call.getExpression(2).accept(this);
			String data = fReader.nextLine();
			// data = String.format(fString, data);
			OpUtil.shallowAssign(location, data);
			return (long)0; // allways true just for consistency with verilog
		} else if (functionName.getLexeme().equals("getMemory")) {
			long arg1 = longValue(call.getExpression(0).accept(this));
			return GuiMemory.getMemory(arg1);
		} else if (functionName.getLexeme().equals("getRegister")) {
			long arg1 = longValue(call.getExpression(0).accept(this));
			return GuiRegister.getRegister(arg1);
		} else if (functionName.getLexeme().equals("getStatus")) {
			String arg1 = (String)call.getExpression(0).accept(this);
			return GuiStatusBit.getStatus(arg1);
		} else {
			errorAndExit("Could not find a systemcall with the name " + functionName.getLexeme(), call.getPosition());
		}

		return null;
	}

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 */

	public Value visit(Identifier ident, Object... argv){

		if (varEnv.entryExists(ident.getLexeme())) {
			InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
			return data.getObject();
		} else {
			errorAndExit("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition());
			return null;
		}

	}

	/**
	 * This is the code for visiting an Number in verilog
	 * 
	 * @param number
	 */

	public Object visit(NumValue number, Object... argv){

		if (number.getLexeme().contains(".")) {
			return Double.parseDouble(number.getLexeme());
		} else {

			if (number.getLexeme().contains("\'")) {

				if (NumberUtil.getBase(number.getLexeme()) == 10) {
					return NumberUtil.getDecimal(number.getLexeme());
				} else if (NumberUtil.getBase(number.getLexeme()) == 8) {
					return NumberUtil.getOctal(number.getLexeme());
				} else if (NumberUtil.getBase(number.getLexeme()) == 16) {
					return NumberUtil.getHexidecimal(number.getLexeme());
				} else {
					return NumberUtil.getPattern(number.getLexeme());
				}

			} else {
				return Long.parseLong(number.getLexeme());
			}

		}

	}

	/**
	 * This is the code for visiting a port connection in verilog
	 * 
	 * @param connection
	 */

	public Object visit(PortConnection connection, Object... argv){ return connection.getExpression().accept(this); }

	/**
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	public Object visit(StrValue string, Object... argv){ return string.getLexeme(); }

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	public Object visit(TernaryOperation expr, Object... argv){

		if (boolValue(expr.getCondition().accept(this))) {
			return expr.getLeft().accept(this);
		} else {
			return expr.getRight().accept(this);
		}

	}

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public Object visit(VectorElement vector, Object... argv){
		Identifier ident = vector.getIdentifier();
		Object expr = vector.getExpression().accept(this);
		int index = (int)longValue(expr);

		if (varEnv.entryExists(ident.getLexeme())) {
			InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
			Object dataObject = data.getObject();

			if (dataObject instanceof Vector[]) {
				Vector<CircuitElem>[] arr = (Vector<CircuitElem>[])data.getObject();
				Vector<CircuitElem> vec = arr[index];
				return vec;
			} else if (dataObject instanceof Vector) {
				return ((Vector<CircuitElem>)dataObject).getValue(index);
			} else if (dataObject instanceof Long[]) {
				return ((Long[])dataObject)[index];
			} else {
				errorAndExit("Unkown array type for " + ident.getLexeme() + " [ Type -> " + dataObject.getClass() + " ]",
					ident.getPosition());
				return null;
			}

		} else {
			errorAndExit("Array or Vector " + ident.getLexeme() + " not found", ident.getPosition());
			return null;
		}

	}

	public Object visit(VectorSlice vector, Object... argv){
		Identifier ident = vector.getIdentifier();
		int startIndex = (int)longValue(vector.getExpression1().accept(this));
		int endIndex = (int)longValue(vector.getExpression2().accept(this));

		if (varEnv.entryExists(ident.getLexeme())) {
			InterpreterVariableData data = varEnv.getEntry(ident.getLexeme());
			Object dataObject = data.getObject();

			if (dataObject instanceof Vector) {
				Vector<CircuitElem> toRet = ((Vector<CircuitElem>)dataObject).getShallowSlice(startIndex, endIndex);
				return toRet;
			} else {
				errorAndExit("Unkown array type for " + ident.getLexeme() + " [ Type -> " + dataObject.getClass() + " ]",
					ident.getPosition());
				return null;
			}

		} else {
			errorAndExit("Array or Vector " + ident.getLexeme() + " not found", ident.getPosition());
			return null;
		}

	}

	/*
	 * Below are RegValue visitors. These are used in the declarations of Integers and
	 * Registers
	 */

	public Void visit(RegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();
		int start = (int)argv[0];
		int end = (int)argv[1];

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Vector vec = new Vector(start, end);

			if (start <= end) {

				for (int i = start; i <= end; i++) { vec.setValue(i, new Register(false)); }

			} else {

				for (int i = end; i <= start; i++) { vec.setValue(i, new Register(false)); }

			}

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(vec);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(RegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Register reg = new Register(false);

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(reg);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(reg, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(OutputRegVectorIdent regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();
		int start = (int)argv[0];
		int end = (int)argv[1];

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Vector vec = new Vector(start, end);

			if (start <= end) {

				for (int i = start; i <= end; i++) { vec.setValue(i, new Register(false)); }

			} else {

				for (int i = end; i <= start; i++) { vec.setValue(i, new Register(false)); }

			}

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(vec);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(vec, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(OutputRegScalarIdent regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {
			Register reg = new Register(false);

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject(reg);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(reg, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(IntegerIdent intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		if (inFunctionName) {
			setcallStackName(ident.getLexeme());
		} else {

			if (varEnv.inScope(ident.getLexeme())) {
				InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

				if (got.getObject() == null) {
					got.setObject((long)0);
				} else {
					errorAndExit("Redeclaration of variable " + ident.getLexeme() + " with undexpected type "
						+ got.getObject().getClass());
				}

			} else {
				varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData((long)0, ident.getPosition()));
			}

		}

		return null;
	}

	public Void visit(RegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		int index1 = (int)argv[0];
		int index2 = (int)argv[1];

		int aIndex1 = (int)(long)regVector.getExpression1().accept(this);
		int aIndex2 = (int)(long)regVector.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Vector<CircuitElem>[] arr = new Vector[aSize];

		for (int i = 0; i < aSize; i++) {
			arr[i] = new Vector<CircuitElem>(index1, index2);

			if (index1 <= index2) {

				for (int x = index1; x <= index2; x++) { arr[i].setValue(x, new Register(false)); }

			} else {

				for (int x = index2; x <= index1; x++) { arr[i].setValue(x, new Register(false)); }

			}

		}

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(RegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		int aIndex1 = (int)(long)regScalar.getExpression1().accept(this);
		int aIndex2 = (int)(long)regScalar.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		CircuitElem[] arr = new CircuitElem[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = new Register(false); }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegVectorArray regVector, Object... argv){
		Identifier ident = regVector.getIdentifier();

		int index1 = (int)argv[0];
		int index2 = (int)argv[1];

		int aIndex1 = (int)(long)regVector.getExpression1().accept(this);
		int aIndex2 = (int)(long)regVector.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Vector<CircuitElem>[] arr = new Vector[aSize];

		for (int i = 0; i < aSize; i++) {
			arr[i] = new Vector<CircuitElem>(index1, index2);

			if (index1 <= index2) {

				for (int x = index1; x <= index2; x++) { arr[i].setValue(x, new Register(false)); }

			} else {

				for (int x = index2; x <= index1; x++) { arr[i].setValue(x, new Register(false)); }

			}

		}

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(OutputRegScalarArray regScalar, Object... argv){
		Identifier ident = regScalar.getIdentifier();

		int aIndex1 = (int)(long)regScalar.getExpression1().accept(this);
		int aIndex2 = (int)(long)regScalar.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		CircuitElem[] arr = new CircuitElem[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = new Register(false); }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}

	public Void visit(IntegerArray intIdent, Object... argv){
		Identifier ident = intIdent.getIdentifier();

		int aIndex1 = (int)(long)intIdent.getExpression1().accept(this);
		int aIndex2 = (int)(long)intIdent.getExpression2().accept(this);

		int aSize = (aIndex2 > aIndex1) ? aIndex2 - aIndex1 + 1 : aIndex1 - aIndex2 + 1;

		Long[] arr = new Long[aSize];

		for (int i = 0; i < aSize; i++) { arr[i] = (long)0; }

		if (varEnv.inScope(ident.getLexeme())) {
			InterpreterVariableData got = (InterpreterVariableData)varEnv.getEntry(ident.getLexeme());

			if (got.getObject() == null) {
				got.setObject(arr);
			} else {
				errorAndExit(
					"Redeclaration of variable " + ident.getLexeme() + " with undexpected type " + got.getObject().getClass());
			}

		} else {
			varEnv.addEntry(ident.getLexeme(), new InterpreterVariableData(arr, ident.getPosition()));
		}

		return null;
	}
}
