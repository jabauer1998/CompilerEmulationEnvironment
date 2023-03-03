package edu.depauw.emulator_ide.verilog_compiler.interpreter;


import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;

import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.RegVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;
import org.junit.Test;
import static edu.depauw.emulator_ide.TestUtils.*;
import static org.junit.Assert.*;
import java.io.StringReader;

public class InterpreterTest {

	@Test
	public void testAdditionOperation(){
		String input = "8 + 9\n";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The Result is not equal to 17", Result.intValue() == 17);
	}

	@Test
	public void testSubtractionOperation(){
		String input = "20 - 9";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result isnt of type Long", Result.isLongValue());
		assertTrue("The result is not equal to 11", Result.intValue() == 11);
	}

	@Test
	public void testDivisionOperation(){
		String input = "20 / 10";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("Result is not equal to 2", Result.intValue() == 2);
	}

	@Test
	public void testMultiplicationOperation(){
		String input = "20 * 10";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The Result is not equal to 200", Result.intValue() == 200);
	}

	@Test
	public void testBitwiseOrOperation(){
		String input = "1 | 2";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is 3", Result.intValue() == 3);
	}
}
