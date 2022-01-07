package edu.depauw.emulator_ide.verilog_compiler.main.util;


import org.junit.Test;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Vector;
import edu.depauw.emulator_ide.verilog_compiler.passes.util.OpUtil;
import static org.junit.Assert.*;

public class OpUtilTest {

	@Test
	public void testToLong(){
		long value = OpUtil.toLong(new Register(true));
		assertTrue("Error: Value is not equal to 1 the value equals " + value, value == 1);

		Vector<CircuitElem> circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not equal to 5 for 2.1.0 vector the value equals " + value, value == 5);

		circ = new Vector<CircuitElem>(3, 5);
		circ.setValue(3, new Register(true));
		circ.setValue(4, new Register(false));
		circ.setValue(5, new Register(true));

		value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not equal to 5 for the 3.4.5 the value equals " + value, value == 5);
	}

	@Test
	public void testFullAssign(){
		// Shallow assign
		// equal

		Vector<CircuitElem> circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		Vector<CircuitElem> circ1 = new Vector<CircuitElem>(7, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(true));

		OpUtil.shallowAssign(circ, circ1); // assign 7 to circ which was 5 before

		long value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not equal to 5 for the 3.4.5 the value equals " + value, value == 7);

		// snaller to greater

		circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		circ1 = new Vector<CircuitElem>(8, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(false));
		circ1.setValue(8, new Register(true));

		OpUtil.shallowAssign(circ1, circ); // assign 7 to circ which was 5 before

		value = OpUtil.toLong(circ1);
		assertTrue("Error: Value is not equal to 5 for the 3.4.5 the value equals " + value, value == 5);

		// greater to smaller

		circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		circ1 = new Vector<CircuitElem>(8, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(false));
		circ1.setValue(8, new Register(true));

		OpUtil.shallowAssign(circ, circ1); // assign 7 to circ which was 5 before

		value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not expected we expected 5 bit got " + value, value == 5);

		// Deep assign
		// equal

		circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		circ1 = new Vector<CircuitElem>(7, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(true));

		OpUtil.deepAssign(circ, circ1); // assign 7 to circ which was 5 before

		value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not equal to 7 for the value equals " + value, value == 7);

		// snaller to greater

		circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		circ1 = new Vector<CircuitElem>(8, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(false));
		circ1.setValue(8, new Register(true));

		OpUtil.deepAssign(circ1, circ); // assign 7 to circ which was 5 before

		value = OpUtil.toLong(circ1);
		assertTrue("Error: Value is not equal to 5 for the 3.4.5 the value equals " + value, value == 5);

		// greater to smaller

		circ = new Vector<CircuitElem>(2, 0);
		circ.setValue(0, new Register(true));
		circ.setValue(1, new Register(false));
		circ.setValue(2, new Register(true));

		circ1 = new Vector<CircuitElem>(8, 5);
		circ1.setValue(5, new Register(true));
		circ1.setValue(6, new Register(true));
		circ1.setValue(7, new Register(false));
		circ1.setValue(8, new Register(true));

		OpUtil.deepAssign(circ, circ1); // assign 7 to circ which was 5 before

		value = OpUtil.toLong(circ);
		assertTrue("Error: Value is not equal to 5 for the 3.4.5 the value equals " + value, value == 5);
	}
}
