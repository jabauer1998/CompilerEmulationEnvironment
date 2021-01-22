package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import java.util.LinkedList;

import edu.depauw.emulator_ide.verilog_compiler.common.gates.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WireTest{
    @Test
    public void BasicWireTest(){
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	Wire up = new Wire(PULLUP);
	Wire down = new Wire(PULLDOWN);
	assertTrue(up.getSignal() == PULLUP.getSignal());
	assertTrue(PULLDOWN.getSignal() == down.getSignal());
    }
    @Test
    public void ToggleSignalTest(){
	Register value = new Register(true);
        CircuitElem andWire = new AndGate(new Wire(value), new Wire(value));
	CircuitElem and = new AndGate(value, value);
	assertTrue(and.getSignal() == andWire.getSignal());
	value.setSignal(false);
	assertTrue(and.getSignal() == andWire.getSignal());
    }
}
