package edu.depauw.emulator_ide.verilog_compiler.main.test_utils;

import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.main.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class test_utils{
    private static boolean lexerPrepared = false;
    private static int expectedErrorItems = -1;
    public static void prepareLexer(List<Token.Type> dataItems, int errorItems){
	lexerPrepared = true;
	expectedErrorItems = errorItems;
    }

    public static void testLexer(Lexer myLexer){
	if(lexerPrepared){
	    
	} else {
	    System.err.println("Error: expected prepare statement before excecution");
	}
    }
}

