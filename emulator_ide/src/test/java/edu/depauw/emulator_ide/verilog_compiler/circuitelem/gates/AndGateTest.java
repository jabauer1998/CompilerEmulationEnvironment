package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AndGateTest{
    @Test
    public void BasicAndTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);
	
    	CircuitElem andGate1 = new AndGate(PULLUP, PULLUP);
    	assertTrue(andGate1.getSignal());

    	CircuitElem andGate2 = new AndGate(PULLDOWN, PULLUP);
    	assertTrue(!andGate2.getSignal());

    	CircuitElem andGate3 = new AndGate(PULLUP, PULLDOWN);
    	assertTrue(!andGate3.getSignal());

    	CircuitElem andGate4 = new AndGate(PULLDOWN, PULLDOWN);
    	assertTrue(!andGate4.getSignal());
    }
    
    @Test
    public void NestedAndTest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem andGate1 = new AndGate(new AndGate(new AndGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(andGate1.getSignal());
	CircuitElem andGate2 = new AndGate(new AndGate(new AndGate(PULLUP, PULLUP), new AndGate(PULLUP, PULLUP)), new AndGate(new AndGate(PULLUP, PULLUP), new AndGate(PULLUP, PULLUP)));
    	assertTrue(andGate2.getSignal());
	CircuitElem andGate3 = new AndGate(new AndGate(new AndGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(!andGate3.getSignal());
	CircuitElem andGate4 = new AndGate(new AndGate(new AndGate(PULLUP, PULLUP), new AndGate(PULLUP, PULLDOWN)), new AndGate(new AndGate(PULLUP, PULLUP), new AndGate(PULLUP, PULLUP)));
    	assertTrue(!andGate4.getSignal());
    }

    @Test
    public void MultiAndTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem myand = new AndGate(PULLUP, PULLUP, PULLUP, new AndGate(PULLUP, PULLDOWN));
	assertTrue(!myand.getSignal());
    }

    @Test
    public void SpamAndTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem myand = new AndGate(PULLUP, PULLUP);

	CircuitElem myotherand = new AndGate(myand, PULLUP, myand, PULLDOWN);
	
	assertTrue(!myotherand.getSignal());
    }
}
