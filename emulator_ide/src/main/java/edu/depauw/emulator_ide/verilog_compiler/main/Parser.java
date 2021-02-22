package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.Delay;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;


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

    private Token skip(){
	if(!lexedTokens.isEmpty()){
	    return lexedTokens.remove(0);
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected end of file while parsing", null));
	    errorLog.printLog();
	    System.exit(1);
	    return false;
	}
    }

    private Token peek(){
	if(!lexedTokens.isEmpty()){
	    return lexedTokens.get(0);
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected end of file while parsing", null));
	    errorLog.printLog();
	    System.exit(1);
	    return false;
	}
    }

    private Token match(Token.Type type){
	if(willMatch(type)){
	    return skip();
	} else {
	    Token matched = lexedTokens.get(0);
	    errorLog.addItem(new ErrorItem("Token of type " + type + " expected but token of type " + matched.getTokenType() + " found ", matched.getPosition()));
	    errorLog.printLog();
	    System.exit(1);
	    return null;
	}
    }

    private Expression parseExpression(){
	if(UnOp.isUnOp(peek())){
	    return parseUnaryOperation();
	} else {
	    return parseTerm();
	}
    }

    private Expression parseTerm(){
	if(willMatch(Token.Type.NUM)){
	    
	}
    }

    private Expression parseUnaryOperation(){
        Token op = skip();
	UnOp unOp = new UnOp(unOp);
	Expression term = parsePrimary();
	return new UnaryOperation(unOp, term);
    }

    private Expression parsePrimary(){
	if(willMatch(Token.Type.NUM)){
	    return parseNumValue();
	} else if (willMatch(Token.Type.IDENT)){
	    return parseIdentifier();
	} else if (willMatch(Token.Type.LCURL)){
	    return parseConcatenation();
	} else if (willMatch(Token.Type.DOLLAR)) {
	    skip();
	    return parseIdentifier();
	} else if {
	    Token string = match(Token.Type.STRING);
	    return new StrValue(string);
	}
    }

    private Expression parseConcatenation(){
	ArrayList<Expression> concat = new ArrayList<>();
	match(Token.Type.LCURL);
	do{
	    concat.add(parseExpression());
	    if(willMatch(Token.Type.LCURL)){
		concat.add(parseConcatenation());
		continue;
	    } else if (willMatch(Token.Type.COMMA)){
		skip();
		continue;
	    } else {
		match(Token.Type.RCURL);
		break;
	    }
	} while(true);
	
	return new Concatenation(concat);
    }

    private Expression parseIdentifier(){
	Token ident = match(Token.Type.IDENT);
	if(willMatch(Token.Type.LBRACK)){
	    Expression index1 = parseExpression();
	    Expression index2 = parseExpression();
	    Vector v = new Vector(ident, index1, index2);
	    match(Token.Type.RBRACK);
	    return v;
	} else if (willMatch(Token.Type.LPAR)) {
	    Idenifier ident = new Indentifier(ident);
	    ArrayList<Expression> expList= new ArrayList<>();
	    match(Token.Type.LPAR);
	    
	    do {
		if(willMatch(Token.Type.RPAR)){
		    skip();
		    break;
		} else {
		    Expression exp = parseExpression();
		    expList.add(exp);
		}
	    } while (true);
	    
	    return new FunctionCall(ident, expList);
	} else {
	    return new Identifier(ident);
	}
    }
    
}
