package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import java.util.List;


public class Parser{

    private final List<Token> lexedTokens;
    private final InfoLog errorLog;

    /**
     * This is the consturctor to the parser class
     * @param tokenArray array of token objects passed into this by the lexer
     * @param errorLog errorLog to print error messages
     */

    public Parser(List<Token> tokens, InfoLog errorLog){
	this.lexedTokens = tokens;
	this.errorLog = errorLog;
    }

    private boolean willMatch(Token.Type type){
	if(!lexedTokens.isEmpty()){
	    errorLog.addItem(new ErrorItem("Unexpected end of file while parsing", null));
	    errorLog.printLog();
	    System.exit(1);
	    return false;
	} else {
	    return lexedTokens.get(0).getTokenType() == type; 
	}
    }

    private Token match(Token.Type type){
	if(willMatch(type)){
	    return lexedTokens.remove(0);
	} else {
	    Token matched = lexedTokens.get(0);
	    errorLog.addItem(new ErrorItem("Token of type " + type + " expected but token of type " + matched.getTokenType() + " found ", matched.getPosition()));
	    errorLog.printLog();
	    System.exit(1);
	    return null;
	}
    }

}
