package edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.gates;


import edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NotGate;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;
import org.junit.Test;
import static edu.depauw.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.TestUtils.*;
import static org.junit.Assert.assertTrue;

public class NotGateTest {

	@Test
	public void NotTest(){
		RegVal input = new RegVal(false);

		WireVal in = new WireVal();
		WireVal out = new WireVal();

		in.assignInput(input);

		CircuitElem norGate = new NotGate(out, in); // put wires through a basic nor gate

		Primitive table = new Primitive(1, 1); // two input one output table
		table.addRow(new Tuple<Boolean>(true), new Tuple<Boolean>(false));
		table.addRow(new Tuple<Boolean>(false), new Tuple<Boolean>(true));

		primitiveVerify(table, new Tuple<RegVal>(input), new Tuple<CircuitElem>(out)); // check if the boolean logic table
																							// playes out correctly in the circuit
																							// created
	}
}
