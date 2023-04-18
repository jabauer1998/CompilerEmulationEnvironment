package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value;


import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class VectorVal implements Value{
	private List<CircuitElem> data;
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

	public VectorVal(int index1, int index2) {
		this.index1 = index1;
		this.index2 = index2;
		int size = this.getSize();
		this.data = new ArrayList<>();
		for(int i = 0; i < size; i++){
			this.data.add(new RegVal(false));
		}
	}

	public CircuitElem getValue(int index){
		return (index1 <= index2) ? data.get(index - index1) : data.get(index1 - index);
	}

	public int getStart(){
		return (index1 < index2) ? index1 : index2;
	}

	public int getEnd(){
		return (index1 > index2) ? index1 : index2;
	}

	public VectorVal getDeepSlice(int index1, int index2){
		VectorVal vec = new VectorVal(index1, index2);

		if (index1 <= index2) {

			for (int i = index1; i <= index2; i++) { vec.setValue(i, this.getValue(i)); }

		} else {

			for (int i = index2; i <= index1; i++) { vec.setValue(i, this.getValue(i)); }

		}

		return vec;
	}

	public VectorVal getShallowSlice(int index1, int index2){
		VectorVal vec = new VectorVal(index1, index2);

		if (index1 <= index2) {

			for (int i = index1; i <= index2; i++) {
				RegVal r = new RegVal(false);
				CircuitElem elem = (CircuitElem)this.getValue(i);
				r.setSignal(elem.getStateSignal());
				vec.setValue(i, r);
			}

		} else {

			for (int i = index2; i <= index1; i++) {
				RegVal r = new RegVal(false);
				CircuitElem elem = (CircuitElem)this.getValue(i);
				r.setSignal(elem.getStateSignal());
				vec.setValue(i, r);
			}

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

	public double realValue(){
		return (double)longValue();
	}

    public long longValue(){
		if (index1 <= index2) {
			long result = 0;
			for (int i = index1; i <= index2 && (i - index1) < 64; i++) {
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			}
			return result;
		} else {
			long result = 0;
			for (int i = index1; i >= index2 && (index1 - i) < 64; i--) { 
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			 }
			 return result;
		}
	}

    public int intValue(){
		if (index1 <= index2) {
			int result = 0;
			for (int i = index1; i <= index2 && (i - index1) < 32; i++) {
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			}
			return result;
		} else {
			int result = 0;
			for (int i = index1; i >= index2 && (index1 - i) < 32; i--) { 
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			 }
			 return result;
		}
	}

    public short shortValue(){
		if (index1 <= index2) {
			short result = 0;
			for (int i = index1; i <= index2 && (i - index1) < 16; i++) {
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			}
			return result;
		} else {
			short result = 0;
			for (int i = index1; i >= index2 && (index1 - i) < 16; i--) { 
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			 }
			 return result;
		}
	}

    public byte byteValue(){
		if (index1 <= index2) {
			byte result = 0;
			for (int i = index1; i <= index2 && (i - index1) < 8; i++) {
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			}
			return result;
		} else {
			byte result = 0;
			for (int i = index1; i >= index2 && (index1 - i) < 8; i--) { 
				result <<= 1;
				byte tf = getValue(i).byteValue();
				result |= tf;
			 }
			 return result;
		}
	}

    public boolean boolValue(){
		if (index1 <= index2) {
			for (int i = index1; i <= index2; i++) {
				if(getValue(i).boolValue()){
					return true;
				}
			}
			return false;
		} else {
			for (int i = index1; i >= index2; i--) { 
				if(getValue(i).boolValue()){
					return true;
				}
			 }
			 return false;
		}
	}

	@Override
	public boolean isBoolValue(){ // 
		return false; 
	}

	@Override
	public boolean isShortValue(){ // 
		return false; 
	}

	@Override
	public boolean isUnsignedShortValue(){ // 
		return false; 
	}

	@Override
	public boolean isByteValue(){ // 
		return false; 
	}

	@Override
	public boolean isUnsignedByteValue(){ // 
		return false; 
	}

	@Override
	public boolean isIntValue(){ // 
		return false; 
	}

	@Override
	public boolean isUnsignedIntValue(){ // 
		return false; 
	}

	@Override
	public boolean isLongValue(){ // 
		return false; 
	}

	@Override
	public boolean isUnsignedLongValue(){ // 
		return false; 
	}

	@Override
	public boolean isRealValue(){ // 
		return false; 
	}

	@Override
	public boolean isStringValue(){ // 
		return false;
	}

	@Override
	public boolean isVector(){
		 // 
		return true; 
	}

	@Override
	public boolean isRegister(){ // 
		return false; 
	}

	@Override
	public boolean isWire(){ // 
		return false; 
	}
}
