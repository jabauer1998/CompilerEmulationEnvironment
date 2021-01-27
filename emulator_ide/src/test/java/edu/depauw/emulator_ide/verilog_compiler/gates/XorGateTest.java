package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XorGateTest {
    @Test
    public void BasicXorTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);
	
    	CircuitElem xorGate1 = new XorGate(PULLUP, PULLUP);
    	assertTrue(!xorGate1.getSignal());

    	CircuitElem xorGate2 = new XorGate(PULLDOWN, PULLUP);
    	assertTrue(xorGate2.getSignal());

    	CircuitElem xorGate3 = new XorGate(PULLUP, PULLDOWN);
    	assertTrue(xorGate3.getSignal());

    	CircuitElem xorGate4 = new XorGate(PULLDOWN, PULLDOWN);
    	assertTrue(!xorGate4.getSignal());
    }
    
    @Test
    public void NestedXorTest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem xorGate1 = new XorGate(new XorGate(new XorGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(!xorGate1.getSignal());
	CircuitElem xorGate2 = new XorGate(new XorGate(new XorGate(PULLUP, PULLUP), new XorGate(PULLUP, PULLUP)), new XorGate(new XorGate(PULLUP, PULLUP), new XorGate(PULLUP, PULLUP)));
    	assertTrue(!xorGate2.getSignal());
	CircuitElem xorGate3 = new XorGate(new XorGate(new XorGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(!xorGate3.getSignal());
	CircuitElem xorGate4 = new XorGate(new XorGate(new XorGate(PULLUP, PULLUP), new XorGate(PULLUP, PULLDOWN)), new XorGate(new XorGate(PULLUP, PULLUP), new XorGate(PULLDOWN, PULLUP)));
    	assertTrue(!xorGate4.getSignal());
    }

    @Test
    public void MultiXorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem myxor = new XorGate(PULLUP, PULLUP, PULLUP, new XorGate(PULLUP, PULLDOWN));
	assertTrue(!myxor.getSignal());
    }

    @Test
    public void SpamXorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem myxor = new XorGate(PULLUP, PULLDOWN);

	CircuitElem myotherxor = new XorGate(myxor, PULLDOWN, myxor, myxor, PULLDOWN);
	
	assertTrue(myotherxor.getSignal());
    }
}
