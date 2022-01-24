package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.nodes.gates.XnorGate;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.web.Wire;
import static edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.TestUtils.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XnorGateTest {

	@Test
	public void XnorTest(){
		Register input1 = new Register(false);
		Register input2 = new Register(false);

		Wire in1 = new Wire();
		Wire in2 = new Wire();
		Wire out1 = new Wire();

		in1.assignInput(input1);
		in2.assignInput(input2);

		CircuitElem xnorGate = new XnorGate(out1, in1, in2); // put wires through a basic and gate

		Primitive table = new Primitive(2, 1); // two input one output table
		table.addRow(new Tuple<Boolean>(true, true), new Tuple<Boolean>(true));
		table.addRow(new Tuple<Boolean>(true, false), new Tuple<Boolean>(false));
		table.addRow(new Tuple<Boolean>(false, true), new Tuple<Boolean>(false));
		table.addRow(new Tuple<Boolean>(false, false), new Tuple<Boolean>(true));

		primitiveVerify(table, new Tuple<Register>(input1, input2), new Tuple<CircuitElem>(out1)); // check if the boolean logic
																									// table playes out correctly
																									// in the circuit created
	}
}
