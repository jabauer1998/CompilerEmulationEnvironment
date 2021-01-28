package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NandGateTest {
    @Test
    public void BasicNandTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);
	
    	CircuitElem nandGate1 = new NandGate(PULLUP, PULLUP);
    	assertTrue(!nandGate1.getSignal());

    	CircuitElem nandGate2 = new NandGate(PULLDOWN, PULLUP);
    	assertTrue(nandGate2.getSignal());

    	CircuitElem nandGate3 = new NandGate(PULLUP, PULLDOWN);
    	assertTrue(nandGate3.getSignal());

    	CircuitElem nandGate4 = new NandGate(PULLDOWN, PULLDOWN);
    	assertTrue(nandGate4.getSignal());
    }
    
    @Test
    public void NestedNandTest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem nandGate1 = new NandGate(new NandGate(new NandGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(!nandGate1.getSignal());
	CircuitElem nandGate2 = new NandGate(new NandGate(new NandGate(PULLUP, PULLUP), new NandGate(PULLUP, PULLUP)), new NandGate(new NandGate(PULLUP, PULLUP), new NandGate(PULLUP, PULLUP)));
    	assertTrue(!nandGate2.getSignal());
	CircuitElem nandGate3 = new NandGate(new NandGate(new NandGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(nandGate3.getSignal());
	CircuitElem nandGate4 = new NandGate(new NandGate(new NandGate(PULLUP, PULLUP), new NandGate(PULLUP, PULLDOWN)), new NandGate(new NandGate(PULLUP, PULLUP), new NandGate(PULLUP, PULLUP)));
    	assertTrue(!nandGate4.getSignal());
    }

    @Test
    public void MultiNandTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem mynand = new NandGate(PULLUP, PULLUP, PULLUP, new NandGate(PULLUP, PULLDOWN));
	assertTrue(!mynand.getSignal());
    }

    @Test
    public void SpamNandTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem mynand = new NandGate(PULLUP, PULLUP);

	CircuitElem myothernand = new NandGate(mynand, mynand, PULLUP, mynand, PULLDOWN);
	
	assertTrue(myothernand.getSignal());
    }
}
