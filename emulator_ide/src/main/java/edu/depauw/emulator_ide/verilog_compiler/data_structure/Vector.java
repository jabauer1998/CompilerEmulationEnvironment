package edu.depauw.emulator_ide.verilog_compiler.data_structure;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

import java.util.Arrays;

/**The vectorcall class is used to par.E a call to an array cell
 * @author Jacob Bauer
 */

public class Vector<DataValue>{

    private DataValue[] data;
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
	int size = (index1 > index2) ? index1 - index2 + 1 : index2 - index1 + 1;
	//data = new DataValue[size];
    }

    public DataValue getValue(int index){
	return (index1 <= index2) ? data[index - index1] : data[index1 - index];
    }

    public Vector<DataValue> getSlice(int index1, int index2){
	Vector<DataValue> vec = new Vector(index1, index2);
	if(index1 <= index2){
	    //vec.setData(Arrays.copyOfRange(data, index1 - this.index1, index2 - this.index1));
	} else {
	    //vec.setData(Arrays.copyOfRange(data, this.index1 - index1, this.index1 - index2));
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

    private void setData(DataValue[] data){
	this.data = data;
    }
}
