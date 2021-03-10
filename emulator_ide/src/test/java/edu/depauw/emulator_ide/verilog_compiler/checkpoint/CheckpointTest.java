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

public class CheckpointTest{
        @Test
	public void Checkpoint1() {

	    String input = "module fulladder (a, b, cin, cout, sum);\n"
		+ "// declare all of the inputs \n"
		+ "input [31:0] a, b;\n"
		+ "input cin;\n"
		+ "wire [31:0] a, b;\n"
		+ "wire cin;\n"
		+ "// declare all of the outputs \n"
		+ "output cout;\n"
		+ "output [31:0] sum;\n"
		+ "wire cout;\n"
		+ "wire [31:0] sum;\n"
		+ "// assign the outputs of the module to the sum of each bit\n"
		+ "assign {cout, sum} = a + b + cin;\n"
		+ "endmodule\n";

	    System.out.print("\n\n---------------Checkpoint 1 Input Program------------------\n\n");

	    System.out.print(input);
	    
	    System.out.print("\n\n-----------Checkpoint 1 Indexer Test Results---------------\n\n");
	    
	    Destination display = new Destination(System.out);
	    Source source = new Source(new StringReader(input));
	    InfoLog errorLog = new InfoLog(display);
	    Lexer lex = new Lexer(source, errorLog);
	    Parser parse = new Parser(lex.tokenize(), errorLog);
	    ModuleDeclaration moddec = parse.parseAST();
	    IndexerVisitor idVisitor = new IndexerVisitor(moddec, display, errorLog);
	    idVisitor.visitRoot();
	    System.out.print("\n\n---------------------------------------------------------\n\n");
	}
}
