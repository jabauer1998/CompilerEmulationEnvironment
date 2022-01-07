package edu.depauw.emulator_ide.verilog_compiler.passes.util;


/**
 * The number utils class was designed to keep track of methods to help with the
 * conversion from String to numerical values
 * 
 * @author Jacob Bauer
 */

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.*;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.web.*;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.*;

import java.lang.String;
import java.lang.Number;
import java.lang.Integer;
import java.lang.Long;
import java.lang.StringBuilder;

/**
 * The opUtil class is used for interpreting binary expressions or performing certain
 * type conversions These methods are created in this way so that they can be called
 * during interpretation or from bytecode after compilation This will make code
 * generation a brease as all of the calculations will still be done in Java
 * 
 * @author Jacob Bauer
 */

public class OpUtil {

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
	public static void shallowAssign(Vector<CircuitElem> vec1, Vector<CircuitElem> vec2){

		if (vec1.getSize() == vec2.getSize()) {
			int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Register elem1 = (Register)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else if (vec1.getSize() < vec2.getSize()) {
			int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - vec1.getSize());

			while(sIndex1 <= end1) {
				Register elem1 = (Register)vec1.getValue(sIndex1);
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
				Register elem1 = (Register)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Register elem1 = (Register)vec1.getValue(sIndex1);
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
	public static void shallowAssign(Vector<CircuitElem> vec1, int index, Vector<CircuitElem> vec2){
		int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
		((Register)vec1.getValue(index)).setSignal(vec2.getValue(start2).getStateSignal());
	}

	public static void shallowAssign(Vector<CircuitElem> vec1, String str2){

		if (vec1.getSize() == str2.length()) {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex2 = start2;

			if (start1 <= end1) {

				for (int i = start1; i <= end1; i++) {
					Register elem1 = (Register)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					Register elem1 = (Register)vec1.getValue(i);
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
					Register elem1 = (Register)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					Register elem1 = (Register)vec1.getValue(i);
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
				Register elem1 = (Register)vec1.getValue(sIndex1);
				boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
				elem1.setSignal(signal);
				sIndex1 += incr;
				sIndex2++;
			}

			if (start1 <= end1) {

				for (int i = sIndex1; i <= end1; i++) {
					Register elem1 = (Register)vec1.getValue(i);
					elem1.setSignal(false);
				}

			} else {

				for (int i = sIndex1; i >= end1; i--) {
					Register elem1 = (Register)vec1.getValue(i);
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
	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, Vector<CircuitElem> vec2){

		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Register elem1 = (Register)vec1.getValue(sIndex1);
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
				Register elem1 = (Register)vec1.getValue(sIndex1);
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
				Register elem1 = (Register)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Register elem1 = (Register)vec1.getValue(sIndex1);
				elem1.setSignal(false);
				sIndex1++;
			}

		}

	}

	/**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the value is copied
	 * over from one register to another a register can hold a True or a False value
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the value from one register to the value of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignment if a signal change occurs in the register that
	 * was assigned it will not affect the register that the value was assigned to. In other
	 * words it makes a copy.
	 */
	public static void deepAssign(Vector<CircuitElem> vec1, int index, Vector<CircuitElem> vec2){
		int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
		vec1.setValue(index, vec2.getValue(start2));
	}

	/**
	 * In a deep assignment the value is assigned by reference. So the assignment works by
	 * literally replacing a register that is in their with another register. This is useful
	 * for a continuous assignment in verilog. As opposed to observing the expression on the
	 * right hand side with a loop and coppying changes to the left hand side I am actually
	 * making the objects that are on the right hand side the things that are on the left
	 * hand side so changes come across in both variables.
	 * 
	 * @author Jacob Bauer
	 */
	public static void deepAssign(Vector<CircuitElem> vec, Vector<CircuitElem> vector){

		if (vec.getSize() == vector.getSize()) {
			int start1 = (vec.getIndex1() < vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int start2 = (vector.getIndex1() < vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();
			int end1 = (vec.getIndex1() > vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int end2 = (vector.getIndex1() > vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				vec.setValue(sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else if (vec.getSize() < vector.getSize()) {
			int start1 = (vec.getIndex1() < vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int start2 = (vector.getIndex1() < vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();
			int end1 = (vec.getIndex1() > vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int end2 = (vector.getIndex1() > vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vector.getSize() - vec.getSize());

			while(sIndex1 <= end1) {
				vec.setValue(sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (vec.getIndex1() < vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int start2 = (vector.getIndex1() < vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();
			int end1 = (vec.getIndex1() > vec.getIndex2()) ? vec.getIndex1() : vec.getIndex2();
			int end2 = (vector.getIndex1() > vector.getIndex2()) ? vector.getIndex1() : vector.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				vec.setValue(sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				vec.setValue(sIndex1, new Register(false));
				sIndex1++;
			}

		}

	}

	public static void deepAssign(Vector<CircuitElem> vec1, int index1, int index2, Vector<CircuitElem> vec2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				vec1.setValue(sIndex1, vec2.getValue(sIndex2));
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
				vec1.setValue(sIndex1, vec2.getValue(sIndex2));
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
				vec1.setValue(sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				vec1.setValue(sIndex1, new Register(false));
				sIndex1++;
			}

		}

	}

	public static void shallowAssign(CircuitElem elem1, Vector<CircuitElem> vec2){
		int index = (vec2.getIndex1() <= vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
		((Register)elem1).setSignal(vec2.getValue(index).getStateSignal());
	}

	public static void shallowAssign(Vector<CircuitElem> vec1, CircuitElem elem2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		((Register)vec1.getValue(start)).setSignal(elem2.getStateSignal());
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			((Register)vec1.getValue(sIndex1)).setSignal(false);
			sIndex1++;
		}

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, CircuitElem elem2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		((Register)vec1.getValue(start)).setSignal(elem2.getStateSignal());
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			((Register)vec1.getValue(sIndex1)).setSignal(false);
			sIndex1++;
		}

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, CircuitElem elem2){
		((Register)vec1.getValue(index1)).setSignal(elem2.getStateSignal());
	}

	public static void deepAssign(Vector<CircuitElem> vec1, int index1, int index2, CircuitElem elem2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		vec1.setValue(start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			vec1.setValue(sIndex1, new Register(false));
			sIndex1++;
		}

	}

	public static void deepAssign(Vector<CircuitElem> vec1, CircuitElem elem2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		vec1.setValue(start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			vec1.setValue(sIndex1, new Register(false));
			sIndex1++;
		}

	}

	public static void deepAssign(Vector<CircuitElem> vec1, int index1, CircuitElem elem2){ vec1.setValue(index1, elem2); }

	public static void shallowAssign(Vector<CircuitElem> vec1, long int2){

		if (vec1.getSize() <= 64) {
			int over = 0;
			int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();

			for (int i = start; i <= end; i++) {
				boolean signal = (int2 & (1<<over)) > 0;
				((Register)vec1.getValue(i)).setSignal(signal);
				over++;
			}

		} else {
			int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = (int2 & (1<<i)) > 0;
				((Register)vec1.getValue(sIndex1)).setSignal(signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				((Register)vec1.getValue(sIndex1)).setSignal(false);
				sIndex1++;
			}

		}

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, long int2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize <= 64) {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int over = 0;

			for (int i = start; i <= end; i++) {
				boolean signal = (int2 & (1<<over)) > 0;
				((Register)vec1.getValue(i)).setSignal(signal);
				over++;
			}

		} else {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = (int2 & (1<<i)) > 0;
				((Register)vec1.getValue(sIndex1)).setSignal(signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				((Register)vec1.getValue(sIndex1)).setSignal(false);
				sIndex1++;
			}

		}

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, long int2){
		((Register)vec1.getValue(index1)).setSignal((int2 & 1) > 0);
	}

	public static void shallowAssign(CircuitElem elem1, long int2){ ((Register)elem1).setSignal((int2 & 1) > 0); }

	public static void shallowAssign(Vector<CircuitElem> vec1, boolean bool2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		((Register)vec1.getValue(start)).setSignal(bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { ((Register)vec1.getValue(i)).setSignal(false); }

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, boolean bool2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		((Register)vec1.getValue(start)).setSignal(bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { ((Register)vec1.getValue(i)).setSignal(false); }

	}

	public static void shallowAssign(Vector<CircuitElem> vec1, int index1, boolean bool2){
		((Register)vec1.getValue(index1)).setSignal(bool2);
	}

	public static void shallowAssign(CircuitElem elem1, boolean bool2){ ((Register)elem1).setSignal(bool2); }

	public static void shallowAssign(CircuitElem elem1, CircuitElem elem2){ ((Register)elem1).setSignal(elem2.getStateSignal()); }

	public static long toLong(CircuitElem elem){ return elem.getStateSignal() ? 1 : 0; }

	public static long toLong(Vector<CircuitElem> vec1){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();

		long result = 0;

		if (start <= end) {
			int over = 0;

			for (int i = start; i <= end && over < 64; i++, over++) { result |= (toLong(vec1.getValue(i)) & 1)<<over; }

		} else {
			int over = 0;

			for (int i = end; i <= start && over < 64; i++, over++) { result |= (toLong(vec1.getValue(i)) & 1)<<over; }

		}

		return result;
	}
}
