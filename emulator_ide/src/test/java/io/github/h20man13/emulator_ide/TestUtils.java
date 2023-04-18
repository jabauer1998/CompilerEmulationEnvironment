package io.github.H20man13.emulator_ide;


import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.circuit_elem.test_utils.Tuple;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Lexer;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Token;
import io.github.H20man13.emulator_ide.common.Position;

import java.util.ArrayList;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
/**
 * Supplied are the Test utility functions for the main classes. So far these are just
 * classes to examine lexing
 * 
 * @author Jacob Bauer
 */
public class TestUtils {

	private static boolean           lexerPrepared      = false;
	private static boolean           errorLogPrepared   = false;
	private static int               expectedErrorItems = -1;   // Expected error items to find in error log
	private static Tuple<Token.Type> testTokens         = null; // Tuple object to store all of the incoming Test Tokens

	public static void prepareErrorLog(int expectedNumItems){
		expectedErrorItems = expectedNumItems;
		errorLogPrepared = true;
	}

	public static void prepareLexer(Tuple<Token.Type> testArgTokens){
		testTokens = testArgTokens;
		lexerPrepared = true;
	}

	/**
	 * The test Lexer function is how I plan to test whether the Lexer is working correctly
	 * It works by prividing a Lexer object and it verifies the
	 * 
	 * @author Jacob Bauer
	 */
	public static void testLexer(Lexer myLexer){
		assertTrue("Error: expected prepareErrorLog statement before testLexer method", errorLogPrepared);
		assertTrue("Error: expected prepareLexer statement before testLexer method", lexerPrepared);
		List<Token> lexedTokens = myLexer.tokenize();
		List<Token> filteredTokens = Lexer.filterWhiteSpace(lexedTokens);
		
		LinkedList<Token.Type> tokenTypes = testTokens.getList();
		assertTrue("Expected number of tokens provided to be equal the number of tokens found [found -> " + filteredTokens.size()
			+ " | Provided -> " + tokenTypes.size() + ']', filteredTokens.size() == tokenTypes.size());

		if (myLexer.getErrorLog().size() != 0) { myLexer.getErrorLog().printLog(); }

		assertTrue("Expected error log to have " + expectedErrorItems + " [found -> " + myLexer.getErrorLog().size() + ']',
			expectedErrorItems == myLexer.getErrorLog().size());

		for (int i = 0; i < testTokens.size(); i++) {
			assertTrue("Error: token mismatch at token " + i + " [Expected -> " + tokenTypes.get(i) + " | Got -> "
				+ filteredTokens.get(i).getTokenType() + ']', tokenTypes.get(i) == filteredTokens.get(i).getTokenType());
		}

		errorLogPrepared = false;
		lexerPrepared = false;
	}
}
