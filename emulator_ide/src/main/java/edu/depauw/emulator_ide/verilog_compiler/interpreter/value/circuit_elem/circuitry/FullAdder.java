package edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry;

import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.AndGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.OrGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.XorGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;

public class FullAdder {
    public FullAdder(WireVal CarryOut, WireVal Output, WireVal InputA, WireVal InputB, WireVal CarryIn){
       WireVal AXorBOutput = new WireVal();
       WireVal AAndBOutput = new WireVal();

       new XorGate(AXorBOutput, InputA, InputB);
       new AndGate(AAndBOutput, InputA, InputB);

       WireVal AXorBAndCinOutput = new WireVal();
       new XorGate(Output, AXorBOutput, CarryIn);
       new AndGate(AXorBAndCinOutput, AXorBOutput, CarryIn);

       new OrGate(CarryOut, AXorBAndCinOutput, AAndBOutput);
    }
}
