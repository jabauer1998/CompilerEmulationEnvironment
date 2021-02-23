package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;

import java.util.ArrayList;


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
	    return null;
	}
    }

    private Token peek(){
	if(!lexedTokens.isEmpty()){
	    return lexedTokens.get(0);
	} else {
	    errorLog.addItem(new ErrorItem("Unexpected end of file while parsing", null));
	    errorLog.printLog();
	    System.exit(1);
	    return null;
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

    /**
     * Below is the code for parsing expressions for the verilog lanuage.
     * These recursive decent takes into account all operator precedence and it can also be used to parse strings
     * There are no booleans in Verilog so having a booelan would not make a bunch of sense.
     * @author Jacob Bauer
     */

    //Expression -> STRING | LOR_Expression
    
    private Expression parseExpression(){
	if(willMatch(Token.Type.STRING)){
	    Token string = skip();
	    return new StrValue(string);
	} else {
	    Expression expression = parseLOR_Expression();
	    if(willMatch(Token.Type.QUEST)){
		skip();
		Expression left = parseExpression();
		match(Token.Type.COLON);
		Expression right = parseExpression();
		expression = new TernaryExpression(expression, left, right);
	    }
	    return expression;
	}
    }

    // ExpressionList -> Expression ExpressionListRest
    // ExpressionListRest -> , Expression ExpressionListRest | null
    private ExpressionList parseExpressionList(){
	List<Expression> expList = new ArrayList<>();
	
	expList.add(parseExpression());
	
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    Expression exp = parseExpression();
	    expList.add(exp);
	}
	
	return new ExpressionList(expList);
    }

    // LOR_Expression -> LAND_Expression BinOp LAND_Expression
    private Expression parseLOR_Expression(){
	Expression left = parseLAND_Expression();
	while(willMatch(Token.Type.LOR)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseLAND_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // LAND_Expression -> BOR_Expression BinOp BOR_Expression
    private Expression parseLAND_Expression(){
	Expression left = parseBOR_Expression();
	while(willMatch(Token.Type.LAND)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseBOR_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // BOR_Expression -> BXOR_Expression BinOp BXOR_Expression
    private Expression parseBOR_Expression(){
	Expression left = parseBXOR_Expression();
	while(willMatch(Token.Type.BOR) || willMatch(Token.Type.BNOR)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseBXOR_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }
    
    // BXOR_Expression -> BAND_Expression BinOp BAND_Expression
    private Expression parseBXOR_Expression(){
	Expression left = parseBAND_Expression();
	while(willMatch(Token.Type.BXOR) || willMatch(Token.Type.BXNOR)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseBAND_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // BAND_Expression -> NE_Expression BinOp NE_Expression
    private Expression parseBAND_Expression(){
	Expression left = parseNE_Expression();
	while(willMatch(Token.Type.BNAND) || willMatch(Token.Type.BAND)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseNE_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // NE_Expression -> REL_Expression BinOp REL_Expression
    private Expression parseNE_Expression(){
	Expression left = parseREL_Expression();
	while(willMatch(Token.Type.NE1) || willMatch(Token.Type.NE2) || willMatch(Token.Type.EQ2) || willMatch(Token.Type.EQ3)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseREL_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // REL_Expression -> SHIFT_Expression BinOp SHIFT_Expression
    private Expression parseREL_Expression(){
	Expression left = parseSHIFT_Expression();
	while(willMatch(Token.Type.GE) || willMatch(Token.Type.GT) || willMatch(Token.Type.LT) || willMatch(Token.Type.LE)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseSHIFT_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // SHIFT_Expression -> BIN_Expression BinOp BIN_Expression
    private Expression parseSHIFT_Expression(){
	Expression left = parseBIN_Expression();
	while(willMatch(Token.Type.LSHIFT) || willMatch(Token.Type.RSHIFT)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseBIN_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // BIN_Expression -> MULT_Expression BinOp MULT_Expression
    private Expression parseBIN_Expression(){
	Expression left = parseMULT_Expression();
	while(willMatch(Token.Type.PLUS) || willMatch(Token.Type.MINUS)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseMULT_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // MULT_Expression -> UNARY_Expression BinOp UNARY_Expression
    private Expression parseMULT_Expression(){
	Expression left = parseUNARY_Expression();
	while(willMatch(Token.Type.TIMES) || willMatch(Token.Type.MOD) || willMatch(Token.Type.DIV)){
	    Token opToken = skip();
	    BinOp Binop = new BinOp(opToken);
	    Expression right = parseUNARY_Expression();
	    left = new BinaryOperation(left, Binop, right);
	}
	return left;
    }

    // UNARY_Expression -> UnOp Primary | Primary
    private Expression parseUNARY_Expression(){
	if(willMatch(Token.Type.PLUS) || willMatch(Token.Type.MINUS)){
	    Token op = skip();
	    UnOp unop = new UnOp(op);
	    Expression right = parsePrimary();
	    return new UnaryOperation(unop, right);
	} else {
	    return parsePrimary();
	}
    }

    // Primary -> NumValue | IDENT | Concatenation | SystemCall
    private Expression parsePrimary(){
	if(willMatch(Token.Type.NUM)){
	    return parseNumValue();
	} else if (willMatch(Token.Type.IDENT)){
	    return parseIdentifier();
	} else if (willMatch(Token.Type.LCURL)){
	    return parseConcatenation();
	} else if (willMatch(Token.Type.LPAR)) {
	    skip();
	    Expression exp = parseExpression();
	    match(Token.Type.RPAR);
	    return exp;
	} else {
	    return parseSystemCall();
	}
    }

    // SystemCall -> $ IDENT
    private Expression parseSystemCall(){
	match(Token.Type.DOLLAR);
	Token identToken = match(Token.Type.IDENT);
	Identifier ident = new Identifier(identToken);
	if(willMatch(Token.Type.LPAR)){
	    skip();
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    return new FunctionCall(ident, expList);
	} else {
	    return new FunctionCall(ident);
	}
    }

    //Concatenation -> { ExpressionList }
    private Expression parseConcatenation(){
	match(Token.Type.LCURL);
	ExpressionList expList = parseExpressionList();
	match(Token.Type.RCURL);
	return new Concatenation(expList);
    }

    //Identifier -> IDENT | IDENT [ Expression : Expression ] | IDENT [ Expression ] | IDENT ( ExpressionList ) | IDENT ()
    private Expression parseIdentifier(){
	Token identToken = match(Token.Type.IDENT);
	if(willMatch(Token.Type.LBRACK)){
	    skip();
	    Identifier ident = new Identifier(identToken);
	    Expression index1 = parseExpression();
	    if(willMatch(Token.Type.COLON)){
		skip();
		Expression index2 = parseExpression();
		match(Token.Type.RBRACK);
		return new Vector(ident, index1, index2);
	    } else {
		match(Token.Type.RBRACK);
		return new Vector(ident, index1);
	    }
	} else if (willMatch(Token.Type.LPAR)) {
	    skip();
	    Identifier ident = new Identifier(identToken);
	    if(!willMatch(Token.Type.RPAR)){
	        ExpressionList expList = parseExpressionList();
	        match(Token.Type.RPAR);
		return new FunctionCall(ident, expList);
	    } else {
		match(Token.Type.RPAR);
		return new FunctionCall(ident, new ExpressionList(new ArrayList<>()));
	    }
	} else {
	    Identifier ident = new Identifier(identToken);
	    return ident;
	}
    }

    // NumValue -> NUM
    private Expression parseNumValue(){
	Token numToken = match(Token.Type.NUM);
	return new NumValue(numToken);
    }
    
}
