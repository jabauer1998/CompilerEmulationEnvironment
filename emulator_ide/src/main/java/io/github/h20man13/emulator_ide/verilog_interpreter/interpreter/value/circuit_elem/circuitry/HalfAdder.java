package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry;

import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.AndGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XorGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;

public class HalfAdder {
    public HalfAdder(WireVal Output, WireVal Carry, WireVal Input1, WireVal Input2){
        new AndGate(Carry, Input1, Input2);
        new XorGate(Output, Input1, Input2);
    }
}
