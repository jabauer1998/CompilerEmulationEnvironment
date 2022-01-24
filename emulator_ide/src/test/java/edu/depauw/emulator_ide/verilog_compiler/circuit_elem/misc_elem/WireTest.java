package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.nodes.Register;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.web.Wire;
import static edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.TestUtils.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WireTest {

	@Test
	public void WireTest(){
		Register input = new Register(false);

		Wire output = new Wire();

		output.assignInput(input);

		Primitive table = new Primitive(1, 1); // two input one output table
		table.addRow(new Tuple<Boolean>(true), new Tuple<Boolean>(true));
		table.addRow(new Tuple<Boolean>(false), new Tuple<Boolean>(false));
		table.addRow(new Tuple<Boolean>(true), new Tuple<Boolean>(true));

		primitiveVerify(table, new Tuple<Register>(input), new Tuple<CircuitElem>(output)); // check if the boolean logic table
																							// playes out correctly in the circuit
																							// created
	}
}
