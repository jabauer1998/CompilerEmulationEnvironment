package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.Delay;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;

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
     * Below is the code for dealing with parsing Module Declarations
     * @author Jacob Bauer
     */

    //ModuleDeclaration -> MODULE IDENT ( IdentifierList ) ; ModItemList ENDMODULE
    private ModuleDeclaration parseModuleDeclaration(){
	match(Token.Type.MODULE);
	Identifier ident = parseIdentifier();
	if(willMatch(Token.Type.LPAR)){
	    skip();
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    if(willMatch(Token.Type.ENDMODULE)){
		skip();
		return new ModuleDeclaration(ident, identList);
	    } else {
		ModItemList modList = parseModItemList();
		match(Token.Type.ENDMODULE);
		return new ModuleDeclaration(ident, identList, modList);
	    }
	} else {
	    match(Token.Type.SEMI);
	    if(willMatch(Token.Type.ENDMODULE)){
		skip();
		return new ModuleDeclaration(ident);
	    } else {
		ModItemList modList = parseModItemList();
		match(Token.Type.ENDMODULE);
		return new ModuleDeclaration(ident, modList);
	    }
	}
    }

    /**
     * Below is all of the code for parsing  Module Items. 
     * @author Jacob Bauer
     */

    //ModItem -> Function | Task | IntegerDeclaration | RealDeclaration | OutputDeclaration | InitialDeclaration | AllwaysDeclaration | RegDeclaration | ContinuousAssignment | ModuleInstantiation | GateDeclaration
    private ModItem parseModItem(){
	if(willMatch(Token.Type.FUNCTION)){
	    return parseFunction();
	} else if (willMatch(Token.Type.TASK)){
	    return parseTask();
	} else if(willMatch(Token.Type.INTEGER)){
	    return parseIntegerDeclaration();
	} else if(willMatch(Token.Type.REAL)){
	    return parseRealDeclaration();
	} else if(willMatch(Token.Type.OUTPUT)){
	    return parseOutputDeclaration();
	} else if(willMatch(Token.Type.INITIAL)){
	    return parseInitialStatement();
	} else if (willMatch(Token.Type.ALLWAYS)){
	    return parseAllwaysStatement();
	} else if(willMatch(Token.Type.REG)){
	    return parseRegDeclaration();
	} else if(willMatch(Token.Type.WIRE)){
	    return parseWireDeclaration();
	} else if(willMatch(Token.Type.ASSIGN)){
	    return parseContinuousAssignment();
	} else if (willMatch(Token.Type.IDENT)) {
	    return parseModInstantiation();
	} else {
	    return parseGateDeclaration();
	}
    }

    //ModItemList -> ModItem ModItemListRest | NULL
    //ModItemListRest -> ModItem ModItemListRest | NULL 
    private ModItemList parseModItemList(){
	List<ModItem> modList = new ArrayList<>();
	
	while(!willMatch(Token.Type.ENDMODULE)){
	    modList.add(parseModItem());
	}

	return new ModItemList(modList);
    }
    
    //Function -> TASK IDENT ; DeclarationList Statement ENDTASK
    private ModItem parseFunction(){
	match(Token.Type.FUNCTION);
	Identifier ident = parseIdentifier();
	match(Token.Type.SEMI);
	DeclarationList declList = parseDeclarationList(true);
	Statement stat = parseStatement();
	match(Token.Type.ENDFUNCTION);
	return new Function(ident, declList, stat);
    }

    //Task -> TASK IDENT ; DeclarationList StatementOrNull ENDTASK
    private ModItem parseTask(){
	match(Token.Type.TASK);
	Identifier ident = parseIdentifier();
	match(Token.Type.SEMI);
	DeclarationList declList = parseDeclarationList(false);
	Statement stat = parseStatementOrNull();
	match(Token.Type.ENDTASK);
	return new Task(ident, declList, stat);
    }

    //Declaration -> IntegerDeclaration | WireDeclaration | RealDeclaration | RegDeclaration | OutputDeclaration | InputDeclaration 
    private Declaration parseDeclaration(){
	if(willMatch(Token.Type.INTEGER)){
	    return parseIntegerDeclaration();
	} else if (willMatch(Token.Type.REAL)){
	    return parseRealDeclaration();
	} else if(willMatch(Token.Type.WIRE)){
	    return parseWireDeclaration();
	} else if(willMatch(Token.Type.REG)){
	    return parseRegDeclaration();
	} else if(willMatch(Token.Type.INPUT)){
	    return parseInputDeclaration();
	} else {
	    return parseOutputDeclaration();
	}
    }

    //DeclarationList -> NULL | Declaration DeclarationListRest
    //DeclarationListRest -> Declaration DeclarationListRest | NULL
    private DeclarationList parseDeclarationList(boolean atLeastOne){
	List<Declaration> declList = new ArrayList<>();

	if(atLeastOne){
	    declList.add(parseDeclaration());
	}
	
	while(willMatch(Token.Type.INTEGER) || willMatch(Token.Type.REAL) || willMatch(Token.Type.WIRE) || willMatch(Token.Type.REG) || willMatch(Token.Type.INPUT) || willMatch(Token.Type.OUTPUT)){
	    declList.add(parseDeclaration());
	}
	return new DeclarationList(declList);
    }

    //AllwaysStatement -> Allways Statement
    private ModItem parseAllwaysStatement(){
	match(Token.Type.ALLWAYS);
	Statement stat = parseStatement();
	return new AllwaysStatement(stat);
    }

    //InitialStatement -> Initial Statement
    private ModItem parseInitialStatement(){
	match(Token.Type.INITIAL);
	Statement stat = parseStatement();
	return new InitialStatement(stat);
    }

    //ContinuousAssignment -> ASSIGN AssignmentList ;
    private ModItem parseContinuousAssignment(){
	match(Token.Type.ASSIGN);
	AssignmentList assignList = parseAssignmentList();
	match(Token.Type.SEMI);
	return new ContinuousAssignment(assignList);
    }

    //RegDeclaration -> REG RegValueList ; | REG [ ConstExpression : ConstExpression ] RegValueList ;
    private Declaration parseRegDeclaration(){
	match(Token.Type.REG);
	if(willMatch(Token.Type.LBRACK)){
	    skip();
	    ConstantExpression exp1 = parseConstantExpression();
	    match(Token.Type.COLON);
	    ConstantExpression exp2 = parseConstantExpression();
	    match(Token.Type.RBRACK);
	    RegValueList identList = parseRegValueList();
	    match(Token.Type.SEMI);
	    return new RegVectorDeclaration(exp1, exp2, identList);
	} else {
	    RegValueList identList = parseRegValueList();
	    match(Token.Type.SEMI);
	    return new RegScalarDeclaration(identList);
	}
    }

    //WireDeclaration -> WIRE IdentifierList ; | WIRE [ ConstExpression : ConstExpression ] IdentifierList ;
    private Declaration parseWireDeclaration(){
	match(Token.Type.WIRE);
	if(willMatch(Token.Type.LBRACK)){
	    skip();
	    ConstantExpression exp1 = parseConstantExpression();
	    match(Token.Type.COLON);
	    ConstantExpression exp2 = parseConstantExpression();
	    match(Token.Type.RBRACK);
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new WireVectorDeclaration(exp1, exp2, identList);
	} else {
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new WireScalarDeclaration(identList);
	}
    }

    //InputDeclaration -> INPUT IdentifierList ; | INPUT [ ConstExpression : ConstExpression ] IdentifierList ;
     private Declaration parseInputDeclaration(){
	match(Token.Type.INPUT);
	if(willMatch(Token.Type.LBRACK)){
	    skip();
	    ConstantExpression exp1 = parseConstantExpression();
	    match(Token.Type.COLON);
	    ConstantExpression exp2 = parseConstantExpression();
	    match(Token.Type.RBRACK);
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new InputVectorDeclaration(exp1, exp2, identList);
	} else {
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new InputScalarDeclaration(identList);
	}
    }
    //OutputDeclaration -> OUTPUT IdentifierList ; | OUTPUT [ ConstExpression : ConstExpression ] IdentifierList ;
     private Declaration parseOutputDeclaration(){
	match(Token.Type.OUTPUT);
	if(willMatch(Token.Type.LBRACK)){
	    skip();
	    ConstantExpression exp1 = parseConstantExpression();
	    match(Token.Type.COLON);
	    ConstantExpression exp2 = parseConstantExpression();
	    match(Token.Type.RBRACK);
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new OutputVectorDeclaration(exp1, exp2, identList);
	} else {
	    IdentifierList identList = parseIdentifierList();
	    match(Token.Type.SEMI);
	    return new OutputScalarDeclaration(identList);
	}
    }

    //RealDeclaration -> REAL IdentifierList ;
    private Declaration parseRealDeclaration(){
	match(Token.Type.REAL);
	IdentifierList identList = parseIdentifierList();
	match(Token.Type.SEMI);
	return new RealDeclaration(identList);
    }

    //IntegerDeclaration -> INTEGER IdentifierList ;
    private Declaration parseIntegerDeclaration(){
	match(Token.Type.INTEGER);
	RegValueList identList = parseRegValueList();
	match(Token.Type.SEMI);
	return new IntegerDeclaration(identList);
    }
    
    // GateDeclaration -> GATYPE ( ExpressionList );
    private ModItem parseGateDeclaration(){
	if(willMatch(Token.Type.ORGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new OrGateDeclaration(expList);
	} else if(willMatch(Token.Type.ANDGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new AndGateDeclaration(expList);
	} else if(willMatch(Token.Type.NANDGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new NandGateDeclaration(expList);
	} else if(willMatch(Token.Type.NORGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new NorGateDeclaration(expList);
	} else if(willMatch(Token.Type.XORGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new XorGateDeclaration(expList);
	} else if(willMatch(Token.Type.XNORGATE)){
	    skip();
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new XnorGateDeclaration(expList);
	} else {
	    match(Token.Type.NOTGATE);
	    match(Token.Type.LPAR);
	    ExpressionList expList = parseExpressionList();
	    match(Token.Type.RPAR);
	    match(Token.Type.SEMI);
	    return new NotGateDeclaration(expList);
	}
    }

    //ModInstantiation -> IDENT ModuleInstanceList
    private ModItem parseModInstantiation(){
	Identifier ident = parseIdentifier();
	ModInstanceList modList = parseModInstanceList();
	match(Token.Type.SEMI);
	return new ModInstantiation(ident, modList);
    }

    //ModInstanceList -> ModInstance ModInstanceListRest
    //ModInstance -> , ModInstance ModInstanceListRest | null 
    private ModInstanceList parseModInstanceList(){
	List<ModInstance> modList = new ArrayList<>();
	ModInstance inst = parseModInstance();
	modList.add(inst);
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    inst = parseModInstance();
	    modList.add(inst);
	}
	return new ModInstanceList(modList);
    }

    //ModInstance -> IDENT ( ExpressionList )
    private ModInstance parseModInstance(){
	Identifier ident = parseIdentifier();
	match(Token.Type.LPAR);
	ExpressionList expList;
	if(willMatch(Token.Type.DOT)){
	    expList = parsePortConnectionList();
	} else {
	    expList = parseExpressionOrNullList();
	}
	match(Token.Type.RPAR);
	return new ModInstance(ident, expList);
    }
    

    /**
     * Below is the code for parsing statements aswell as CaseItems
     * @author Jacob Bauer
     */

    // Statement -> IfStatement | CaseXStatement | CaseStatement | CaseZStatement | ForeverStatement | RepeatStatement | WhileStatement | ForStatement | WaitStatement | SeqBlock | NonBlockAssign | ContinuousAssign | BlockAssign | NONBlockAssign | TaskCall
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
	    return parseAssignment();
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
	} else if (willMatch(Token.Type.LCURL)){
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
	} else { //lvalue or task_enable
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
	} 
    }

    //StatementOrNull -> {Statement | NULL} ;
    private Statement parseStatementOrNull(){
	if(willMatch(Token.Type.SEMI)){
	    Token sem = skip();
	    return new EmptyStatement(sem.getPosition());
	} else {
	    return parseStatement();
	}
    }
    //StatementList -> Statement StatementList | NULL
    private StatementList parseStatementList(){
	List<Statement> statList = new ArrayList<>();
	
	while(!willMatch(Token.Type.END)){
	    Statement stat = parseStatement();
	    statList.add(stat);
	}
	
	return new StatementList(statList);
    }

    //CaseItemList -> CaseItemList CaseItem 
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

    //CaseItem -> DEFAULT : Statement | DEFAULT Statement | ExpressionList : Statement
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

    //IfStatement -> IF ( expression ) StatementOrNull
    //IfElseStatement -> IF ( expression ) StatementOrNull ELSE StatementOrNull 
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

    //ForStatement -> FOR ( Assignment ; Expression ; Assignment ) Statement
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

    //Assignment -> LValue = Expression
    private Assignment parseAssignment(){
	Expression exp = parseLValue();
	match(Token.Type.EQ1);
	Expression exp1 = parseExpression();
	return new Assignment(exp, exp1);
    }

    //AssignmentList -> Assignment AssignmentListRest
    //AssignmentListRest -> , Assignment AssignmentListRest | NULL 
    private AssignmentList parseAssignmentList(){
	List<Assignment> assignList = new ArrayList();
	assignList.add(parseAssignment());
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    assignList.add(parseAssignment());
	}

	return new AssignmentList(assignList);
    }

    //CaseStatement -> CASE ( Expression ) CaseItemList ENDCASE
    private Statement parseCaseStatement(){
	match(Token.Type.CASE);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	willMatch(Token.Type.ENDCASE); 
	return new CaseStatement(exp, caseList);
    }

    //CaseZStatement -> CASEZ ( Expression ) CaseItemList ENDCASE
    private Statement parseCaseZStatement(){
	match(Token.Type.CASEZ);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	match(Token.Type.ENDCASE);
	return new CaseZStatement(exp, caseList);
    }

    //CaseXStatement -> CASEX ( Expression ) CaseItemList ENDCASE
    private Statement parseCaseXStatement(){
	match(Token.Type.CASEZ);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	CaseItemList caseList = parseCaseItemList();
	match(Token.Type.ENDCASE);
	return new CaseXStatement(exp, caseList);
    }

    //ForeverStatement -> FOREVER Statement
    private Statement parseForeverStatement(){
	match(Token.Type.FOREVER);
	Statement stat = parseStatement();
	return new ForeverStatement(stat);
    }

    //RepeatStatement -> REPEAT Statement
    private Statement parseRepeatStatement(){
	match(Token.Type.REPEAT);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatement();
	return new RepeatStatement(exp, stat);
    }

    //WhileStatement -> WHILE Statement
    private Statement parseWhileStatement(){
	match(Token.Type.WHILE);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatement();
	return new WhileStatement(exp, stat);
    }

    //WaitStatement -> WAIT Statement
    private Statement parseWaitStatement(){
	match(Token.Type.WAIT);
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	Statement stat = parseStatementOrNull();
	return new WaitStatement(exp, stat);
    }

    //SeqBlock -> BEGIN StatementList END
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

    //ExpressionOrNull -> Expression | NULL (ex: a, b, ,d)
    //This is mainly used for Module Items
    private Expression parseExpressionOrNull(){
	if(willMatch(Token.Type.COMMA)){
	    Token comma = peek();
	    return new EmptyExpression(comma.getPosition());
	} else {
	    return parseExpression();
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

    //RegValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
    private Expression parseRegValue(){
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
    
    //RegValueList -> RegValue RegValueRest
    //RegValueRest -> , RegValue RegValueRest | NULL
    private RegValueList parseRegValueList(){
	List<Expression> expList = new ArrayList<>();
	
	expList.add(parseRegValue());
	
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    Expression exp = parseRegValue();
	    expList.add(exp);
	}
	
	return new RegValueList(expList);
    }

    // ConstantExpression -> expression
    private ConstantExpression parseConstantExpression(){
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

    //ExpressionOrNullList -> ExpressionOrNull ExpressionOrNullListRest
    //ExpressionOrNullListRest -> , ExpressionOrNull ExpressionOrNullListRest
    private ExpressionList parseExpressionOrNullList(){
	List<Expression> expList = new ArrayList<>();

	Expression exp = parseExpressionOrNull();
	expList.add(exp);
	
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    exp = parseExpressionOrNull();
	    expList.add(exp);
	}
	
	return new ExpressionList(expList);
    }
    //PortConnectionList -> PortConnection PortConnectionListRest
    //PortConnectionListRest -> , PortConenction
    private ExpressionList parsePortConnectionList(){
	List<Expression> expList = new ArrayList<>();

	Expression exp = parsePortConnection();
	expList.add(exp);
	
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    exp = parsePortConnection();
	    expList.add(exp);
	}
	
	return new ExpressionList(expList);
    }
    //PortConnection -> . IDENT ( Expression )
    private Expression parsePortConnection(){
	match(Token.Type.DOT);
	Identifier ident = parseIdentifier();
	match(Token.Type.LPAR);
	Expression exp = parseExpression();
	match(Token.Type.RPAR);
	return new PortConnection(ident, exp);
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

    //Identifier -> IDENT
    private Identifier parseIdentifier(Token identToken){
	return new Identifier(identToken);
    }

    //Identifier -> IDENT
    private Identifier parseIdentifier(){
	Token ident = match(Token.Type.IDENT);
	return new Identifier(ident);
    }

    //IdentifierList -> Identifier IdentifierListRest
    //IdentifierListRest -> , Identifier IdentifierListRest | NULL
    private IdentifierList parseIdentifierList(){
	List<Identifier> identList = new ArrayList<>();
	
	identList.add(parseIdentifier());
	while(willMatch(Token.Type.COMMA)){
	    skip();
	    identList.add(parseIdentifier());
	}

	return new IdentifierList(identList);
    }

    // NumValue -> NUM
    private NumValue parseNumValue(){
	Token numToken = match(Token.Type.NUM);
	return new NumValue(numToken);
    }
    
}
