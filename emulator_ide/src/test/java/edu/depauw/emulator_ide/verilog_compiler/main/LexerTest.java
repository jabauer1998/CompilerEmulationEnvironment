package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;

import static edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.TestUtils.*;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class LexerTest{
        @Test
	void testIdentifiers() {
		String input = "This is test1";
		compareToModel(input);
	}
	
	@Test
	void testReservedWords() {
		String input = "MOD OR PROCEDURE REPEAT RETURN THEN TO TRUE UNTIL VAR WHILE";
		compareToModel(input);
	}

	@Test
	void testIntegers() {
		String input = "0 11 9999 0123456789";
		compareToModel(input);
	}
	
	@Test
	void testStrings() {
		String input = "\"\" \"testing\" \"!@#$%^&(*)-_=+\"";
		compareToModel(input);
	}
	
	@Test
	void testComments() {
		String input = "(* this is a comment *) ((**)) (***) (* * ) *)";
		compareToModel(input);
	}
	
	@Test
	void testOperators() {
		String input = "<<=>>=:=:()=#+-*/&~;,.";
		compareToModel(input);
	}
	
	@Test
	void testCombinations() {
		String input = "PROCEDURE a(b:INTEGER,VAR c:REAL):BOOLEAN;\n"
				+ "\t(* body goes here *)\n"
				+ "BEGIN a(42,3+14)END.\n";
		compareToModel(input);
	}
	
	@Test
	void testErrorRecovery() {
		String input = "bad! &\"unclosed!";
		compareToModel(input);
	}
	
	@Test
	void testErrorRecovery2() {
		String input = "`!@$%^_{}[]|\\'ok?(**";
		compareToModel(input);
	}
}
