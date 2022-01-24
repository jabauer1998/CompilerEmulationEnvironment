package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.Concatenation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.TernaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.operation.unary.UnaryOperation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ContinuousAssignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.AllwaysProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.assignment.Assignment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.ForeverStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.RepeatStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.branching.WhileStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.SystemTaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.task.TaskStatement;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.pattern.Pattern;
import edu.depauw.emulator_ide.verilog_compiler.passes.util.*;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.*;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.gui.GuiMemory;
import edu.depauw.emulator_ide.gui.GuiRegister;
import edu.depauw.emulator_ide.gui.GuiStatusBit;
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

public class DeepVisitor implements ExpressionVisitor<Value> {

	private final ErrorLog                               errorLog;
	private Environment environment;

	public DeepVisitor(ErrorLog errorLog, Environment environment){
		this.errorLog = errorLog;
		this.environment = environment;
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

	public Object visit(BinaryOperation op, Object... argv){
		Object left = op.getLeft().accept(this);
		Object right = op.getRight().accept(this);

		switch(op.getOp()){
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left + longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) + (double)right;
				} else {
					return longValue(left) + longValue(right);
				}
			case MINUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left - (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left - longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) - (double)right;
				} else {
					return longValue(left) - longValue(right);
				}
			case TIMES:
				if (left instanceof Double && right instanceof Double) {
					return (double)left*(double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left*longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left)*(double)right;
				} else {
					return longValue(left)*longValue(right);
				}
			case DIV:
				if (left instanceof Double && right instanceof Double) {
					return (double)left/(double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left/longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left)/(double)right;
				} else {
					return longValue(left)/longValue(right);
				}
			case MOD:
				return longValue(left)%longValue(right);
			case EQ2:
				if (left instanceof Double && right instanceof Double) {
					return (double)left == (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left == longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) == (double)right;
				} else {
					return longValue(left) == longValue(right);
				}
			case EQ3:
				if (right instanceof Double && left instanceof Double) {
					return (Double)left == (Double)right;
				} else {
					return longValue(left) == longValue(right);
				}
			case NE1:
				if (left instanceof Double && right instanceof Double) {
					return (double)left != (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left != longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) != (double)right;
				} else {
					return longValue(left) != longValue(right);
				}
			case NE2:
				if (right instanceof Double && left instanceof Double) {
					return (Double)left != (Double)right;
				} else {
					return longValue(left) != longValue(right);
				}
			case LAND:
				return boolValue(left) && boolValue(right);
			case LOR:
				return boolValue(left) || boolValue(right);
			case LE:
				if (left instanceof Double && right instanceof Double) {
					return (double)left <= (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left <= longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) <= (double)right;
				} else {
					return longValue(left) <= longValue(right);
				}
			case LT:
				if (left instanceof Double && right instanceof Double) {
					return (double)left < (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left < longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) < (double)right;
				} else {
					return longValue(left) < longValue(right);
				}
			case GE:
				if (left instanceof Double && right instanceof Double) {
					return (double)left >= (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left >= longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) >= (double)right;
				} else {
					return longValue(left) >= longValue(right);
				}
			case GT:
				if (left instanceof Double && right instanceof Double) {
					return (double)left > (double)right;
				} else if (left instanceof Double && !(right instanceof Double)) {
					return (double)left > longValue(right);
				} else if (!(left instanceof Double) && right instanceof Double) {
					return longValue(left) > (double)right;
				} else {
					return longValue(left) > longValue(right);
				}
			case BAND:
				return longValue(left) & longValue(right);
			case BOR:
				return longValue(left) | longValue(right);
			case BXOR:
				return longValue(left) ^ longValue(right);
			case BXNOR:
				return ~(longValue(left) ^ longValue(right));
			case LSHIFT:
				return longValue(left)<<longValue(right);
			case RSHIFT:
				return longValue(left)>>longValue(right);
			default:
				errorLog.addItem(new ErrorItem("Invalid operation in verilog of type " + left.getClass() + " "
					+ op.getOp() + " " + right.getClass(), op.getPosition()));
				return null;
		}

	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Object visit(UnaryOperation op, Object... argv){
		Object right = op.getRight().accept(this);

		switch(op.getOp()){
			case PLUS:
				return right;
			case MINUS:
				if (right instanceof Double) {
					return -(double)right;
				} else {
					return -longValue(right);
				}
			case LNEG:
				return !boolValue(right);
			case BNEG:
				return ~longValue(right);
			default:
				errorLog.addItem(
					new ErrorItem("Unknown unary operation in verilog of type " + op.getOp() + " " + right.getClass(),
						op.getPosition()));
				return null;
		}

	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	public Object visit(Concatenation concat, Object... argv){
		int size = 0;

		for (int expr = 0; expr < concat.numExpressions(); expr++) {
			Object obj = concat.getExpression(expr).accept(this);

			if (obj instanceof Vector) {
				size += ((Vector<CircuitElem>)obj).getSize();
			} else {
				size++; // it is a CircuitElem
			}

		}

		Vector<CircuitElem> newVec = new Vector(size - 1, 0);
		int total = size - 1;

		for (int expr = 0; expr < concat.numExpressions(); expr++) {
			Object obj = concat.getExpression(expr).accept(this);

			if (obj instanceof Vector) {
				Vector<CircuitElem> vec = (Vector<CircuitElem>)obj;

				if (vec.getIndex1() <= vec.getIndex2()) {

					for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
						newVec.setValue(total, new Register(vec.getValue(v).getStateSignal()));
					}

				} else {

					for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
						newVec.setValue(total, new Register(vec.getValue(v).getStateSignal()));
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

	public Object visit(ConstantExpression expr, Object... argv){ return expr.getExpression().accept(this); }

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	public Object visit(EmptyExpression expr, Object... argv){ return null; }

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	boolean inFunctionReturn = false;

	public Object visit(FunctionCall call, Object... argv){
		Identifier tname = call.getFunctionName();

		if (funcEnv.entryExists(tname.getLexeme())) {
			// Collect symbol table data from the function
			InterpreterFunctionData funcData = funcEnv.getEntry(tname.getLexeme());
			FunctionDeclaration decl = (FunctionDeclaration)funcData.getFuncDeclaration();

			funcData.storeParameterList();// store the old parameter list
			varEnv.addScope();

			setcallStackName(tname.getLexeme());
			setTopExit(false);
			decl.getFunctionName().accept(this); // declare the return variable for the function

			InterpreterVariableData returnData = varEnv.getEntry(tname.getLexeme()); // get return object

			inFunctionParam = true; // Set input declarations as function params

			for (int i = 0; i < decl.numDeclarations(); i++) { decl.getDeclaration(i).accept(this); }

			inFunctionParam = false; // Params are declared if any

			if (call.numExpressions() == funcData.numParameters()) {

				// Assign parameter values
				for (int i = 0; i < call.numExpressions(); i++) {
					InterpreterVariableData varData = funcData.getParameter(i);
					Object exp = call.getExpression(i).accept(this);
					varData.setObject(exp);
				}

			} else {
				errorAndExit("Argument amount mismatch " + tname.getLexeme() + " [Expected -> " + funcData.numParameters()
					+ " | Got -> " + call.numExpressions() + " ]", tname.getPosition());
				return null;
			}

			inFunctionReturn = true;
			decl.getStatement().accept(this);
			inFunctionReturn = false;
			removeTopExit();
			varEnv.removeScope();
			removecallStackName();
			funcData.restoreParameterList();
			return returnData.getObject();
		} else {
			errorAndExit("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition());
			return null;
		}

	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public Object visit(SystemFunctionCall call, Object... argv){
		Identifier functionName = call.getSystemFunctionName();

		if (functionName.getLexeme().equals("fopen")) {
			String fname = (String)call.getExpression(0).accept(this);
			String basePath = new File("").getAbsolutePath();
			String access = (String)call.getExpression(1).accept(this);

			if (fname.equals("default")) {
				return new Scanner(Main.getByteInputStream());
			} else {
				File filename = new File(basePath + '/' + fname);

				if (access.equals("r")) {
					filename.setReadOnly();

					Scanner ref = null;

					try {
						ref = new Scanner(filename);
					} catch (FileNotFoundException exp) {
						exp.printStackTrace();
						System.exit(1);
					}

					return ref;
				} else if (access.equals("w")) {
					filename.setWritable(true, false);

					FileWriter ref = null;

					try {
						ref = new FileWriter(filename);
					} catch (IOException exp) {
						exp.printStackTrace();
						System.exit(1);
					}

					return ref;
				} else {
					errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.getPosition());
				}

				return null;
			}

		} else if (functionName.getLexeme().equals("feof")) {
			Scanner fReader = (Scanner)call.getExpression(0).accept(this);
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

	public Object visit(Identifier ident, Object... argv){

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
