package edu.depauw.emulator_ide.verilog_compiler.main;


import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.common.debug.Debugger;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor.Preprocessor;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

public class ParserTest {

	@Test
	public void testExpression(){
		System.out.println("--------------Expression Parser Test----------------");
		String input = "~a[3] + f[5 : 78] || g | x || g && -a || t * m % t ? slakfjd + cool / cooler | t - ~^g : other\n";
		Destination display = new Destination(System.out);
		Source source = new Source(new StringReader(input));
		ErrorLog errorLog = new ErrorLog(display);

		Lexer lex = new Lexer(source, errorLog);
		List<Token> list = lex.tokenize();

		Preprocessor preProcessor = new Preprocessor(errorLog);
		preProcessor.attachList(list);
		preProcessor.executePass();
		List<Token> preprocessedList = preProcessor.fetchResult();


		Parser parse = new Parser(preprocessedList, errorLog);
		Expression exp = parse.parseExpression();

		errorLog.printLog();
		// If this tests passes and doesnt fail then the expression parsed correctly
		assertTrue(errorLog.size() == 0);
	}

	@Test
	public void testStatement(){
		System.out.println("--------------Statement Parser Test--------------");
		String input = "begin \n" + "a = -b + c[6] + d[0: 8 + 60] ^ c;\n" + "assign a = -b + c[6] + d[0: 8 + 60] ^ c;\n"
			+ "forever a <= -b + c[6] + d[0: 8 + 60] ^ c;\n" + "while (a >= t) begin a <= -b + c[6] + d[0: 8 + 60] ^ c; end\n"
			+ "for (i = 0; i < t; i = i + 1) begin a <= -b + c[6] + d[0: 8000 + 60] ^ c; end\n"
			+ "case ({r_VAL_1, r_VAL_2, r_VAL_3})\n" + "3'b000  : r_RESULT <= 0;\n" + "3'b001  : r_RESULT <= 1;\n"
			+ "3'b010  : r_RESULT <= 2;\n" + "default : r_RESULT <= 9;\n" + "endcase\n" + "end\n";
		Destination display = new Destination(System.out);
		Source source = new Source(new StringReader(input));
		ErrorLog errorLog = new ErrorLog(display);
		Lexer lex = new Lexer(source, errorLog);
		List<Token> list = lex.tokenize();


		Preprocessor preProcessor = new Preprocessor(errorLog);
		preProcessor.attachList(list);
		preProcessor.executePass();
		List<Token> preprocessedList = preProcessor.fetchResult();


		Parser parse = new Parser(preprocessedList, errorLog);
		Statement stat = parse.parseStatement();

		errorLog.printLog();

		assertTrue(errorLog.size() == 0);
	}

	@Test
	public void testModule(){
		System.out.println("--------------Module Parser Test--------------");
		String input = "module function_example ();\n" + "reg r_Bit1, r_Bit2, r_Bit3;\n" + "wire w_Result;\n" + "reg  r_Global;\n"
			+ "function do_math;\n" + "input i_bit1, i_bit2, i_bit3;\n" + "reg   v_Temp; // Local Variable\n" + "begin\n"
			+ "// Demonstrates driving external Global Reg\n" + "r_Global = 1'b1;\n" + "v_Temp  = (i_bit1 & i_bit2);\n"
			+ "do_math = (v_Temp | i_bit3);\n" + "end\n" + "endfunction\n"
			+ "assign w_Result = do_math(r_Bit1, r_Bit2, r_Bit3);\n" + "endmodule\n";
		Destination display = new Destination(System.out);
		Source source = new Source(new StringReader(input));
		ErrorLog errorLog = new ErrorLog(display);
		Lexer lex = new Lexer(source, errorLog);
		List<Token> list = lex.tokenize();

		Preprocessor preProcessor = new Preprocessor(errorLog);
		preProcessor.attachList(list);
		preProcessor.executePass();
		List<Token> preprocessedList = preProcessor.fetchResult();
		
		Parser parser = new Parser(preprocessedList, errorLog);
		ModuleDeclaration stat = parser.parseAST();

		errorLog.printLog();

		assertTrue(errorLog.size() == 0);
	}
}
