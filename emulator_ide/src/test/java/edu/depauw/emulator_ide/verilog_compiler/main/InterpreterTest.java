package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.main.*;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;

import static edu.depauw.emulator_ide.verilog_compiler.main.test_utils.TestUtils.*;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.StringReader;

public class InterpreterTest {

	@Test
	public void testBinaryOperation() {
	    String input = "8 + 9\n";
	    Destination display = new Destination(System.out);
	    Source source = new Source(new StringReader(input));
	    InfoLog errorLog = new InfoLog(display);
	    Lexer lex = new Lexer(source, errorLog);
	    Parser parse = new Parser(lex.tokenize(), errorLog);
	    Expression exp = parse.parseExpression();
	    Interpreter interpret = new Interpreter(errorLog);
	    
	    assertTrue("Error expected type long but got type " + exp.accept(interpret).getClass(), exp.accept(interpret) instanceof Long);
	    assertTrue("Error expected value 17 but got " + (long)exp.accept(interpret), ((long)exp.accept(interpret)) == 17);
	}
    
}
