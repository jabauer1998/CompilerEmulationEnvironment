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
import edu.depauw.emulator_ide.verilog_compiler.main.Indexer;
import edu.depauw.emulator_ide.verilog_compiler.main.TypeChecker;
import edu.depauw.emulator_ide.verilog_compiler.ast.ModuleDeclaration;

import static edu.depauw.emulator_ide.verilog_compiler.main.test_utils.TestUtils.*;

import org.junit.Test;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.util.List;

public class PreProcessorTest{
    @Test
    public void TestCorrect() {
	String input = "module fulladder (//declare all of the inputs and output ports\n"
	    + "\t\tinput wire [31:0] a,\n"
	    + "\t\tinput wire [31:0] b,\n"
	    + "\t\tinput wire cin,\n"
	    + "\t\toutput wire cout,\n"
	    + "\t\toutput [31:0] sum\n"
            + "\t\t);\n"
	    + "//assign the outputs of the module to the sum of each bit\n"
	    + "`define RESULT {cout, sum}\n"
	    + "`define ADD a + b + cin\n"
	    + "assign `RESULT = `ADD;\n"
	    + "endmodule\n";

	Destination display = new Destination(System.out);
	Source source = new Source(new StringReader(input));
	InfoLog errorLog = new InfoLog(display);
	Lexer lex = new Lexer(source, errorLog);
	
	List<Token> tokens = lex.tokenize();

	//parse the tokens
	Parser parse = new Parser(tokens, errorLog);
	ModuleDeclaration moddec = parse.parseAST();

	//visit it with the type checker
	PreProcessor preProcessor = new PreProcessor(errorLog);
	preProcessor.visit(moddec);

	prepareErrorLog(0);
	testPreProcessor(preProcessor);
    }
}
