package edu.depauw.emulator_ide.verilog_compiler.data_structure;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.main.util.OpUtil;

import java.util.ArrayList;

/**The vectorcall class is used to par.E a call to an array cell
 * @author Jacob Bauer
 */

public class Vector<DataValue>{

    private Object[] data;
    private final int index1;
    private final int index2;

    /** The Vector constructor takes an identifier with up to two index to specify the sub array that is desired
     * @param ident name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    
    public Vector(int index1, int index2){
		this.index1 = index1;
		this.index2 = index2;
		data = new Object[getSize()];
    }

    public DataValue getValue(int index){
    	return (index1 <= index2) ? (DataValue)data[index - index1] : (DataValue)data[index1 - index];
    }

    public Vector<DataValue> getSlice(int index1, int index2){
    	Vector<DataValue> vec = new Vector(index1, index2);
    	if(vec.getValue(index2) instanceof CircuitElem && this.getValue(index2) instanceof CircuitElem) {
    		Vector<CircuitElem> elem = (Vector<CircuitElem>)vec;
    		Vector<CircuitElem> This = (Vector<CircuitElem>)this;
    		OpUtil.deepAssign(elem, This);
    		return (Vector<DataValue>)elem;
    	}
    	return vec;
    }

    public int getIndex1(){
    	return index1;
    }

    public int getIndex2(){
    	return index2;
    }

    public int getSize(){
    	return (index1 > index2) ? index1 - index2 + 1 : index2 - index1 + 1;
    }

    public void setValue(int index, DataValue val){
    	data[(index1 <= index2) ? index - index1 : index1 - index] = val;
    }
}
