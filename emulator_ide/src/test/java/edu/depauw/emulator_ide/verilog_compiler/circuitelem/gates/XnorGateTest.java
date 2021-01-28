package edu.depauw.emulator_ide.verilog_compiler.circuitelem.gates;
import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XnorGateTest {
    @Test
    public void BasicNorTest(){
    	CircuitElem input1 = new Register(true);
    	CircuitElem input2 = new Register(false);

	CircuitElem wire1 = new Wire();
	CircuitElem wire2 = new Wire();
	
    	CircuitElem xnorGate1 = new XnorGate(wire1, wire2, wire2);
    	assertTrue(xnorGate1.getSignal());
    }
    
    @Test
    public void NestedNorTest(){

	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);
	
    	CircuitElem xnorGate1 = new XnorGate(new XnorGate(new XnorGate(PULLUP, PULLUP), PULLUP), PULLUP);
    	assertTrue(xnorGate1.getSignal());
	CircuitElem xnorGate2 = new XnorGate(new XnorGate(new XnorGate(PULLUP, PULLUP), new XnorGate(PULLUP, PULLUP)), new XnorGate(new XnorGate(PULLUP, PULLUP), new XnorGate(PULLUP, PULLUP)));
    	assertTrue(xnorGate2.getSignal());
	CircuitElem xnorGate3 = new XnorGate(new XnorGate(new XnorGate(PULLUP, PULLDOWN), PULLUP), PULLDOWN);
    	assertTrue(xnorGate3.getSignal());
	CircuitElem xnorGate4 = new XnorGate(new XnorGate(new XnorGate(PULLUP, PULLUP), new XnorGate(PULLUP, PULLDOWN)), new XnorGate(new XnorGate(PULLUP, PULLUP), new XnorGate(PULLDOWN, PULLUP)));
    	assertTrue(xnorGate4.getSignal());
    }

    @Test
    public void MultiNorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	
        CircuitElem myxnor = new XnorGate(PULLUP, PULLUP, PULLUP, new XnorGate(PULLUP, PULLDOWN));
	assertTrue(!myxnor.getSignal());
    }

    @Test
    public void SpamNorTest(){
	
	Register PULLUP = new Register(true);
	Register PULLDOWN = new Register(false);

	CircuitElem myxnor = new XnorGate(PULLUP, PULLDOWN);

	CircuitElem myotherxnor = new XnorGate(myxnor, PULLDOWN, myxnor, myxnor, PULLDOWN);
	
	assertTrue(myotherxnor.getSignal());
    }
}
