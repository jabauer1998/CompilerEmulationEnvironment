package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import java.util.LinkedList;

import edu.depauw.emulator_ide.verilog_compiler.common.gates.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GateTest{
    @Test
    public void ComboGateTest(){
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	Gate or = new OrGate(PULLUP, PULLDOWN);
	Gate and = new AndGate(PULLDOWN, PULLUP);
	Gate nand = new NandGate(PULLDOWN, PULLDOWN);
	Gate xor = new XorGate(or, and, nand, or);
	Gate xnor = new XnorGate(xor, nand, and, or, xor);

	assertTrue(xnor.getSignal());
    }
}
