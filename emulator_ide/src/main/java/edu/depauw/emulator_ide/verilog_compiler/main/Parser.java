package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.Delay;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;

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
     * Below I cam creating the statement classes
     * @author Jacob Bauer
     */

    public Statement parseStatement(){
	if(willMatch(Token.Type.IF)){
	    return parseIfStatement();
	} else if(willMatch(Token.Type.CASE)){
	    return parseCaseStatement();
	} else if(willMatch(Token.Type.CASEZ)){
	    return parseCaseZStatement();
	} else if(willMatch(Token.Type.CASEX)){
	    return parseCaseXStatement();
	} else if(willMatch(Token.Type.FOREVER)){
	    return parseForeverStatement();
	} else if(willMatch(Token.Type.REPEAT)){
	    return parseRepeatStatement();
	} else if(willMatch(Token.Type.WHILE)){
	    return parseWhileStatement();
	} else if(willMatch(Token.Type.FOR)){
	    return parseForStatement();
	} else if(willMatch(Token.Type.WAIT)){
	    return parseWaitStatement();
	} else if(willMatch(Token.Type.BEGIN)){
	    return parseSeqBlock();
	} else if(willMatch(Token.Type.ASSIGN)){
	    skip();
	    Expression exp1 = parseLValue();
	    match(Token.Type.EQ1);
	    Expression exp2 = parseExpression();
	    match(Token.Type.SEMI);
	    return new ContinuousAssign(exp1, exp2);
	} else if(willMatch(Token.Type.IDENT)){ //lvalue or task_enable
	    Identifier ident = parseIdentifier();
	    if(willMatch(Token.Type.LPAR)){
		skip();
		if(willMatch(Token.Type.RPAR)){
		    skip();
		    match(Token.Type.SEMI);
		    return new TaskStatement(ident);
		} else {
		    ExpressionList expList = parseExpressionList();
		    match(Token.Type.RPAR);
		    match(Token.Type.SEMI);
		    return new TaskStatement(ident, expList);
		}
	    } else if (willMatch(Token.Type.SEMI)){
		skip();
		return new TaskStatement(ident);
	    } else if (willMatch(Token.Type.LBRACK)){ //It must be an assignment
		skip();
		Expression exp1 = parseExpression();
		if(willMatch(Token.Type.RBRACK)){
		    Expression vec = new Vector(ident, exp1);
		     if (willMatch(Token.Type.EQ1)){ // it is a blocking assignment
			skip();
			Expression exp = parseExpression();
			match(Token.Type.SEMI);
			return new BlockAssign(vec, exp);
		    } else { //it is a non blocking assignment
			match(Token.Type.LE);
			Expression exp = parseExpression();
			match(Token.Type.SEMI);
			return new NonBlockAssign(vec, exp);
		    }
		} else {
		    match(Token.Type.COLON);
		    Expression exp2 = parseExpression();
		    match(Token.Type.RBRACK);
		    Expression vec = new Vector(ident, exp1, exp2);
		    if (willMatch(Token.Type.EQ1)){ // it is a blocking assignment
			skip();
			Expression exp = parseExpression();
			match(Token.Type.SEMI);
			return new BlockAssign(vec, exp);
		    } else { //it is a non blocking assignment
			match(Token.Type.LE);
			Expression exp = parseExpression();
			match(Token.Type.SEMI);
			return new NonBlockAssign(vec, exp);
		    }
		}
	    } else if (willMatch(Token.Type.EQ1)){ // it is a blocking assignment
		skip();
		Expression exp = parseExpression();
		match(Token.Type.SEMI);
		return new BlockAssign(ident, exp);
	    } else { //it is a non blocking assignment
		match(Token.Type.LE);
		Expression exp = parseExpression();
		match(Token.Type.SEMI);
		return new NonBlockAssign(ident, exp);
	    }
	} else if (willMatch(Token.Type.DOLLAR)){ //system tasks
	    skip();
	    Identifier ident = parseIdentifier();
	    if(willMatch(Token.Type.SEMI)){
		skip();
		return new TaskStatement(ident);
	    } else {
		match(Token.Type.LPAR);
		if(willMatch(Token.Type.RPAR)){
		    skip();
		    match(Token.Type.SEMI);
		    return new TaskStatement(ident);
		} else {
		    ExpressionList expList = parseExpressionList();
		    match(Token.Type.RPAR);
		    match(Token.Type.SEMI);
		    return new TaskStatement(ident, expList);
		}
	    }
	} else {
	    Expression concat = parseConcatenation();
	    if (willMatch(Token.Type.EQ1)){ // it is a blocking assignment
		skip();
		Expression exp = parseExpression();
		match(Token.Type.SEMI);
		return new BlockAssign(concat, exp);
	    } else { //it is a non blocking assignment
		match(Token.Type.LE);
		Expression exp = parseExpression();
		match(Token.Type.SEMI);
		return new NonBlockAssign(concat, exp);
	    }
	}
    }

    private Statement parseStatementOrNull(){
	if(willMatch(Token.Type.SEMI)){
	    Token sem = skip();
	    return new EmptyStatement(sem.getPosition());
	} else {
	    return parseStatement();
	}
    }

    private StatementList parseStatementList(){
	List<Statement> statList = new ArrayList<>();
	
	while(!willMatch(Token.Type.END)){
	    Statement stat = parseStatement();
	    statList.add(stat);
	}
	
	return new StatementList(statList);
    }

    private CaseItemList parseCaseItemList(){
	List<CaseItem> caseList = new ArrayList<>();
	CaseItem item = parseCaseItem();
	caseList.add(item);
	while(!willMatch(Token.Type.ENDCASE)){
	    item = parseCaseItem();
	    caseList.add(item);
	}
	return new CaseItemList(caseList);
    }

    private CaseItem parseCaseItem(){
	if(willMatch(Token.Type.DEFAULT)){
	    if(willMatch(Token.Type.COLON)){
		skip();
	    }
	    Statement stat = parseStatementOrNull();
	    return new DefCaseItem(stat);
	} else {
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.COLON);
	    Statement stat = parseStatementOrNull();
	    return new ExprCaseItem(expList, stat);
	}
    }

    private Statement parseIfStatement(){
	match(Token.Type.IF);
	match(Token.Type.LPAR);
	Expression expr = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatementOrNull();
        if(willMatch(Token.Type.ELSE)){
	    skip();
	    Statement stat2 = parseStatementOrNull();
	    return new IfElseStatement(expr, stat, stat2);
	} else {
	    return new IfStatement(expr, stat);
	}
    }

    private Statement parseForStatement(){
	match(Token.Type.FOR);
	match(Token.Type.LPAR);
	Assignment init = parseAssignment();
	match(Token.Type.SEMI);
	Expression expr = parseExpression();
	match(Token.Type.SEMI);
	Assignment change = parseAssignment();
	match(Token.Type.RPAR);
	Statement stat = parseStatement();
	return new ForStatement(init, expr, change, stat);
    }

    private Assignment parseAssignment(){
	Expression exp = parseLValue();
	match(Token.Type.EQ1);
	Expression exp1 = parseExpression();
	return new Assignment(exp, exp1);
    }

    private Statement parseCaseStatement(){
	match(Token.Type.CASE);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	willMatch(Token.Type.ENDCASE); 
	return new CaseStatement(exp, caseList);
    }

    private Statement parseCaseZStatement(){
	match(Token.Type.CASEZ);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	match(Token.Type.ENDCASE);
	return new CaseZStatement(exp, caseList);
    }

    private Statement parseCaseXStatement(){
	match(Token.Type.CASEZ);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	match(Token.Type.ENDCASE);
	return new CaseXStatement(exp, caseList);
    }

    private Statement parseForeverStatement(){
	match(Token.Type.FOREVER);
	Statement stat = parseStatement();
	return new ForeverStatement(stat);
    }

    private Statement parseRepeatStatement(){
	match(Token.Type.REPEAT);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatement();
	return new RepeatStatement(exp, stat);
    }

    private Statement parseWhileStatement(){
	match(Token.Type.WHILE);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatement();
	return new WhileStatement(exp, stat);
    }

    private Statement parseWaitStatement(){
	match(Token.Type.WAIT);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatementOrNull();
	return new WaitStatement(exp, stat);
    }

    private Statement parseSeqBlock(){
	match(Token.Type.BEGIN);
	StatementList statList = parseStatementList();
	match(Token.Type.END);
	return new SeqBlockStatement(statList);
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

    // lvalue -> IDENT | IDENT [ Expression ] | IDENT [ Expression : Expression ] | Concatenation
    private Expression parseLValue(){
	if(willMatch(Token.Type.LCURL)){
	    return parseConcatenation();
	} else {
	    Token ident = match(Token.Type.IDENT);
	    if(willMatch(Token.Type.LBRACK)){
		skip();
		Expression exp = parseExpression();
		if(willMatch(Token.Type.RBRACK)){
		    skip();
		    return new Vector(new Identifier(ident), exp);
		} else {
		    match(Token.Type.COLON);
		    Expression exp2 = parseExpression();
		    match(Token.Type.RBRACK);
		    return new Vector(new Identifier(ident), exp, exp2);
		}
	    } else {
		return new Identifier(ident);
	    }
	}
    }

    // ConstantExpression -> expression
    private Expression parseConstantExpression(){
	Expression constant = parseExpression();
	return new ConstantExpression(constant);
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
	    Token identToken = skip();
	    if(willMatch(Token.Type.LBRACK)){
		skip();
		Identifier ident = parseIdentifier(identToken);
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
		Identifier ident = parseIdentifier(identToken);
		if(!willMatch(Token.Type.RPAR)){
		    ExpressionList expList = parseExpressionList();
		    match(Token.Type.RPAR);
		    return new FunctionCall(ident, expList);
		} else {
		    match(Token.Type.RPAR);
		    return new FunctionCall(ident);
		}
	    } else {
		Identifier ident = new Identifier(identToken);
		return ident;
	    }
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
    private Expression  parseConcatenation(){
	match(Token.Type.LCURL);
	ExpressionList expList = parseExpressionList();
	match(Token.Type.RCURL);
	return new Concatenation(expList);
    }

    //Identifier -> IDENT | IDENT [ Expression : Expression ] | IDENT [ Expression ] | IDENT ( ExpressionList ) | IDENT ()
    private Identifier parseIdentifier(Token identToken){
	return new Identifier(identToken);
    }
    
    private Identifier parseIdentifier(){
	Token ident = match(Token.Type.IDENT);
	return new Identifier(ident);
    }

    // NumValue -> NUM
    private NumValue parseNumValue(){
	Token numToken = match(Token.Type.NUM);
	return new NumValue(numToken);
    }
    
}
