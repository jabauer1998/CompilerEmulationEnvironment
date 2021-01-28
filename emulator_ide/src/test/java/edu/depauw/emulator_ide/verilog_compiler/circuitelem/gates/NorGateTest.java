package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NorGateTest {
    @Test
    public void BasicNorTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);
	
    	CircuitElem norGate1 = new NorGate(PULLUP, PULLUP);
    	assertTrue(!norGate1.getSignal());

    	CircuitElem norGate2 = new NorGate(PULLDOWN, PULLUP);
    	assertTrue(!norGate2.getSignal());

    	CircuitElem norGate3 = new NorGate(PULLUP, PULLDOWN);
    	assertTrue(!norGate3.getSignal());

    	CircuitElem norGate4 = new NorGate(PULLDOWN, PULLDOWN);
    	assertTrue(norGate4.getSignal());
    }
    
    @Test
    public void NestedNorTest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem norGate1 = new NorGate(new NorGate(new NorGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(!norGate1.getSignal());
	CircuitElem norGate2 = new NorGate(new NorGate(new NorGate(PULLUP, PULLUP), new NorGate(PULLUP, PULLUP)), new NorGate(new NorGate(PULLUP, PULLUP), new NorGate(PULLUP, PULLUP)));
    	assertTrue(!norGate2.getSignal());
	CircuitElem norGate3 = new NorGate(new NorGate(new NorGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(norGate3.getSignal());
	CircuitElem norGate4 = new NorGate(new NorGate(new NorGate(PULLUP, PULLUP), new NorGate(PULLUP, PULLDOWN)), new NorGate(new NorGate(PULLUP, PULLUP), new NorGate(PULLDOWN, PULLUP)));
    	assertTrue(!norGate4.getSignal());
    }

    @Test
    public void MultiNorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem mynor = new NorGate(PULLUP, PULLUP, PULLUP, new NorGate(PULLUP, PULLDOWN));
	assertTrue(!mynor.getSignal());
    }

    @Test
    public void SpamNorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem mynor = new NorGate(PULLUP, PULLDOWN);

	CircuitElem myothernor = new NorGate(mynor, PULLDOWN, mynor, mynor, PULLDOWN);
	
	assertTrue(myothernor.getSignal());
    }
}
