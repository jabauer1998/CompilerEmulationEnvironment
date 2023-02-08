package edu.depauw.emulator_ide.verilog_compiler.interpreter;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.BoolVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ByteVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.RealVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ShortVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Unsigned;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.UnsignedByteVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.UnsignedIntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.UnsignedLongVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.UnsignedShortVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.pattern.Pattern;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.FunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.UnaryOperation;
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
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.value_node.StringNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Element;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.label.Slice;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.util.NumberUtil;
import edu.depauw.emulator_ide.verilog_compiler.util.OpUtil;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.util.*;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.*;
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

	private static Value getUnsignedNumValue(long input){
		if(((byte)input) == input){
			return new UnsignedByteVal((byte)input);
		} else if(((short)input) == input){
			return new UnsignedShortVal((short)input);
		} else if(((int)input) == input){
			return new UnsignedIntVal((int)input);
		} else {
			return new UnsignedLongVal(input);
		}
	}

	private static Value getSignedNumValue(long input){
		if(((byte)input) == input){
			return new ByteVal((byte)input);
		} else if(((short)input) == input){
			return new ShortVal((short)input);
		} else if(((int)input) == input){
			return new IntVal((int)input);
		} else {
			return new LongVal(input);
		}
	}

	public Value visit(Add node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal) {
			return new StrVal(left.toString() + right.toString());
		} else if(right instanceof StrVal){
			errorAndExit("Error: Cant add types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			return new RealVal(left.realValue() + right.realValue());
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() + right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() + right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() + right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() + right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() + right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() + right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() + right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() + right.byteValue());
			}
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant add types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			return new RealVal(left.realValue() - right.realValue());
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() - right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() - right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() - right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() - right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() - right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() - right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() - right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() - right.byteValue());
			}
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant add types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			return new RealVal(left.realValue() * right.realValue());
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() * right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() * right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() * right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() * right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() * right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() * right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() * right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() * right.byteValue());
			}
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	private static Value getUnsignedRealValue(double input){
		if(((long)input) != input){
			return new RealVal(input);
		} else {
			return getUnsignedNumValue((long)input);
		}
	}

	private static Value getSignedRealValue(double input){
		if(((long)input) != input){
			return new RealVal(input);
		} else {
			return getSignedNumValue((long)input);
		}
	}

	public Value visit(Divide node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant add types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new RealVal(left.realValue() / right.realValue());
		} else if(left instanceof Unsigned || right instanceof Unsigned){
			return getUnsignedRealValue(left.realValue() / right.realValue());
		} else {
			return getSignedRealValue(left.realValue() / right.realValue());
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(Modulo node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			return new RealVal(left.realValue() % right.realValue());
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() % right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() % right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() % right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() % right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() % right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() % right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() % right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() % right.byteValue());
			}
		}
	}

	public Value visit(BitwiseAnd node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() & right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() & right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() & right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() & right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() & right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() & right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() & right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() & right.byteValue());
			}
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(~(left.longValue() & right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(~(left.intValue() & right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(~(left.shortValue() & right.shortValue()));
			} else {
				return getUnsignedNumValue(~(left.byteValue() & right.byteValue()));
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(~(left.longValue() & right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(~(left.intValue() & right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(~(left.shortValue() & right.shortValue()));
			} else {
				return getSignedNumValue(~(left.byteValue() & right.byteValue()));
			}
		}
	}

	public Value visit(BitwiseOr node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() | right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() | right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() | right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() | right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() | right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() | right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() | right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() | right.byteValue());
			}
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(~(left.longValue() | right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(~(left.intValue() | right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(~(left.shortValue() | right.shortValue()));
			} else {
				return getUnsignedNumValue(~(left.byteValue() | right.byteValue()));
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(~(left.longValue() | right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(~(left.intValue() | right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(~(left.shortValue() | right.shortValue()));
			} else {
				return getSignedNumValue(~(left.byteValue() | right.byteValue()));
			}
		}
	}

	public Value visit(BitwiseXor node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() ^ right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() ^ right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() ^ right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() ^ right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() ^ right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() ^ right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() ^ right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() ^ right.byteValue());
			}
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(~(left.longValue() ^ right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(~(left.intValue() ^ right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(~(left.shortValue() ^ right.shortValue()));
			} else {
				return getUnsignedNumValue(~(left.byteValue() ^ right.byteValue()));
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(~(left.longValue() ^ right.longValue()));
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(~(left.intValue() ^ right.intValue()));
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(~(left.shortValue() ^ right.shortValue()));
			} else {
				return getSignedNumValue(~(left.byteValue() ^ right.byteValue()));
			}
		}
	}

	public Value visit(LessThan node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() < right.realValue());
		} else {
			return new BoolVal(left.longValue() < right.longValue());
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() <= right.realValue());
		} else {
			return new BoolVal(left.longValue() <= right.longValue());
		}
	}

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	/*
	 * Below is the code that is used for visiting expressions /** This is the code for
	 * visiting binary operations
	 * 
	 * @param op
	 */

	public Value visit(GreaterThan node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() > right.realValue());
		} else {
			return new BoolVal(left.longValue() > right.longValue());
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() >= right.realValue());
		} else {
			return new BoolVal(left.longValue() >= right.longValue());
		}
	}

	public Value visit(BasicEquality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal && right instanceof StrVal){
			return new BoolVal(left.toString().equals(right.toString()));
		} else if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant equate types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() == right.realValue());
		} else {
			return new BoolVal(left.longValue() == right.longValue());
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
		if(left instanceof StrVal && right instanceof StrVal){
			return new BoolVal(left.toString().equals(right.toString()));
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() == right.realValue());
		} else if(left instanceof LongVal && right instanceof LongVal){
			return new BoolVal(left.longValue() == right.longValue());
		} else if(left instanceof IntVal && right instanceof IntVal){
			return new BoolVal(left.intValue() == right.intValue());
		} else if(left instanceof ShortVal && right instanceof ShortVal){
			return new BoolVal(left.shortValue() == right.shortValue());
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			return new BoolVal(left.byteValue() == right.byteValue());
		} else if(left instanceof BoolVal && right instanceof BoolVal){
			return new BoolVal(left.boolValue() == right.boolValue());
		} else {
			errorAndExit("Error: In === type mismatch between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		}
	}

	public Value visit(BasicInequality node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal && right instanceof StrVal){
			return new BoolVal(left.toString().equals(right.toString()));
		} else if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant equate types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() != right.realValue());
		} else {
			return new BoolVal(left.longValue() != right.longValue());
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
		if(left instanceof StrVal && right instanceof StrVal){
			return new BoolVal(left.toString().equals(right.toString()));
		} else if(left instanceof RealVal || right instanceof RealVal){
			return new BoolVal(left.realValue() != right.realValue());
		} else if(left instanceof LongVal && right instanceof LongVal){
			return new BoolVal(left.longValue() != right.longValue());
		} else if(left instanceof IntVal && right instanceof IntVal){
			return new BoolVal(left.intValue() != right.intValue());
		} else if(left instanceof ShortVal && right instanceof ShortVal){
			return new BoolVal(left.shortValue() != right.shortValue());
		} else if(left instanceof ByteVal && right instanceof ByteVal){
			return new BoolVal(left.byteValue() != right.byteValue());
		} else if(left instanceof BoolVal && right instanceof BoolVal){
			return new BoolVal(left.boolValue() != right.boolValue());
		} else {
			errorAndExit("Error: In === type mismatch between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else {
			return new BoolVal(left.boolValue() && right.boolValue());
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
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else {
			return new BoolVal(left.boolValue() || right.boolValue());
		}
	}

	public Value visit(BitshiftLeft node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() << right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() << right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() << right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() << right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() << right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() << right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() << right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() << right.byteValue());
			}
		}
	}

	public Value visit(BitshiftRight node, Object... argv){
		Value left = node.left.accept(this);
		Value right = node.right.accept(this);
		if(left instanceof StrVal || right instanceof StrVal) {
			errorAndExit("Error: Cant mod types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof RealVal || right instanceof RealVal) {
			errorAndExit("Error: Cant and types together of " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else if (left instanceof Unsigned || right instanceof Unsigned) {
			if(left instanceof LongVal || right instanceof LongVal){
				return getUnsignedNumValue(left.longValue() >> right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getUnsignedNumValue(left.intValue() >> right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getUnsignedNumValue(left.shortValue() >> right.shortValue());
			} else {
				return getUnsignedNumValue(left.byteValue() >> right.byteValue());
			}
		} else {
			if(left instanceof LongVal || right instanceof LongVal){
				return getSignedNumValue(left.longValue() >> right.longValue());
			} else if(left instanceof IntVal || right instanceof IntVal){
				return getSignedNumValue(left.intValue() >> right.intValue());
			} else if(left instanceof ShortVal || right instanceof ShortVal){
				return getSignedNumValue(left.shortValue() >> right.shortValue());
			} else {
				return getSignedNumValue(left.byteValue() >> right.byteValue());
			}
		}
	}

	public Value visit(Negation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof RealVal){
			return new RealVal(-right.realValue());
		} else if(right instanceof LongVal){
			return new LongVal(-right.longValue());
		} else if(right instanceof IntVal){
			return new IntVal(-right.intValue());
		} else if(right instanceof ShortVal){
			return new ShortVal((short)-right.shortValue());
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)-right.byteValue());
		} else if(right instanceof BoolVal){
			return new ByteVal((byte)-(right.byteValue()));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(BitwiseNegation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof LongVal){
			return new LongVal(~right.longValue());
		} else if(right instanceof IntVal){
			return new IntVal(~right.intValue());
		} else if(right instanceof ShortVal){
			return new ShortVal((short)~right.shortValue());
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)~right.byteValue());
		} else if(right instanceof BoolVal){
			return new ByteVal((byte)~right.byteValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		}
	}

	public Value visit(LogicalNegation node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof StrVal){
			errorAndExit("Could not negate type " + right.getClass().getSimpleName(), node.getPosition());
			return null;
		} else {
			return new BoolVal(!right.boolValue());
		}
	}

	public Value visit(ReductionAnd node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			return new UnsignedLongVal(right.longValue() == -1 ? 1 : 0);
		} else if(right instanceof LongVal){
			return new LongVal(right.longValue() == -1 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			return new UnsignedIntVal(right.intValue() == -1 ? 1 : 0);
		} else if(right instanceof IntVal){
			return new IntVal(right.intValue() == -1 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			return new UnsignedShortVal((short)(right.shortValue() == -1 ? 1 : 0));
		} else if(right instanceof ShortVal){
			return new ShortVal((short)(right.shortValue() == -1 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			return new UnsignedByteVal((byte)(right.byteValue() == -1 ? 1 : 0));
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)(right.byteValue() == -1 ? 1 : 0));
		} else if(right instanceof BoolVal){
			return new ByteVal(right.byteValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionNand node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			return new UnsignedLongVal(right.longValue() == -1 ? 0 : 1);
		} else if(right instanceof LongVal){
			return new LongVal(right.longValue() == -1 ? 0 : 1);
		} else if(right instanceof UnsignedIntVal){
			return new UnsignedIntVal(right.intValue() == -1 ? 0 : 1);
		} else if(right instanceof IntVal){
			return new IntVal(right.intValue() == -1 ? 0 : 1);
		} else if(right instanceof UnsignedShortVal){
			return new UnsignedShortVal((short)(right.shortValue() == -1 ? 0 : 1));
		} else if(right instanceof ShortVal){
			return new ShortVal((short)(right.shortValue() == -1 ? 0 : 1));
		} else if(right instanceof UnsignedByteVal){
			return new UnsignedByteVal((byte)(right.byteValue() == -1 ? 0 : 1));
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)(right.byteValue() == -1 ? 0 : 1));
		} else if(right instanceof BoolVal){
			return new ByteVal(right.byteValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}



	public Value visit(ReductionOr node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			return new UnsignedLongVal((right.longValue() ^ -1) != -1 ? 1 : 0);
		} else if(right instanceof LongVal){
			return new LongVal((right.longValue() ^ -1) != -1 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			return new UnsignedIntVal((right.intValue() ^ -1) != -1 ? 1 : 0);
		} else if(right instanceof IntVal){
			return new IntVal((right.intValue() ^ -1) != -1 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			return new UnsignedShortVal((short)((right.shortValue() ^ -1) != -1 ? 1 : 0));
		} else if(right instanceof ShortVal){
			return new ShortVal((short)((right.shortValue() ^ -1) != -1 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			return new UnsignedByteVal((byte)((right.byteValue() ^ -1) != -1 ? 1 : 0));
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)((right.byteValue() ^ -1) != -1 ? 1 : 0));
		} else if(right instanceof BoolVal){
			return new ByteVal(right.byteValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionNor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			return new UnsignedLongVal((right.longValue() ^ -1) != -1 ? 0 : 1);
		} else if(right instanceof LongVal){
			return new LongVal((right.longValue() ^ -1) != -1 ? 0 : 1);
		} else if(right instanceof UnsignedIntVal){
			return new UnsignedIntVal((right.intValue() ^ -1) != -1 ? 0 : 1);
		} else if(right instanceof IntVal){
			return new IntVal((right.intValue() ^ -1) != -1 ? 0 : 1);
		} else if(right instanceof UnsignedShortVal){
			return new UnsignedShortVal((short)((right.shortValue() ^ -1) != -1 ? 0 : 1));
		} else if(right instanceof ShortVal){
			return new ShortVal((short)((right.shortValue() ^ -1) != -1 ? 0 : 1));
		} else if(right instanceof UnsignedByteVal){
			return new UnsignedByteVal((byte)((right.byteValue() ^ -1) != -1 ? 0 : 1));
		} else if(right instanceof ByteVal){
			return new ByteVal((byte)((right.byteValue() ^ -1) != -1 ? 0 : 1));
		} else if(right instanceof BoolVal){
			return new ByteVal(right.byteValue());
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}
	

	public Value visit(ReductionXor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedLongVal(num1s % 2);
		} else if(right instanceof LongVal){
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new LongVal(num1s % 2);
		} else if(right instanceof UnsignedIntVal){
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (right.intValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedIntVal(num1s % 2);
		} else if(right instanceof IntVal){
			IntVal rRight = (IntVal)right;
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (right.intValue() >> i == 1 ? 1 : 0);
			}
			return new IntVal(num1s % 2);
		} else if(right instanceof UnsignedShortVal){
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (right.shortValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedShortVal((short)(num1s % 2));
		} else if(right instanceof ShortVal){
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (right.shortValue() >> i == 1 ? 1 : 0);
			}
			return new ShortVal((short)(num1s % 2));
		} else if(right instanceof UnsignedByteVal){
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (right.byteValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedByteVal((byte)(num1s % 2));
		} else if(right instanceof ByteVal){
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (right.byteValue() >> i == 1 ? 1 : 0);
			}
			return new ByteVal((byte)(num1s % 2));
		} else if(right instanceof BoolVal){
			return new ByteVal((byte)(right.boolValue() ? 1 : 0));
		} else {
			errorAndExit("Could not negate type " + right.getClass(), node.getPosition());
			return null;
		}
	}

	public Value visit(ReductionXnor node, Object... argv){
		Value right = node.accept(this);

		if(right instanceof UnsignedLongVal){
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedLongVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof LongVal){
			int num1s = 0;
			for(int i = 0; i < 64; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new LongVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof UnsignedIntVal){
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedIntVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof IntVal){
			int num1s = 0;
			for(int i = 0; i < 32; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new IntVal(num1s % 2 == 0 ? 1 : 0);
		} else if(right instanceof UnsignedShortVal){
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (right.longValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedShortVal((short)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof ShortVal){
			int num1s = 0;
			for(int i = 0; i < 16; i++){
				num1s += (right.shortValue() >> i == 1 ? 1 : 0);
			}
			return new ShortVal((short)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof UnsignedByteVal){
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (right.byteValue() >> i == 1 ? 1 : 0);
			}
			return new UnsignedByteVal((byte)(num1s % 2 == 0 ? 1 : 0));
		} else if(right instanceof ByteVal){
			int num1s = 0;
			for(int i = 0; i < 8; i++){
				num1s += (right.byteValue() >> i == 1 ? 1 : 0);
			}
			return new ByteVal((byte)((num1s % 2) == 0 ? 1 : 0));
		} else if(right instanceof BoolVal){
			return new ByteVal((byte)(right.boolValue() ? 0 : 1));
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
			return GuiRam.getMemory(arg1);
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
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	public Value visit(StringNode string, Object... argv){ return new StrVal(string.getLexeme()); }

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	public Value visit(TernaryOperation expr, Object... argv){
		Value cond = expr.getCondition().accept(this);
		if (cond.boolValue()) {
			return expr.getExecuteIfTrue().accept(this);
		} else {
			return expr.getExecuteIfFalse().accept(this);
		}

	}

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public Value visit(Element vector, Object... argv){
		String ident = vector.getLabel();
		
		Value index = vector.getExpression().accept(this);

		if (environment.variableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);

			if (data.deRefrence() instanceof Vector) {
				Vector vecData = (Vector)data.deRefrence();
				return vecData.getValue(index.intValue());
			} else {
				errorAndExit("Unkown array type for " + ident + " [ Type -> " + data.deRefrence().getClass().getSimpleName() + " ]", vector.getPosition());
				return null;
			}
		} else {
			errorAndExit("Array or Vector " + ident + " not found", vector.getPosition());
			return null;
		}

	}

	public Value visit(Slice vector, Object... argv){
		String ident = vector.getLabel();
		Value startIndex = vector.getExpression1().accept(this);
		Value endIndex = vector.getExpression2().accept(this);

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
}