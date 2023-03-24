package edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils;


import java.util.LinkedList;

public class Tuple<InputType> {

    private LinkedList<InputType> tuple;

    public Tuple(InputType... tuple) {
        this.tuple = new LinkedList<>();
        for (InputType elem : tuple) { this.tuple.add(elem); }

    }

    public LinkedList<InputType> getList(){ return this.tuple; }

    public int size(){ return tuple.size(); }
}
