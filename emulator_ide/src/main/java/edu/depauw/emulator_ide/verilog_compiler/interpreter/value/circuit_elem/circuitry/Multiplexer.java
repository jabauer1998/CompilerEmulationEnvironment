package edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.circuitry;

import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NandGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.gates.NotGate;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;

public class Multiplexer {
    public Multiplexer(WireVal FinalOutput, WireVal Input1, WireVal Input2, WireVal Select){
        WireVal OutputInputSelect = new WireVal();
        WireVal OutputInputNotSelect = new WireVal();
        
        WireVal NotSelect = new WireVal();
        new NotGate(NotSelect, Select);

        new NandGate(OutputInputSelect, Input1, Select);
        new NandGate(OutputInputNotSelect, Input2, NotSelect);
        new NandGate(FinalOutput, OutputInputSelect, OutputInputNotSelect);
    }
}
