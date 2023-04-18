package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web;


import java.util.HashSet;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.Node;

/**
 * The wire class is used to attach all of the CircuitElems together. The wire class
 * should work just like a Wire Net in Verilog
 * 
 * @author Jacob Bauer
 */

public class WireVal extends Web {

    private HashSet<CircuitElem> outputs; // Outputs of the wire object
    private CircuitElem          input;

    /**
     * This is the constructor of the wire class
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public WireVal(CircuitElem input) {
        this.outputs = new HashSet<>();
        this.input = input;
    }

    public WireVal() { this(null); }

    /**
     * The update method is used to update the wires outputs.
     * 
     * @param  None
     * @author      Jacob Bauer
     */
    public void update(){

        for (CircuitElem output : outputs) { output.update(); }

    }

    /**
     * The hasOutput is used to check if the output allready exists inside the Linkedlist
     * 
     * @param  output: the output you want to see if it exists
     * @author         Jacob Bauer
     */
    public boolean hasOutput(CircuitElem output){ return outputs.contains(output); }

    /**
     * Add output method adds non duplicate circuit elem to the hashset
     */
    public void addOutput(CircuitElem output){ outputs.add(output); }

    public boolean getStateSignal(){
        if (input == null) return false;

        return input.getStateSignal();
    }

    /**
     * The assign input method is used to attach an input to the Wire
     * @param input
     */

    public void assignInput(CircuitElem input){
        this.input = input;
        
        if(this.input instanceof Web){
            ((Web)this.input).addOutput(this);
        } else {
            ((Node)this.input).attachOutput(this);
        }
    }

    public String toString(){
		return "Wire with 1 input";
	}

    @Override
    public boolean isBoolValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShortValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isByteValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedIntValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedLongValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRealValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isStringValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return true; }
}
