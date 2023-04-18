package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.gates;


import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Primitive;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Tuple;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NandGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;
import org.junit.Test;
import static io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.TestUtils.*;
import static org.junit.Assert.assertTrue;

public class NandGateTest {

	@Test
	public void NandTest(){
		RegVal input1 = new RegVal(false);
		RegVal input2 = new RegVal(false);

		WireVal in1 = new WireVal();
		WireVal in2 = new WireVal();
		WireVal out1 = new WireVal();

		in1.assignInput(input1);
		in2.assignInput(input2);

		CircuitElem andGate = new NandGate(out1, in1, in2); // put wires through a basic and gate

		Primitive table = new Primitive(2, 1); // two input one output table
		table.addRow(new Tuple<Boolean>(true, true), new Tuple<Boolean>(false));
		table.addRow(new Tuple<Boolean>(true, false), new Tuple<Boolean>(true));
		table.addRow(new Tuple<Boolean>(false, true), new Tuple<Boolean>(true));
		table.addRow(new Tuple<Boolean>(false, false), new Tuple<Boolean>(true));

		primitiveVerify(table, new Tuple<RegVal>(input1, input2), new Tuple<CircuitElem>(out1)); // check if the boolean logic
																									// table playes out correctly
																									// in the circuit created
	}
}
