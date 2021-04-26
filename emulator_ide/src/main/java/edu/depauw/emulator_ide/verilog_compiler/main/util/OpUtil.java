package edu.depauw.emulator_ide.verilog_compiler.main.util;

/** The number utils class was designed to keep track of methods to help with the conversion from String to numerical values
 * @author Jacob Bauer
 */

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.*;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.*;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.*;



import java.lang.String;
import java.lang.Number;
import java.lang.Integer;
import java.lang.Long;
import java.lang.StringBuilder;

/** The opUtil class is used for interpreting binary expressions or performing certain type conversions
 * These methods are created in this way so that they can be called during interpretation or from bytecode after compilation
 * This will make code generation a brease as all of the calculations will still be done in Java
 * @author Jacob Bauer
 */

public class OpUtil {

    /** I created two types of assignments for verilog and the difference breaks down to how registers are assigned to one another
     * In a shallow assignment the value is coppied over from one register to another a register can hold a True or a False value representing whether the output from that register is high or low
     * So the assignment works by assigning the value from one register to the value of the current register. This is different then actually changing the literal register object in a deep assignment.
     * After a shallow assignent if a signal change occours in the register that was assigned it will not affect the register that the value was assigned to. In other words it makes a copy.
     */
    public static void shallowAssign(Vector<CircuitElem> vec1, Vector<CircuitELem> vec2){
	if(vec1.getSize() == vec2.getSize()){
	    int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
		elem1.setSignal(signal);
		sIndex1++;
		sIndex2++;
	    }
	    
	} else if(vec1.getSize() < vec2.getSize()){
	    int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2 + (vec2.getSize() - vec1.getSize());
	    
	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
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
	    
	    while(sIndex2 <= end2){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
		elem1.setSignal(signal);
		sIndex1++;
		sIndex2++;
	    }

	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		elem1.setSignal(false);
	    }
	}
    }

    /** I created two types of assignments for verilog and the difference breaks down to how registers are assigned to one another
     * In a shallow assignment the value is coppied over from one register to another a register can hold a True or a False value representing whether the output from that register is high or low
     * So the assignment works by assigning the value from one register to the value of the current register. This is different then actually changing the literal register object in a deep assignment.
     * After a shallow assignent if a signal change occours in the register that was assigned it will not affect the register that the value was assigned to. In other words it makes a copy.
     */
    public static void shallowAssign(Vector<CircuitElem> vec1, int index, Vector<CircuitELem> vec2){
	int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	vec1.getValue(index).setSignal(vec2.getValue(start2).getSignal());
    }

    /** I created two types of assignments for verilog and the difference breaks down to how registers are assigned to one another
     * In a shallow assignment the value is coppied over from one register to another a register can hold a True or a False value representing whether the output from that register is high or low
     * So the assignment works by assigning the value from one register to the value of the current register. This is different then actually changing the literal register object in a deep assignment.
     * After a shallow assignent if a signal change occours in the register that was assigned it will not affect the register that the value was assigned to. In other words it makes a copy.
     */
    public static void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2,  Vector<CircuitELem> vec2){
	
	int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;
	if(sliceSize == vec2.getSize()){
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
		elem1.setSignal(signal);
		sIndex1++;
		sIndex2++;
	    }
	} else if(sliceSize < vec2.getSize()){
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2 + (vec2.getSize() - sliceSize);
	    
	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
		elem1.setSignal(signal);
		sIndex1++;
		sIndex2++;
	    }
	} else {
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex2 <= end2){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		boolean signal = vec2.getValue(sIndex2).getSignal();
		elem1.setSignal(signal);
		sIndex1++;
		sIndex2++;
	    }

	    while(sIndex1 <= end1){
		Register elem1 = (Register)vec1.getValue(sIndex1);
		elem1.setSignal(false);
	    }
	}
    }

    /** I created two types of assignments for verilog and the difference breaks down to how registers are assigned to one another
     * In a shallow assignment the value is coppied over from one register to another a register can hold a True or a False value representing whether the output from that register is high or low
     * So the assignment works by assigning the value from one register to the value of the current register. This is different then actually changing the literal register object in a deep assignment.
     * After a shallow assignent if a signal change occours in the register that was assigned it will not affect the register that the value was assigned to. In other words it makes a copy.
     */
    public static void deepAssign(Vector<CircuitElem> vec1, int index, Vector<CircuitELem> vec2){
	int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	vec1.setValue(index, vec2.getValue(start2));
    }

    /**
     * In a deep assignment the value is assigned by reference. So the assignment works by literally replacing a register that is in their with another register. 
     * This is usefull for a continuous assignment in verilog. As apposed to oberving the expression on the right hand side with a loop and coppying changes to the left hand side I am actually making the objects that are on the right hand side the things that are on the left hand side so changes come across in both variables.
     * @author Jacob Bauer
     */
    public static void deepAssign(Vector<CircuitElem> vec1, Vector<CircuitELem> vec2){
	if(vec1.getSize() == vec2.getSize()){
	    int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
		sIndex1++;
		sIndex2++;
	    }
	    
	} else if(vec1.getSize() < vec2.getSize()){
	    int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2 + (vec2.getSize() - vec1.getSize());
	    
	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
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
	    
	    while(sIndex2 <= end2){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
		sIndex1++;
		sIndex2++;
	    }

	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, new Register(false));
	    }
	}
    }


    public static void deepAssign(Vector<CircuitElem> vec1, int index1, int index2, Vector<CircuitELem> vec2){
	int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;
	if(sliceSize == vec2.getSize()){
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
		sIndex1++;
		sIndex2++;
	    }
	    
	} else if(sliceSize < vec2.getSize()){
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2 + (vec2.getSize() - sliceSize);
	    
	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
		sIndex1++;
		sIndex2++;
	    }
	} else {
	    int start1 = (index1 < index2) ? index1 : index2;
	    int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	    int end1 =  (index1 > index2) ? index1 : index2;
	    int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

	    int sIndex1 = start1;
	    int sIndex2 = start2;
	    
	    while(sIndex2 <= end2){
		vec1.setValue(sIndex1, vec2.getValue(sIndex2));
		sIndex1++;
		sIndex2++;
	    }

	    while(sIndex1 <= end1){
		vec1.setValue(sIndex1, new Register(false));
	    }
	}
    }
    
    public void shallowAssign(CircuitElem elem1, Vector<CircuitElem> vec2){
	int index = (vec2.getIndex1() <= vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
	((Register)elem1).setSignal(vec2.getValue(index));
    }

    public void shallowAssign(Vector<CircuitElem> vec1, CircuitElem elem2){
	int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	((Register)vec1.getValue(start)).setSignal(elem2.getSignal());
	int sIndex1 = start + 1;
	while(sIndex1 <= end){
	    ((Register)vec1.getValue(sIndex1)).setSignal(false);
	}
	
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, CircuitElem elem2){
	int start = (index1 < index2) ? index1 : index2;
	int end = (index1 > index2) ? index1 : index2;
	((Register)vec1.getValue(start)).setSignal(elem2.getSignal());
	int sIndex1 = start + 1;
	while(sIndex1 <= end){
	    ((Register)vec1.getValue(sIndex1)).setSignal(false);
	}
	
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, CircuitElem elem2){
	((Register)vec1.getValue(index1)).setSignal(elem2.getSignal());
    }

    public void deepAssign(Vector<CircuitElem> vec1, int index1, int index2, CircuitElem elem2){
	int start = (index1 < index2) ? index1 : index2;
	int end = (index1 > index2) ? index1 : index2;
	((Register)vec1).setValue(start, elem2);
	int sIndex1 = start + 1;
	while(sIndex1 <= end){
	    ((Register)vec1).setValue(sIndex1, new Register(false));
	}
    }

    public void deepAssign(Vector<CircuitElem> vec1, CircuitElem elem2){
	int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	((Register)vec1).setValue(start, elem2);
	int sIndex1 = start + 1;
	while(sIndex1 <= end){
	    ((Register)vec1).setValue(sIndex1, new Register(false));
	}
    }

    public void deepAssign(Vector<CircuitElem> vec1, int index1, CircuitElem elem2){
	((Register)vec1).setValue(index1, elem2);
    }

    public void shallowAssign(Vector<CircuitElem> vec1, long int2){
	if(vec1.getSize() <= 64){
	    int over = 0;
	    int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    for(int i = start; i <= end; i++){
		bool signal = (int2 & (1 << over)) > 0;
		vecl.getValue(i).setSignal(signal);
		over++;
	    }
	} else {
	    int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	    int sIndex1 = start;
	    for(int i = 0; i < 64; i++){
		bool signal = (int2 & (1 << i)) > 0;
		vecl.getValue(sIndex1).setSignal(signal);
		sIndex1++;
	    }

	    while(sIndex1 <= end){
		vecl.getValue(sIndex1).setSignal(false);
	    }
	}
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, long int2){
	int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;
	if(sliceSize <= 64){
	    int start = (index1 < index2) ? index1: index2;
	    int end = (index1 > index2) ? index1 : index2;
	    int over = 0;
	    for(int i = start; i <= end; i++){
		bool signal = (int2 & (1 << over)) > 0;
		vecl.getValue(i).setSignal(signal);
		over++;
	    }
	} else {
	    int start = (index1 < index2) ? index1: index2;
	    int end = (index1 > index2) ? index1 : index2;
	    int sIndex1 = start;
	    for(int i = 0; i < 64; i++){
		bool signal = (int2 & (1 << i)) > 0;
		vecl.getValue(sIndex1).setSignal(signal);
		sIndex1++;
	    }

	    while(sIndex1 <= end){
		vecl.getValue(sIndex1).setSignal(false);
	    }
	}
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, long int2){
	vecl.getValue(index1).setSignal(int2 & 1 > 0);
    }

    public void shallowAssign(CircuitElem elem1, long int2){
	elem1.setSignal(int2 & 1 > 0);
    }

    public void shallowAssign(Vector<CircuitElem> vec1, boolean bool2){
	int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	vec1.getValue(vec1.getIndex(start)).setSignal(bool2);
	int sIndex1 = start + 1;
	for(int i = sIndex1; i <= end; i++){
	    vec1.getValue(i).setSignal(false);
	}
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, int index2, boolean bool2){
	int start = (index1 < index2) ? index1: index2;
	int end = (index1 > index2) ? index1 : index2;
	vec1.getValue(vec1.getIndex(start)).setSignal(bool2);
	int sIndex1 = start + 1;
	for(int i = sIndex1; i <= end; i++){
	    vec1.getValue(i).setSignal(false);
	}
    }

    public void shallowAssign(Vector<CircuitElem> vec1, int index1, boolean bool2){
	vec1.getValue(vec1.getIndex(index1)).setSignal(bool2);
    }

    public void shallowAssign(CircuitElem elem1, boolean bool2){
	elem1.setSignal(bool2);
    }

    public void shallowAssign(CircuitElem elem1, CircuitElem elem2){
	elem1.setSignal(elem2.getSignal());
    }

    public long toLong(CircuitElem elem){
	return elem.getSignal() ? 1 : 0;
    }

    public long toLong(Vector<CircuitElem> vec1){
	int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
	int sIndex1 = start + 1;
	int i = 0;
	long result = 0;
	while(i < 64 && sIndex1 <= end1){
	    result |= toLong(vec1.getValue(sIndex1)) << i;
	    i++;
	    sIndex1++;
	}
    }
}
