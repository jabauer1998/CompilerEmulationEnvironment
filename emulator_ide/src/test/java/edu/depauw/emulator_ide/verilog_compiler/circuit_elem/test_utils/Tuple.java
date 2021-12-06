package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;


import java.util.ArrayList;
import java.lang.Boolean;

public class Tuple<InputType> {

    private ArrayList<InputType> tuple;

    public Tuple(InputType... tuple) {
        this.tuple = new ArrayList<>();

        for (InputType elem : tuple) { this.tuple.add(elem); }

    }

    public ArrayList<InputType> getList(){ return this.tuple; }

    public int size(){ return tuple.size(); }
}
