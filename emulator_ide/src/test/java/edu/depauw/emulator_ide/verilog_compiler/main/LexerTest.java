package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;

import static edu.depauw.emulator_ide.verilog_compiler.main.test_utils.TestUtils.*;

import org.junit.Test;
import java.io.StringReader;

public class LexerTest{
        @Test
	public void testIdentifiers() {
		String input = "This is test1";
		prepareLexer(new Tuple(Token.Type.IDENT, Token.Type.IDENT, Token.Type.IDENT), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
	
	@Test
	public void testKeyWords() {
		String input = "initial allways begin end module endmodule task endtask function endfunction assign posedge negedge or if else while forever repeat for integer real reg";
		prepareLexer(new Tuple(Token.Type.INIT, Token.Type.ALLWAYS, Token.Type.BEGIN, Token.Type.END, Token.Type.MODULE, Token.Type.ENDMODULE, Token.Type.TASK, Token.Type.ENDTASK, Token.Type.FUNCTION, Token.Type.ENDFUNCTION, Token.Type.ASSIGN, Token.Type.POSEGE, Token.Type.NEGEGE, Token.Type.OR, Token.Type.IF, Token.Type.ELSE, Token.Type.WHILE, Token.Type.FOREVER, Token.Type.REPEAT, Token.Type.FOR, Token.Type.INT, Token.Type.REAL, Token.Type.REG), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}

	@Test
	public void testIntegers() {
		String input = "0 11 9999 0123456789";
		prepareLexer(new Tuple(Token.Type.NUM, Token.Type.NUM, Token.Type.NUM, Token.Type.NUM), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
	
	@Test
	public void testStrings() {
		String input = "\"\" \"testing\" \"!@#$%^&(*)-_=+\"";
		prepareLexer(new Tuple(Token.Type.STRING, Token.Type.STRING, Token.Type.STRING), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
	
	@Test
	public void testComments() {
	        String input = "/* this is a comment */ // this is another comment";
		prepareLexer(new Tuple(), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
	
	@Test
	public void testOperators() {
		String input = "(){}[],+-*%/?:<><=>=#;@$= == === != !== &&||!&~ | ^~~^ ~& ~| <<>><<<>>>";
		prepareLexer(new Tuple(Token.Type.LPAR, Token.Type.RPAR, Token.Type.LCURL, Token.Type.RCURL, Token.Type.LBRACK, Token.Type.RBRACK, Token.Type.COMMA, Token.Type.PLUS, Token.Type.MINUS, Token.Type.TIMES, Token.Type.MOD, Token.Type.DIV, Token.Type.QUEST, Token.Type.COLON, Token.Type.AT, Token.Type.DOLLAR, Token.Type.EQ1, Token.Type.EQ2, Token.Type.EQ3, Token.Type.NE1, Token.Type.NE2, Token.Type.LAND, Token.Type.LOR, Token.Type.LNEG, Token.Type.BAND, Token.Type.LOR, Token.Type.LNEG, Token.Type.BAND, Token.Type.BNEG, Token.Type.BOR, Token.Type.BXNOR, Token.Type.BXNOR, Token.Type.BNAND, Token.Type.BNOR, Token.Type.LSHIFT, Token.Type.RSHIFT, Token.Type.ALSHIFT, Token.Type.ARSHIFT), 0);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
	
	@Test
	public void testErrors() {
		String input = "\"unclosed!";
		prepareLexer(new Tuple(), 1);
		testLexer(new Lexer(new Source(new StringReader(input)), new InfoLog(new Destination(System.out))));
	}
}
