package edu.depauw.emulator_ide.verilog_compiler.visitor;

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
import edu.depauw.emulator_ide.verilog_compiler.ast.ModuleDeclaration;

import static edu.depauw.emulator_ide.verilog_compiler.main.test_utils.TestUtils.*;

import org.junit.Test;
import java.io.StringReader;
import java.io.StringWriter;

public class IndexerVisitorTest{
        @Test
	public void testFullAdder() {

	    /*
		String input = "module fulladder (a, b, c_in, c_out, sum);\n"
		    + "input [3:0] a, b;\n"
		    + "input cin;\n"
		    + "output cout;\n"
		    + "output [3:0] sum;\n"
		    + "assign {cout, sum} = a + b + cin;\n"
		    + "endmodule\n";

		Destination display = new Destination(new StringWriter());
		Source source = new Source(new StringReader(input));
		InfoLog errorLog = new InfoLog(display);
		Lexer lex = new Lexer(source, errorLog);
		Parser parse = new Parser(lex.tokenize(), errorLog);
		ModuleDeclaration moddec = parse.parseAST();
		IndexerVisitor idVisitor = new IndexerVisitor(moddec, display, errorLog);
		idVisitor.visitRoot();
	    */
	}
}
