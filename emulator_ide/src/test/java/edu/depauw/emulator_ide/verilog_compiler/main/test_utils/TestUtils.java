package edu.depauw.emulator_ide.verilog_compiler.main.test_utils;

import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.main.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import static org.junit.Assert.*;

import java.util.LinkedList;
/**
 * Supplied are the Test utility functions for the main classes.
 * So far these are just classes to examine lexing
 * @author Jacob Bauer
 */
public class TestUtils{
    private static boolean lexerPrepared = false;
    private static int expectedErrorItems = -1; //Expected error items to find in error log
    private static Tuple<Token.Type> testTokens = null; //Tuple object to store all of the incoming Test Tokens
    
    public static void prepareLexer(Tuple<Token.Type> testTokens, int expectedErrorItems){
	this.lexerPrepared = true;
	this.expectedErrorItems = expectedErrorItems;
	this.testTokens = testTokens;
    }
    /** 
     * The test Lexer function is how I plan to test whether the Lexer is working correctly
     * @author Jacob Bauer
     */
    public static void testLexer(Lexer myLexer){
	if(lexerPrepared){
	    ArrayList<Token> lexedTokens = myLexer.tokenise();
	    if(lexedTokens.size() == testItems.size()){
		ArrayList<Token.Type> tokenTypes = testTokens.getList();
		for(int i = 0; i < tokens.size(); i++){
		    assertTrue("Error: token mismatch at token " + i, tokenTypes.get(i) == lexedTokens.get(i).getTokenType());
		}
	    } else {
		System.err.println("Error: size mismatch between Tuple provided and Tokens retrieved from the source");
		fail();
	    }
	} else {
	    System.err.println("Error: expected prepare statement before excecution");
	    fail();
	}
    }
}

