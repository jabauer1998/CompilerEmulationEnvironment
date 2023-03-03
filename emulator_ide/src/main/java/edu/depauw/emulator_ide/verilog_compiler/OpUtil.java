package edu.depauw.emulator_ide.verilog_compiler;

import java.io.File;
import javax.management.RuntimeErrorException;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.ArrayVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.BoolVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.RealVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.VectorVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry.HalfAdder;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry.RippleCarryAdder;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry.RippleCarrySubtractor;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.RegVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.AndGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NotGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.OrGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.XnorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.XorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.pattern.Pattern;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Input;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Int;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.Reg;

public class OpUtil {
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
	 */
	public static void errorAndExit(String errorParam){ 
		Error error = new Error(errorParam);
		throw new RuntimeErrorException(error);
	}

	public static void errorAndExit(String errorParam, edu.depauw.emulator_ide.common.Position position){
		Error error = new Error(errorParam + position.toString());
		throw new RuntimeErrorException(error);
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

	public static StrVal fetchFunctionName(ModuleItem functionDeclaration){
		if(functionDeclaration instanceof Reg.Scalar.Ident){
			return new StrVal(((Reg.Scalar.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Reg.Vector.Ident) {
			return new StrVal(((Reg.Vector.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Int){
			return new StrVal(((Int)functionDeclaration).toString());
		} else {
			OpUtil.errorAndExit("Unknown Function Type found " + functionDeclaration.toString());
			return null;
		}
	}

    public static Value add(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() + right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new RealVal(left.realValue() + right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.longValue() + right.realValue());
        } else {
            return new LongVal(left.longValue() + right.longValue());
        }
    }

	public static Value createAdder(Value left, Value right){
		return createRippleCarryAdder(left, right);
	}

	public static Value createRippleCarryAdder(Value left, Value right){
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
			OpUtil.errorAndExit("Error Invalid Types for creating an Adder (Left -> " + left.getClass().getName() + " | Right -> " + right.getClass().getName() + ")");
		}

		return OpUtil.errorOccured();
	}

    public static Value minus(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() - right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new RealVal(left.realValue() - right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.longValue() - right.realValue());
        } else {
            return new LongVal(left.longValue() - right.longValue());
        }
    }

	public static Value createSubtractor(Value left, Value right){
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
			OpUtil.errorAndExit("Can't create a subtractor with the types " + right.getClass().getName() + " and " + left.getClass().getName());
			return OpUtil.errorOccured();
		}
	}

    public static Value times(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() * right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new RealVal(left.realValue() * right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() * right.longValue());
        } else {
            return new LongVal(left.longValue() * right.longValue());
        }
    }

    public static Value div(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() / right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new RealVal(left.realValue() / right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new RealVal(left.realValue() / right.longValue());
        } else {
            return new RealVal(left.longValue() / right.longValue());
        }
    }

    public static Value mod(Value left, Value right){
        return new LongVal(left.longValue() % right.longValue());
    }

    public static Value lazyEquality(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() == right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() == right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.longValue() == right.realValue());
        } else {
            return new BoolVal(left.longValue() == right.longValue());
        }
    }

    public static Value strictEquality(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() == right.realValue());
        } else {
            return new BoolVal(left.longValue() == right.longValue());
        }
    }

    public static Value lazyInequality(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() != right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() != right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.longValue() != right.realValue());
        } else {
            return new BoolVal(left.longValue() != right.longValue());
        }
    }

    public static Value strictInequality(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() != right.realValue());
        } else {
            return new BoolVal(left.longValue() != right.longValue());
        }
    }

    public static Value logicalAnd(Value left, Value right){
        return new BoolVal(left.boolValue() && right.boolValue());
    }

    public static Value logicalOr(Value left, Value right){
        return new BoolVal(left.boolValue() || right.boolValue());
    }

    public static Value lessThanOrEqualTo(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() <= right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() <= right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() <= right.longValue());
        } else {
            return new BoolVal(left.longValue() <= right.longValue());
        }
    }

    public static Value lessThan(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() < right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() < right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() < right.longValue());
        } else {
            return new BoolVal(left.longValue() < right.longValue());
        }
    }

    public static Value greaterThanOrEqualTo(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() >= right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() >= right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() >= right.longValue());
        } else {
            return new BoolVal(left.longValue() >= right.longValue());
        }
    }

    public static Value greaterThan(Value left, Value right){
        if (left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() > right.realValue());
        } else if (left.isRealValue() && !right.isRealValue()) {
            return new BoolVal(left.realValue() > right.longValue());
        } else if (!left.isRealValue() && right.isRealValue()) {
            return new BoolVal(left.realValue() > right.longValue());
        } else {
            return new BoolVal(left.longValue() > right.longValue());
        }
    }

    public static Value bitwiseAnd(Value left, Value right){
        return new LongVal(left.longValue() & right.longValue());
    }

	public static Value bitwiseAndCircuit(Value left, Value right){
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
				OpUtil.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return OpUtil.errorOccured();
			}
		}

		return null;
	}

    public static Value bitwiseOr(Value left, Value right){
        return new LongVal(left.longValue() | right.longValue());
    }

	public static Value bitwiseOrCircuit(Value left, Value right){
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
				OpUtil.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return OpUtil.errorOccured();
			}
		}

		return null;
	}

    public static Value exclusiveOr(Value left, Value right){
        return new LongVal(left.longValue() ^ right.longValue());
    }

	public static Value bitwiseXorCircuit(Value left, Value right){
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
				OpUtil.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return OpUtil.errorOccured();
			}
		}

		return null;
	}

    public static Value exclusiveNor(Value left, Value right){
        return new LongVal(~(left.longValue() ^ right .longValue()));
    }

	public static Value bitwiseXnorCircuit(Value left, Value right){
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
				OpUtil.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return OpUtil.errorOccured();
			}
		}

		return null;
	}

    public static Value leftShift(Value left, Value right){
        return new LongVal(left.longValue() << right.longValue());
    }

    public static Value rightShift(Value left, Value right){
        return new LongVal(left.longValue() >> right.longValue());
    }

    public static Value negation(Value right){
        if (right.isRealValue()) {
            return new RealVal(-right.realValue());
        } else {
            return new LongVal(-right.longValue());
        }
    }

	public static Value notGateCircuit(Value right){
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
			OpUtil.errorAndExit("Error invlid type for NotGate");
			return OpUtil.errorOccured();
		}
	}

    public static Value logicalNegation(Value right){
        return new BoolVal(!right.boolValue());
    }

    public static Value bitwiseNegation(Value right){
        return new LongVal(~right.longValue());
    }

	/**
	 * This is used to visit casedz statements in verilog
	 * 
	 * @param assign
	 */

	public static boolean caseBoolean(Value target, Value Val){
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
			OpUtil.errorAndExit("Error Invalid Type for paramater " + Item.getClass().toString());
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
	 */

	public static void deepAssign(VectorVal vec1, int index, Value vec2){
		CircuitElem assignTo = vec1.getValue(index);
		OpUtil.deepAssign(assignTo, vec2);
	}

	public static void deepAssign(ArrayVal<Value> arr1, int index, Value vec2){
		Value arrVal = arr1.ElemAtIndex(index);
		OpUtil.deepAssign((CircuitElem)arrVal, vec2);
	}

	public static void deepAssign(VectorVal vec1, int index1, int index2, Value elem2){
		int start = vec1.getStart();
		int end = vec1.getEnd();
		OpUtil.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			OpUtil.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, CircuitElem elem2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		OpUtil.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			OpUtil.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, int index1, CircuitElem elem2){ 
		WireVal vec1Value = new WireVal();
		vec1Value.addOutput(vec1.getValue(index1));
		vec1Value.assignInput(elem2);
	}

	public static void deepAssign(CircuitElem elemTo, Value vec2){
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
			OpUtil.errorAndExit("Error and exit  : " + elemTo.getClass().getName() + " and " + vec2.getClass().getName());
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
				OpUtil.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
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
				OpUtil.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
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
				OpUtil.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				OpUtil.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
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
				OpUtil.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
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
				OpUtil.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
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
				OpUtil.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				OpUtil.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
			}
		}
	}

	public static void shallowAssign(CircuitElem elem1, VectorVal vec2){
		int index = vec2.getStart();
		OpUtil.shallowAssign(elem1, vec2.getValue(index));
	}

	public static void shallowAssign(VectorVal vec1, CircuitElem elem2){
		int start = vec1.getStart();
		int end = vec1.getEnd();
		OpUtil.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			OpUtil.shallowAssign(vec1.getValue(sIndex1), elem2);
			sIndex1++;
		}
	}

	public static void shallowAssign(CircuitElem elem1, CircuitElem elem2){
		if(elem1.isRegister()){
			RegVal reg1 = (RegVal)elem1;
			reg1.setSignal(elem2.getStateSignal());
		} else {
			OpUtil.errorAndExit("CircuitElement shallow asignment cannoot take place with types " + elem1.getClass().getName() + " and " + elem2.getClass().getName());
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, CircuitElem elem2){
		int start = vec1.getStart();
		int end = vec1.getEnd();
		int startIndex1 = start + 1;
		
		OpUtil.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			OpUtil.shallowAssign(vec1.getValue(sIndex1), false);
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
				boolean signal = (int2 & (1<<over)) > 0;
				OpUtil.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = vec1.getStart();
			int end = vec1.getEnd();
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = (int2 & (1<<i)) > 0;
				OpUtil.shallowAssign(vec1, i, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				OpUtil.shallowAssign(vec1, sIndex1, false);
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
				boolean signal = (int2 & (1<<over)) > 0;
				OpUtil.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = (int2 & (1<<i)) > 0;
				OpUtil.shallowAssign(vec1, sIndex1, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				OpUtil.shallowAssign(vec1, sIndex1, false);
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

		OpUtil.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			OpUtil.shallowAssign(vec1, i, false);
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, boolean bool2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		OpUtil.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			OpUtil.shallowAssign(vec1, i, false);
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
}
