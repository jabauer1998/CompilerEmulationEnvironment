package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;

import java.util.ArrayList;
import java.lang.Boolean;

public class Outputs{
    private ArrayList<Boolean> outputs = new ArrayList();

    public Inputs(boolean... outputs){
	for(boolean output : outputs){
	    this.outputs.add(output);
	}
    }

    public ArrayList<Boolean> getList(){
	return this.inputs;
    }
}
