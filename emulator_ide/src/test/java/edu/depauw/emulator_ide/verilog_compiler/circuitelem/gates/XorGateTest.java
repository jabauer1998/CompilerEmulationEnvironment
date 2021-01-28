package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XorGateTest {
    @Test
    public void XorTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);

	Wire pd = new Wire();
	wire pu = new Wire();

	pd.setInput(PULLDOWN);
	pu.setInput(PULLUP);
	
    	CircuitElem xorGate1 = new XorGate(pu, pu);
    	assertTrue(!xorGate1.getSignal());

    	CircuitElem xorGate2 = new XorGate(pd, pu);
    	assertTrue(xorGate2.getSignal());

    	CircuitElem xorGate3 = new XorGate(pu, pd);
    	assertTrue(xorGate3.getSignal());

    	CircuitElem xorGate4 = new XorGate(pd, pd);
    	assertTrue(!xorGate4.getSignal());
    }
}
