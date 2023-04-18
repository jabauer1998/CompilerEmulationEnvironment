package io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.gates;


import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Primitive;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Tuple;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.AndGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.Gate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.OrGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.XorGate;
import io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;
import org.junit.Test;
import static io.github.h20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.TestUtils.*;
import static org.junit.Assert.assertTrue;

public class FullAdderTest {

	@Test
	public void FullAdderTest(){
		RegVal A = new RegVal(false);
		RegVal B = new RegVal(false);
		RegVal Cin = new RegVal(false);

		WireVal aW = new WireVal();
		WireVal bW = new WireVal();
		WireVal CinW = new WireVal();

		aW.assignInput(A);
		bW.assignInput(B);
		CinW.assignInput(Cin);

		WireVal iW1 = new WireVal();
		WireVal iW2 = new WireVal();

		Gate Xor1Gate = new XorGate(iW1, aW, bW); // put wires through a basic and gate
		Gate And1Gate = new AndGate(iW2, aW, bW);

		WireVal S = new WireVal();

		Gate Xor2Gate = new XorGate(S, iW1, CinW);

		WireVal iW3 = new WireVal();

		Gate And2Gate = new AndGate(iW3, CinW, iW1);

		WireVal Cout = new WireVal();

		Gate Or1Gate = new OrGate(Cout, iW3, iW2);

		Primitive table = new Primitive(3, 2); // two input one output table
		table.addRow(new Tuple<Boolean>(false, false, false), new Tuple<Boolean>(false, false)); // First Tuple is for inputs and
																									// the second is for outputs
																									// these represent rows in a
																									// boolean logic table
		table.addRow(new Tuple<Boolean>(true, false, false), new Tuple<Boolean>(true, false));
		table.addRow(new Tuple<Boolean>(false, true, false), new Tuple<Boolean>(true, false));
		table.addRow(new Tuple<Boolean>(false, false, true), new Tuple<Boolean>(true, false));
		table.addRow(new Tuple<Boolean>(true, true, false), new Tuple<Boolean>(false, true));
		table.addRow(new Tuple<Boolean>(false, true, true), new Tuple<Boolean>(false, true));
		table.addRow(new Tuple<Boolean>(true, false, true), new Tuple<Boolean>(false, true));
		table.addRow(new Tuple<Boolean>(true, true, true), new Tuple<Boolean>(true, true));

		primitiveVerify(table, new Tuple<RegVal>(A, B, Cin), new Tuple<CircuitElem>(S, Cout)); // check if the boolean logic
																									// table playes out correctly
																									// in the circuit created
	}
}
