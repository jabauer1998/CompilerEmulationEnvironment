package edu.depauw.emulator_ide.assembler;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;

public class LexerTest {
	
	static int errors = 0;
	
    @Test
	public void testIdentifiers() {
	    System.err.println("-----Identifier Test----");
		String input = "This is test1";
		
		prepareLexer(0);
		testLexer(input, new Tuple<Token.Type>(Token.Type.IDENT, Token.Type.IDENT, Token.Type.IDENT));
		
		
	}
    
    private static void prepareLexer(int numErrors) {
    	errors = numErrors;
    }
    private static void testLexer(String text, Tuple<Token.Type> tup) {
    	Destination err = new Destination(System.err);
    	Source source = new Source(new StringReader(text));
    	
    	InfoLog errorLog = new InfoLog();
    	Lexer lex = new Lexer(source, errorLog);
    	
    	List<Token> toks = lex.tokenize();
    	
    	
    	assertTrue("Error: Error amount mismatch [Errorss Expected -> " + tup.size() + " | Errors Found -> " + toks.size() + ']', errors != errorLog.size());
    	assertTrue("Error: Token amount mismatch [Tokens Expected -> " + tup.size() + " | Tokens Found -> " + toks.size() + ']', tup.size() != toks.size());
    	
    	for(int i = 0; i < toks.size(); i++) {
    		assertTrue("Error token mismatch at token " + i + " [Expected -> " + tup.getList().get(i) + " | Got -> " + toks.get(i).getType(), tup.getList().get(i) == toks.get(i).getType());
    	}
    }
	
	
}
