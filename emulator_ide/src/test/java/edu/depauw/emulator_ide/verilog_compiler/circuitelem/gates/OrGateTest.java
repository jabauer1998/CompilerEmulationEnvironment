package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OrGateTest {
    @Test
    public void BasicOrTest(){
    	CircuitElem PULLUP = new Register(true);
    	CircuitElem PULLDOWN = new Register(false);
	
    	CircuitElem andGate1 = new OrGate(PULLUP, PULLUP);
    	assertTrue(andGate1.getSignal());

    	CircuitElem andGate2 = new OrGate(PULLDOWN, PULLUP);
    	assertTrue(andGate2.getSignal());

    	CircuitElem andGate3 = new OrGate(PULLUP, PULLDOWN);
    	assertTrue(andGate3.getSignal());

    	CircuitElem andGate4 = new OrGate(PULLDOWN, PULLDOWN);
    	assertTrue(!andGate4.getSignal());
    }
    
    @Test
    public void NestedOrest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem andGate1 = new OrGate(new OrGate(new OrGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(andGate1.getSignal());
	CircuitElem andGate2 = new OrGate(new OrGate(new OrGate(PULLUP, PULLUP), new OrGate(PULLUP, PULLUP)), new OrGate(new OrGate(PULLUP, PULLUP), new OrGate(PULLUP, PULLUP)));
    	assertTrue(andGate2.getSignal());
	CircuitElem andGate3 = new OrGate(new OrGate(new OrGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(andGate3.getSignal());
	CircuitElem andGate4 = new OrGate(new OrGate(new OrGate(PULLUP, PULLUP), new OrGate(PULLUP, PULLDOWN)), new OrGate(new OrGate(PULLUP, PULLUP), new OrGate(PULLDOWN, PULLUP)));
    	assertTrue(andGate4.getSignal());
    }

    @Test
    public void MultiOrTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem myor = new OrGate(PULLUP, PULLUP, PULLUP, new OrGate(PULLUP, PULLDOWN));
	assertTrue(myor.getSignal());
    }

    @Test
    public void SpamOrTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem myor = new OrGate(PULLUP, PULLDOWN);

	CircuitElem myotheror = new OrGate(myor, PULLDOWN, myor, myor, PULLDOWN);
	
	assertTrue(myotheror.getSignal());
    }
}
