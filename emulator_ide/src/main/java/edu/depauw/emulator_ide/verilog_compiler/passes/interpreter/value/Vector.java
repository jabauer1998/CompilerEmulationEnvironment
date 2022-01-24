package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;


import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.nodes.Register;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Vector implements Value{

	private List<CircuitElem>  data;
	private final int index1;
	private final int index2;

	/**
	 * The Vector constructor takes an identifier with up to two index to specify the sub
	 * array that is desired
	 * 
	 * @param ident  name of the array
	 * @param index1 min index of the array
	 * @param index2 max index of the array
	 */

	public Vector(int index1, int index2) {
		this.index1 = index1;
		this.index2 = index2;
		int size = this.getSize();
		data = new ArrayList<>(size);
	}

	public CircuitElem getValue(int index){
		return (index1 <= index2) ? data.get(index - index1) : data.get(index1 - index);
	}

	public Vector getDeepSlice(int index1, int index2){
		Vector vec = new Vector(index1, index2);

		if (index1 <= index2) {

			for (int i = index1; i <= index2; i++) { vec.setValue(i, this.getValue(i)); }

		} else {

			for (int i = index2; i <= index1; i++) { vec.setValue(i, this.getValue(i)); }

		}

		return vec;
	}

	public Vector getShallowSlice(int index1, int index2){
		Vector vec = new Vector(index1, index2);

		if (index1 <= index2) {

			for (int i = index1; i <= index2; i++) {
				Register r = new Register(false);
				CircuitElem elem = (CircuitElem)this.getValue(i);
				r.setSignal(elem.getStateSignal());
				vec.setValue(i, r);
			}

		} else {

			for (int i = index2; i <= index1; i++) {
				Register r = new Register(false);
				CircuitElem elem = (CircuitElem)this.getValue(i);
				r.setSignal(elem.getStateSignal());
				vec.setValue(i, r);
			}

		}

		return vec;
	}

	public int getIndex1(){ return index1; }

	public int getIndex2(){ return index2; }

	public int getSize(){ return (index1 > index2) ? index1 - index2 + 1 : index2 - index1 + 1; }

	public void setValue(int index, CircuitElem val){
		int realIndex = (index1 <= index2) ? index - index1 : index1 - index;
		data.set(realIndex, val); 
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();

		if (index1 <= index2) {

			for (int i = index1; i <= index2; i++) { sb.append(getValue(i).toString()); }

		} else {

			for (int i = index1; i >= index2; i--) { sb.append(getValue(i).toString()); }

		}

		return sb.toString();
	}
}
