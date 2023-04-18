package io.github.H20man13.emulator_ide.verilog_interpreter.parser;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.VerilogFile;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.ConstantExpression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.EmptyExpression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.PortConnection;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.FunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.BinaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.Concatenation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.TernaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.UnaryOperation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation.BinaryOperation.Operator;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.BinaryNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.DecimalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.HexadecimalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.OctalNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.value_node.StringNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.LValue;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ContinuousAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.AndGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NandGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.NotGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.OrGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.XnorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.gate_declaration.XorGateDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstance;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.instantiation.ModuleInstantiation;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process.AllwaysProcess;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process.InitialProcess;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Int;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Output;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Real;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.RegValueList;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Unidentified;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Wire;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.EmptyStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.SeqBlockStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.WaitStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseXStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.CaseZStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.CaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.DefCaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.ExprCaseItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.NonBlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.ForeverStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.RepeatStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching.WhileStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfElseStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.branching._if_.IfStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.TaskStatement;

public class Parser {

	private final List<Token>               lexedTokens;
	private final ErrorLog                  errorLog;

	/**
	 * This is the consturctor to the parser class
	 * 
	 * @param tokenArray array of token objects passed into this by the lexer
	 * @param errorLog   errorLog to print error messages
	 */

	public Parser(List<Token> tokens, ErrorLog errorLog) {

		this.lexedTokens = Lexer.filterWhiteSpace(tokens);
		
		this.errorLog = errorLog;
	}

	private Position getStart(){
		Token tok = peek();
		return tok.getPosition();
	}

	private boolean willMatch(Token.Type... types){
		for(int i = 0; i < types.length; i++){
			if(willMatch(types[i])){
				return true;
			}
		}
		return false;
	}

	private boolean willMatch(Token.Type type){
		if (lexedTokens.isEmpty()) return false;

		return lexedTokens.get(0).getTokenType() == type;
	}

	private void errorAndExit(String message){
		errorAndExit(message, null);
	}

	private void errorAndExit(String message, Position position){
		errorLog.addItem(new ErrorItem(message, position));
		errorLog.printLog();
		System.exit(1);
	}

	private Token skip(){
		if (!lexedTokens.isEmpty()) return lexedTokens.remove(0);

		errorAndExit("Unexpected end of file while skipping token");
		return null;
	}

	private boolean skipIfYummy(Token.Type type){
		if (!willMatch(type)) return false;

		skip();
		return true;
	}

	private Token peek(){
		if (!lexedTokens.isEmpty()) return lexedTokens.get(0);

		errorAndExit("Unexpected end of file while peeking at token");
		return null;
	}

	private enum STRATEGY{REPAIR, SKIP, EXIT}

	private Token match(Token.Type type){
		return match(type, STRATEGY.EXIT);
	}

	private Token match(Token.Type type, STRATEGY strategy){

		if (lexedTokens.isEmpty()) { // if token isnt matched then
			errorAndExit("Unexpected end of file while matching " + type);
			return null;
		} else if (willMatch(type)) { // return the token if it is matched
			return skip();
		} else {
			Token matched = peek();

			ErrorItem errorItem = new ErrorItem("Token of type " + type + " expected but token of type " + matched.getTokenType() + " found ", matched.getPosition());
			errorLog.addItem(errorItem);
			//Depending on the error strategy exit the program, skip tokens, or repair a token
			if(strategy == STRATEGY.SKIP){
				while(!willMatch(type, Token.Type.EOF))
					skip();
				return null;
			} else if(strategy == STRATEGY.EXIT){
				errorLog.printLog();
				System.exit(1);
				return null;
			} else {
				return null;
			}
		}

	}

	public VerilogFile parseVerilogFile(){
		Position start = getStart();
		List<ModuleDeclaration> moduleList = new ArrayList<>();
		do{
			ModuleDeclaration moduleDeclaration = parseModuleDeclaration();
			moduleList.add(moduleDeclaration);
		} while(willMatch(Token.Type.MODULE));

		return new VerilogFile(start, moduleList);
	}

	/**
	 * Below is the code for dealing with parsing Module Declarations
	 * 
	 * @author Jacob Bauer
	 */

	// ModuleDeclaration -> MODULE IDENT ( ModuleDeclarationList ) ; ModItemList ENDMODULE
	public ModuleDeclaration parseModuleDeclaration(){
		Position start = getStart();
		match(Token.Type.MODULE);
		
		Token modTok = match(Token.Type.IDENT);
		String moduleName = modTok.getLexeme();

		List<ModuleItem> moduleItemList = new ArrayList<>();

		if(skipIfYummy(Token.Type.LPAR) && !willMatch(Token.Type.RPAR)){
			moduleItemList = parseModuleParDeclarationList();
			match(Token.Type.RPAR);
		} else if(willMatch(Token.Type.RPAR)){
			skip();
		}

		match(Token.Type.SEMI, STRATEGY.REPAIR);

		if(!willMatch(Token.Type.ENDMODULE)){
			List<ModuleItem> modList = parseModuleItemList();
			moduleItemList.addAll(modList);
		}

		match(Token.Type.ENDMODULE);


		return new ModuleDeclaration(start, moduleName, moduleItemList);

	}

	/**
	 * Below is all of the code for parsing Module Items.
	 * 
	 * @author Jacob Bauer
	 */

	// ModuleDeclarationList -> ModuleParam ModuleDeclarationListRest
	// ModuleDeclarationListRest -> , ModuleParam ModuleDeclarationListRest | NULL
	private List<ModuleItem> parseModuleParDeclarationList(){
		ArrayList<ModuleItem> declList = new ArrayList<>();

		do {
			ModuleItem decl = parseModuleParDeclaration();
			declList.add(decl);
		} while(skipIfYummy(Token.Type.COMMA));

		return declList;
	}

	// ModuleDeclaration -> OutputDeclaration | InputDeclaration | OutputWireDeclaration |
	// OutputRegDeclaration | InputWireDeclaration
	private ModuleItem parseModuleParDeclaration(){
		Position start = getStart();

		if (skipIfYummy(Token.Type.INPUT)) {
			skipIfYummy(Token.Type.WIRE);
			if (skipIfYummy(Token.Type.LBRACK)) {
				ConstantExpression exp1 = parseConstantExpression();
				match(Token.Type.COLON, STRATEGY.REPAIR);
				ConstantExpression exp2 = parseConstantExpression();
				match(Token.Type.RBRACK, STRATEGY.REPAIR);

				Input.Wire.Vector vector = new Input().new Wire().new Vector(exp1, exp2);
				String variableName = parseRawIdentifier(); //fetch name to declare

				Input.Wire.Vector.Ident item = vector.new Ident(start, variableName);

				return item;
			} else {
				String declarationIdent = parseRawIdentifier();
				Input.Wire.Scalar.Ident scalar = new Input().new Wire().new Scalar().new Ident(start, declarationIdent);

				return scalar;
			}

		} else if (skipIfYummy(Token.Type.OUTPUT)) {
			if (skipIfYummy(Token.Type.REG)) {	
				if (skipIfYummy(Token.Type.LBRACK)) {

					ConstantExpression exp1 = parseConstantExpression();
					match(Token.Type.COLON, STRATEGY.REPAIR);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK, STRATEGY.REPAIR);


					Output.Reg.Vector vector = new Output().new Reg().new Vector(exp1, exp2);
					String variableName = parseRawIdentifier(); //fetch name to declare

					Output.Reg.Vector.Ident item = vector.new Ident(start, variableName);

					return item;
				} else {
					String declarationIdent = parseRawIdentifier();
					Output.Reg.Scalar.Ident scalar = new Output().new Reg().new Scalar().new Ident(start, declarationIdent);
					return scalar;
				}

			} else {
				skipIfYummy(Token.Type.WIRE);
				if (skipIfYummy(Token.Type.LBRACK)) {
					ConstantExpression exp1 = parseConstantExpression();
					match(Token.Type.COLON, STRATEGY.REPAIR);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK, STRATEGY.REPAIR);

					Output.Wire.Vector vector = new Output().new Wire().new Vector(exp1, exp2);
					String variableName = parseRawIdentifier(); //fetch name to declare

					Output.Wire.Vector.Ident item = vector.new Ident(start, variableName);

					return item;
				} else {
					String declarationIdent = parseRawIdentifier();
					Output.Wire.Scalar.Ident scalar = new Output().new Wire().new Scalar().new Ident(start, declarationIdent);
					return scalar;
				}
			}
		} else {
			String ident = parseRawIdentifier();
			return new Unidentified().new Declaration(start, ident);
		}

	}

	// ModItem -> Function | Task | IntegerDeclaration | RealDeclaration | OutputDeclaration
	// | InitialDeclaration | AllwaysDeclaration | RegDeclaration | ContinuousAssignment |
	// ModuleInstantiation | GateDeclaration
	public List<ModuleItem> parseModuleItem(){

		LinkedList<ModuleItem> itemsToRet = new LinkedList<ModuleItem>();

		if (willMatch(Token.Type.FUNCTION)) {
			FunctionDeclaration Decl = parseFunctionDeclaration();
			itemsToRet.add(Decl);
		}
		else if (willMatch(Token.Type.TASK)){
			TaskDeclaration  Decl = parseTaskDeclaration();
			itemsToRet.add(Decl);
		}
		else if (willMatch(Token.Type.INTEGER)) {
			List<ModuleItem> Decls =  parseIntegerDeclaration();
			itemsToRet.addAll(Decls);
		}
		else if (willMatch(Token.Type.REAL)) {
			List<ModuleItem> Decls =  parseRealDeclaration();
			itemsToRet.addAll(Decls);
		}
		else if (willMatch(Token.Type.INITIAL)){
			ModuleItem stat = parseInitialStatement();
			itemsToRet.add(stat);
		}
		else if (willMatch(Token.Type.ALLWAYS)) {
			ModuleItem stat = parseAllwaysStatement();
			itemsToRet.add(stat);
		}
		else if (willMatch(Token.Type.REG)) {
			List<ModuleItem> Decls = parseRegDeclaration();
			itemsToRet.addAll(Decls);
		}
		else if (willMatch(Token.Type.WIRE)) {
			List<ModuleItem> Decls = parseWireDeclaration();
			itemsToRet.addAll(Decls);
		}
		else if (willMatch(Token.Type.ASSIGN)){
			ModuleItem assign = parseContinuousAssignment();
			itemsToRet.add(assign);
		}
		else if (willMatch(Token.Type.IDENT)){
			ModuleItem stat =  parseModInstantiation();
			itemsToRet.add(stat);
		}
		else if (skipIfYummy(Token.Type.OUTPUT)) {

			if (willMatch(Token.Type.WIRE)) {
				return parseOutputWireDeclaration();
			} else if (willMatch(Token.Type.REG)) {
				return parseOutputRegDeclaration();
			} else {
				return parseOutputDeclaration();
			}

		} else if (willMatch(Token.Type.INPUT)) {
			
			skip();

			if (willMatch(Token.Type.WIRE)) {
				return parseInputWireDeclaration();
			} else {
				return parseInputDeclaration();
			}

		} else if (willMatch(Token.Type.ANDGATE, Token.Type.ORGATE, Token.Type.NANDGATE, Token.Type.NORGATE, Token.Type.NOTGATE, Token.Type.XNORGATE, Token.Type.XORGATE)){
			ModuleItem geteDecl = parseGateDeclaration();
			itemsToRet.add(geteDecl);
	    } else {
			Token matched = peek();
			errorAndExit("Unexpected ModItem token of type " + matched.getTokenType() + " and lexeme " + matched.getLexeme() + " found", matched.getPosition());
			return null;
		}

		return itemsToRet;

	}

	// ModItemList -> ModItem ModItemListRest | NULL
	// ModItemListRest -> ModItem ModItemListRest | NULL
	private List<ModuleItem> parseModuleItemList(){
		List<ModuleItem> modList = new ArrayList<>();

		while(!willMatch(Token.Type.ENDMODULE)) {
			List<ModuleItem> modItem = parseModuleItem();
			modList.addAll(modItem);
		}

		return modList;
	}

	// Function -> Function FunctionName DeclarationList Statement ENDFUNCTION
	public FunctionDeclaration parseFunctionDeclaration(){
		Position start = getStart();
		match(Token.Type.FUNCTION);
		ModuleItem decl = parseFunctionName();
		match(Token.Type.SEMI);
		List<ModuleItem> declList = parseDeclarationList(true);
		Statement stat = parseStatement();
		match(Token.Type.ENDFUNCTION);
		return new FunctionDeclaration(start, decl, declList, stat);
	}

	// FunctionName -> (REG | REG [ : ] | INTEGER | REAL) IDENT | UNIDENTIFIED
	private ModuleItem parseFunctionName(){
		Position start = getStart();
		if (willMatch(Token.Type.REG)) {
			skip();

			if (willMatch(Token.Type.LBRACK)) {
				skip();
				ConstantExpression exp1 = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression exp2 = parseConstantExpression();
				match(Token.Type.RBRACK);
				Reg.Vector vector = new Reg().new Vector(exp1, exp2);

				String ident = parseRawIdentifier();

				Reg.Vector.Ident rValueIdent = vector.new Ident(start, ident);

				return rValueIdent;
			} else {
				String ident = parseRawIdentifier();
				Reg.Scalar.Ident rValueIdent = new Reg().new Scalar().new Ident(start, ident);
				return rValueIdent;
			}

		} else if (willMatch(Token.Type.INTEGER)) {
			skip();
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Int.Ident rValueIdent = new Int().new Ident(localStart, ident);
			return rValueIdent;
		} else if (willMatch(Token.Type.REAL)) {
			skip();
			String ident = parseRawIdentifier();
			return new Real().new Ident(start, ident);
		} else if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);

			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Reg.Vector vector = new Reg().new Vector(exp1, exp2);
			Reg.Vector.Ident var = vector.new Ident(localStart, ident);
			return var;
		} else {
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Reg.Scalar.Ident var = new Reg().new Scalar().new Ident(localStart, ident);
			return var;
		}

	}

	// Task -> TASK IDENT ; DeclarationList StatementOrNull ENDTASK
	public TaskDeclaration parseTaskDeclaration(){
		Position start = getStart();
		match(Token.Type.TASK);
		String ident = parseRawIdentifier();
		match(Token.Type.SEMI);
		List<ModuleItem> declList = parseDeclarationList(false);
		Statement stat = parseStatementOrNull();
		match(Token.Type.ENDTASK);
		return new TaskDeclaration(start, ident, declList, stat);
	}

	// Declaration -> IntegerDeclaration | WireDeclaration | RealDeclaration |
	// RegDeclaration | OutputDeclaration | InputDeclaration
	private List<ModuleItem> parseDeclaration(){

		if (willMatch(Token.Type.INTEGER)) {
			return parseIntegerDeclaration();
		} else if (willMatch(Token.Type.REAL)) {
			return parseRealDeclaration();
		} else if (willMatch(Token.Type.WIRE)) {
			return parseWireDeclaration();
		} else if (willMatch(Token.Type.REG)) {
			return parseRegDeclaration();
		} else if (willMatch(Token.Type.INPUT)) {
			skip();

			if (willMatch(Token.Type.WIRE)) {
				return parseInputWireDeclaration();
			} else if (willMatch(Token.Type.REG)) {
				return parseInputRegDeclaration();
			} else {
				return parseInputDeclaration();
			}

		} else if (willMatch(Token.Type.OUTPUT)) {
			skip();

			if (willMatch(Token.Type.REG)) {
				return parseOutputRegDeclaration();
			} else if (willMatch(Token.Type.WIRE)) {
				return parseOutputWireDeclaration();
			} else {
				return parseOutputDeclaration();
			}

		} else {
			Token matched = peek();
			errorAndExit("Unexpected Declaration token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition());
			return null;
		}

	}

	// DeclarationList -> NULL | Declaration DeclarationListRest
	// DeclarationListRest -> Declaration DeclarationListRest | NULL
	private List<ModuleItem> parseDeclarationList(boolean atLeastOne){
		List<ModuleItem> declList = new ArrayList<>();

		if (atLeastOne) {
			List<ModuleItem> decl = parseDeclaration();
			declList.addAll(decl);
		}

		while(willMatch(Token.Type.INTEGER, Token.Type.REAL, Token.Type.WIRE, Token.Type.REG, Token.Type.INPUT, Token.Type.OUTPUT)) {
			List<ModuleItem> decl = parseDeclaration();
			declList.addAll(decl);
		}

		return declList;
	}

	// AllwaysStatement -> Allways Statement
	private ModuleItem parseAllwaysStatement(){
		Position start = getStart();
		match(Token.Type.ALLWAYS);
		Statement stat = parseStatement();
		return new AllwaysProcess(start, stat);
	}

	// InitialStatement -> Initial Statement
	private ModuleItem parseInitialStatement(){
		Position start = getStart();
		match(Token.Type.INITIAL);
		Statement stat = parseStatement();
		return new InitialProcess(start, stat);
	}

	// ContinuousAssignment -> ASSIGN AssignmentList ;
	private ModuleItem parseContinuousAssignment(){
		Position start = getStart();
		match(Token.Type.ASSIGN);
		List<BlockingAssignment> assignList = parseAssignmentList();
		match(Token.Type.SEMI);
		return new ContinuousAssignment(start, assignList);
	}

	// RegDeclaration -> REG RegValueList ; | REG [ ConstExpression : ConstExpression ] RegValueList ;
	private List<ModuleItem> parseRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseRegVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseRegScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	private List<ModuleItem> parseRegVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Reg.Vector vector = new Reg().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();

			if(willMatch(Token.Type.LBRACK)){
				skip();
				ConstantExpression begin = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression end = parseConstantExpression();
				match(Token.Type.RBRACK);

				Reg.Vector.Array decl = vector.new Array(start, ident, begin, end);
				result.add(decl);

			} else {
				Reg.Vector.Ident decl = vector.new Ident(localStart, ident);
				result.add(decl);
			}
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseRegScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Reg.Scalar scalar = new Reg().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			if(willMatch(Token.Type.LBRACK)){
				skip();
				ConstantExpression begin = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression end = parseConstantExpression();
				match(Token.Type.RBRACK);

				Reg.Scalar.Array decl = scalar.new Array(start, ident, begin, end);
				result.add(decl);

			} else {
				Reg.Scalar.Ident decl = scalar.new Ident(localStart, ident);
				result.add(decl);
			}
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// OutputRegDeclaration -> OUTPUT REG RegValueList ; | OUTPUT REG [ ConstExpression :
	// ConstExpression ] RegValueList ;
	private List<ModuleItem> parseOutputRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseOutputRegVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseOutputRegScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	private List<ModuleItem> parseOutputRegVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Output.Reg.Vector vector = new Output().new Reg().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();

			if(willMatch(Token.Type.LBRACK)){
				skip();
				ConstantExpression begin = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression end = parseConstantExpression();
				match(Token.Type.RBRACK);

				Output.Reg.Vector.Array decl = vector.new Array(start, ident, begin, end);
				result.add(decl);

			} else {
				Output.Reg.Vector.Ident decl = vector.new Ident(localStart, ident);
				result.add(decl);
			}
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseOutputRegScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Output.Reg.Scalar scalar = new Output().new Reg().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			if(willMatch(Token.Type.LBRACK)){
				skip();
				ConstantExpression begin = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression end = parseConstantExpression();
				match(Token.Type.RBRACK);

				Output.Reg.Scalar.Array decl = scalar.new Array(start, ident, begin, end);
				result.add(decl);

			} else {
				Output.Reg.Scalar.Ident decl = scalar.new Ident(localStart, ident);
				result.add(decl);
			}
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// WireDeclaration -> WIRE IdentifierList ; | WIRE [ ConstExpression : ConstExpression ]
	// IdentifierList ;
	private List<ModuleItem> parseWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> regValList = parseWireVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return regValList;
		} else {
			List<ModuleItem> regValList = parseWireScalarDeclarationList();
			match(Token.Type.SEMI);
			return regValList;
		}
	}

	private List<ModuleItem> parseWireVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Wire.Vector vector = new Wire().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Wire.Vector.Ident decl = vector.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseWireScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Wire.Scalar scalar = new Wire().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Wire.Scalar.Ident decl = scalar.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// OutputWireDeclaration -> INPUT WIRE IdentifierList ; | INPUT WIRE [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private List<ModuleItem> parseOutputWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);

			List<ModuleItem> declList = parseOutputWireVectorDeclarationList(exp1, exp2);
			
			match(Token.Type.SEMI);
			return declList;
		} else {
			List<ModuleItem> declList = parseOutputWireScalarDeclarationList();
			match(Token.Type.SEMI);
			return declList;
		}

	}

	private List<ModuleItem> parseOutputWireVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Output.Wire.Vector vector = new Output().new Wire().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Output.Wire.Vector.Ident decl = vector.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseOutputWireScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Output.Wire.Scalar scalar = new Output().new Wire().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Output.Wire.Scalar.Ident decl = scalar.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// InputWireDeclaration -> INPUT WIRE IdentifierList ; | INPUT WIRE [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private List<ModuleItem> parseInputWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseInputWireVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseInputWireScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	private List<ModuleItem> parseInputWireVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Input.Wire.Vector vector = new Input().new Wire().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Input.Wire.Vector.Ident decl = vector.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseInputWireScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Input.Wire.Scalar scalar = new Input().new Wire().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Input.Wire.Scalar.Ident decl = scalar.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}


	// InputRegDeclaration -> INPUT REG IdentifierList ; | INPUT REG [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private List<ModuleItem> parseInputRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseInputRegVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseInputRegScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	private List<ModuleItem> parseInputRegVectorDeclarationList(Expression vectorIndex1, Expression vectorIndex2){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Input.Reg.Vector vector = new Input().new Reg().new Vector(vectorIndex1, vectorIndex2);

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Input.Reg.Vector.Ident decl = vector.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	private List<ModuleItem> parseInputRegScalarDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		Input.Reg.Scalar scalar = new Input().new Reg().new Scalar();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Input.Reg.Scalar.Ident decl = scalar.new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// OutputRegDeclaration -> INPUT IdentifierList ; | INPUT [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private List<ModuleItem> parseInputDeclaration(){
		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseInputWireVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseInputWireScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	// OutputDeclaration -> OUTPUT IdentifierList ; | OUTPUT [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private List<ModuleItem> parseOutputDeclaration(){
		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			List<ModuleItem> identList = parseOutputWireVectorDeclarationList(exp1, exp2);
			match(Token.Type.SEMI);
			return identList;
		} else {
			List<ModuleItem> identList = parseOutputWireScalarDeclarationList();
			match(Token.Type.SEMI);
			return identList;
		}

	}

	// RealDeclaration -> REAL IdentifierList ;
	private List<ModuleItem> parseRealDeclaration(){
		match(Token.Type.REAL);
		List<ModuleItem> identList = parseRealDeclarationList();
		match(Token.Type.SEMI);
		return identList;
	}

	private List<ModuleItem> parseRealDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			Real.Ident decl = new Real().new Ident(localStart, ident);
			result.add(decl);
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// IntegerDeclaration -> INTEGER IdentifierList ;
	private List<ModuleItem> parseIntegerDeclaration(){
		match(Token.Type.INTEGER);
		List<ModuleItem> identList = parseIntegerDeclarationList();
		match(Token.Type.SEMI);
		return identList;
	}

	private List<ModuleItem> parseIntegerDeclarationList(){
		Position start = getStart();

		List<ModuleItem> result = new ArrayList<>();

		do{
			Position localStart = getStart();
			String ident = parseRawIdentifier();
			if(willMatch(Token.Type.LBRACK)){
				skip();
				Expression exp1 = parseConstantExpression();
				match(Token.Type.COLON);
				Expression exp2 = parseConstantExpression();
				match(Token.Type.RBRACK);

				Int.Array decl = new Int().new Array(localStart, ident, exp1, exp2);
				result.add(decl);
			} else {
				Int.Ident decl = new Int().new Ident(localStart, ident);
				result.add(decl);
			}
		} while (skipIfYummy(Token.Type.COMMA));

		return result;
	}

	// GateDeclaration -> GATYPE ( ExpressionList );
	private ModuleItem parseGateDeclaration(){

		Position start = getStart();

		if (willMatch(Token.Type.ORGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new OrGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.ANDGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new AndGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.NANDGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NandGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.NORGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NorGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.XORGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new XorGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.XNORGATE)) {
			skip();
			match(Token.Type.LPAR);
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new XnorGateDeclaration(start, expList);
		} else if (willMatch(Token.Type.NOTGATE)) {
			match(Token.Type.NOTGATE);
			match(Token.Type.LPAR);
			Expression exp = parseExpression();
			List<Expression> expressions = new LinkedList<Expression>();
			expressions.add(exp);
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NotGateDeclaration(start, expressions);
		} else {
			Token matched = peek();
			errorLog.addItem(new ErrorItem("Unexpected GateDeclaration token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition()));
			errorLog.printLog();
			System.exit(1);
			return null;
		}

	}

	// ModInstantiation -> IDENT ModuleInstanceList
	private ModuleItem parseModInstantiation(){
		Position start = getStart();
		String ident = parseRawIdentifier();
		List<ModuleInstance> modList = parseModInstanceList();
		match(Token.Type.SEMI);
		return new ModuleInstantiation(start, ident, modList);
	}

	// ModInstanceList -> ModInstance ModInstanceListRest
	// ModInstance -> , ModInstance ModInstanceListRest | null
	private List<ModuleInstance> parseModInstanceList(){
		List<ModuleInstance> modList = new ArrayList<>();

		do{
			ModuleInstance inst = parseModInstance();
			modList.add(inst);
		} while(skipIfYummy(Token.Type.COMMA));

		return modList;
	}

	// ModInstance -> IDENT ( ExpressionList )
	private ModuleInstance parseModInstance(){
		Position start = getStart();
		String ident = parseRawIdentifier();
		match(Token.Type.LPAR);
		List<Expression> expList;

		if (willMatch(Token.Type.DOT)) {
			expList = parsePortConnectionList();
		} else {
			expList = parseExpressionOrNullList();
		}

		match(Token.Type.RPAR);
		return new ModuleInstance(start, ident, expList);
	}

	/**
	 * Below is the code for parsing statements aswell as CaseItems
	 * 
	 * @author Jacob Bauer
	 */

	// Statement -> IfStatement | CaseXStatement | CaseStatement | CaseZStatement |
	// ForeverStatement | RepeatStatement | WhileStatement | ForStatement | WaitStatement |
	// SeqBlock | NonBlockAssign | ContinuousAssign | BlockAssign | NONBlockAssign |
	// TaskCall
	public Statement parseStatement(){
		Position start = getStart();
		if (willMatch(Token.Type.IF)) return parseIfStatement();
		else if (willMatch(Token.Type.CASE)) return parseCaseStatement();
		else if (willMatch(Token.Type.CASEZ)) return parseCaseZStatement();
		else if (willMatch(Token.Type.CASEX)) return parseCaseXStatement();
		else if (willMatch(Token.Type.FOREVER)) return parseForeverStatement();
		else if (willMatch(Token.Type.REPEAT)) return parseRepeatStatement();
		else if (willMatch(Token.Type.WHILE)) return parseWhileStatement();
		else if (willMatch(Token.Type.FOR)) return parseForStatement();
		else if (willMatch(Token.Type.WAIT)) return parseWaitStatement();
		else if (willMatch(Token.Type.BEGIN)) return parseSeqBlock();
		else if (willMatch(Token.Type.ASSIGN)) {
			skip();
			Statement stat = parseAssignment();
			match(Token.Type.SEMI);
			return stat;
		} else if (willMatch(Token.Type.DOLLAR)) { // system tasks
			skip();
			String ident = parseRawIdentifier();

			if (willMatch(Token.Type.SEMI)) {
				skip();
				return new SystemTaskStatement(start, ident, new ArrayList<>());
			} else {
				match(Token.Type.LPAR);

				if (willMatch(Token.Type.RPAR)) {
					skip();
					match(Token.Type.SEMI);
					return new SystemTaskStatement(start, ident, new ArrayList<>());
				} else {
					List<Expression> expList = parseExpressionList();
					match(Token.Type.RPAR);
					match(Token.Type.SEMI);
					return new SystemTaskStatement(start, ident, expList);
				}

			}

		} else if (willMatch(Token.Type.LCURL)) {
			LValue concat = parseConcatenation();
			if (willMatch(Token.Type.LE)) { // it is a blocking assignment
				return parseNonBlockingAssignment(start, concat);
			} else { // it is a non blocking assignment
				return parseBlockingAssignment(start, concat);
			}

		} else { // lvalue or task_enable
			String ident = parseRawIdentifier();

			if (willMatch(Token.Type.LPAR)) {
				skip();

				if (willMatch(Token.Type.RPAR)) {
					skip();
					match(Token.Type.SEMI);
					return new TaskStatement(start, ident, new ArrayList<>());
				} else {
					List<Expression> expList = parseExpressionList();
					match(Token.Type.RPAR);
					match(Token.Type.SEMI);
					return new TaskStatement(start, ident, expList);
				}

			} else if (skipIfYummy(Token.Type.SEMI)) {
				return new TaskStatement(start, ident, new ArrayList<>());
			} else if (willMatch(Token.Type.LBRACK)) { // It must be an assignment
				skip();
				Expression exp1 = parseExpression();

				if (willMatch(Token.Type.RBRACK)) {
					skip();
					LValue vec = new Element(start, ident, exp1);

					if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
						return parseBlockingAssignment(start, vec);
					} else { // it is a non blocking assignment
						return parseNonBlockingAssignment(start, vec);
					}

				} else {
					match(Token.Type.COLON);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					ConstantExpression cexp1 = new ConstantExpression(start, exp1);
					LValue vec = new Slice(start, ident, cexp1, exp2);

					if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
						return parseBlockingAssignment(start, vec);
					} else { // it is a non blocking assignment
						return parseNonBlockingAssignment(start, vec);
					}

				}

			} else if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
				LValue lvalue = new Identifier(start, ident);
				return parseBlockingAssignment(start,lvalue);
			} else if (willMatch(Token.Type.LE)) { // it is a non blocking assignment
				LValue lvalue = new Identifier(start, ident);
				return parseNonBlockingAssignment(start, lvalue);
			} else {
				Token matched = peek();
				errorAndExit("Unexpected Statement token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition());
				return null;
			}

		}

	}


	//NonBlockingAssignment -> LValue <= Expression; NonBlockingAssignment | NULL
	private NonBlockingAssignment parseNonBlockingAssignment(Position start, LValue value){
		List<LValue> lValues = new ArrayList<LValue>();
		List<Expression> expressions = new ArrayList<Expression>();
		match(Token.Type.LE);
		Expression expression = parseExpression();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		expressions.add(expression);
		lValues.add(value);
		
		while(willMatch(Token.Type.IDENT, Token.Type.LCURL)){
			LValue lvalue = parseLValue();
			match(Token.Type.LE);
			expression = parseExpression();
			match(Token.Type.SEMI);
			lValues.add(lvalue);
			expressions.add(expression);
		}

		return new NonBlockingAssignment(start, lValues, expressions);
	}

	//NonBlockingAssignment -> LValue <= Expression; NonBlockingAssignment | NULL
	private BlockingAssignment parseBlockingAssignment(Position start, LValue value){
		match(Token.Type.EQ1);
		Expression expression = parseExpression();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		return new BlockingAssignment(start, value, expression);
	}

	// StatementOrNull -> {Statement | NULL} ;
	private Statement parseStatementOrNull(){

		if (willMatch(Token.Type.SEMI)) {
			Token sem = skip();
			return new EmptyStatement(sem.getPosition());
		} else {
			return parseStatement();
		}

	}

	// StatementList -> Statement StatementList | NULL
	private List<Statement> parseStatementList(){
		List<Statement> statList = new ArrayList<>();

		if (!willMatch(Token.Type.END)) {
			do {
				Statement stat = parseStatement();
				statList.add(stat);
			} while(!willMatch(Token.Type.END));
		}

		return statList;
	}

	// CaseItemList -> CaseItemList CaseItem
	private List<CaseItem> parseCaseItemList(){
		List<CaseItem> caseList = new ArrayList<>();
		CaseItem item = parseCaseItem();
		caseList.add(item);

		while(!willMatch(Token.Type.ENDCASE)) {
			item = parseCaseItem();
			caseList.add(item);
		}

		return caseList;
	}

	// CaseItem -> DEFAULT : Statement | DEFAULT Statement | ExpressionList : Statement
	private CaseItem parseCaseItem(){

		Position start = getStart();

		if (willMatch(Token.Type.DEFAULT)) {
			skip();

			if (willMatch(Token.Type.COLON)) { skip(); }

			Statement stat = parseStatementOrNull();
			return new DefCaseItem(start, stat);
		} else {
			List<Expression> expList = parseExpressionList();
			match(Token.Type.COLON, STRATEGY.REPAIR);
			Statement stat = parseStatementOrNull();
			return new ExprCaseItem(start, expList, stat);
		}

	}

	// IfStatement -> IF ( expression ) StatementOrNull
	// IfElseStatement -> IF ( expression ) StatementOrNull ELSE StatementOrNull
	private Statement parseIfStatement(){
		Position start = getStart();
		match(Token.Type.IF, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression expr = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatementOrNull();

		if (willMatch(Token.Type.ELSE)) {
			skip();
			Statement stat2 = parseStatementOrNull();
			return new IfElseStatement(start, expr, stat, stat2);
		} else {
			return new IfStatement(start, expr, stat);
		}

	}

	// ForStatement -> FOR ( Assignment ; Expression ; Assignment ) Statement
	private Statement parseForStatement(){
		Position start = getStart();
		match(Token.Type.FOR, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		BlockingAssignment init = parseAssignment();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		Expression expr = parseExpression();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		BlockingAssignment change = parseAssignment();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new ForStatement(start, init, expr, change, stat);
	}

	// Assignment -> LValue = Expression
	private BlockingAssignment parseAssignment(){
		Position start = getStart();
		LValue exp = parseLValue();
		match(Token.Type.EQ1, STRATEGY.REPAIR);
		Expression exp1 = parseExpression();
		return new BlockingAssignment(start, exp, exp1);
	}

	// AssignmentList -> Assignment AssignmentListRest
	// AssignmentListRest -> , Assignment AssignmentListRest | NULL
	private List<BlockingAssignment> parseAssignmentList(){
		List<BlockingAssignment> assignList = new ArrayList<>();

		do{
			BlockingAssignment assignment = parseAssignment();
			assignList.add(assignment);
		} while(skipIfYummy(Token.Type.COMMA));

		return assignList;
	}

	// CaseStatement -> CASE ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseStatement(){
		Position start = getStart();
		match(Token.Type.CASE, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		List<CaseItem> caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseStatement(start, exp, caseList);
	}

	// CaseZStatement -> CASEZ ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseZStatement(){
		Position start = getStart();
		match(Token.Type.CASEZ, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		List<CaseItem> caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseZStatement(start, exp, caseList);
	}

	// CaseXStatement -> CASEX ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseXStatement(){
		Position start = getStart();
		match(Token.Type.CASEX, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		List<CaseItem> caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseXStatement(start, exp, caseList);
	}

	// ForeverStatement -> FOREVER Statement
	private Statement parseForeverStatement(){
		Position start = getStart();
		match(Token.Type.FOREVER, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new ForeverStatement(start, stat);
	}

	// RepeatStatement -> REPEAT Statement
	private Statement parseRepeatStatement(){
		Position start = getStart();
		match(Token.Type.REPEAT, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new RepeatStatement(start, exp, stat);
	}

	// WhileStatement -> WHILE Statement
	private Statement parseWhileStatement(){
		Position start = getStart();
		match(Token.Type.WHILE, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new WhileStatement(start, exp, stat);
	}

	// WaitStatement -> WAIT Statement
	private Statement parseWaitStatement(){
		Position start = getStart();
		match(Token.Type.WAIT, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatementOrNull();
		return new WaitStatement(start, exp, stat);
	}

	// SeqBlock -> BEGIN StatementList END
	private Statement parseSeqBlock(){
		Position start = getStart();
		match(Token.Type.BEGIN, STRATEGY.REPAIR);
		List<Statement> statList = parseStatementList();
		match(Token.Type.END, STRATEGY.REPAIR);
		return new SeqBlockStatement(start, statList);
	}

	/**
	 * Below is the code for parsing expressions for the verilog lanuage. The recursive
	 * decent takes into account all operator precedence and it can also be used to parse
	 * strings. There are no booleans in Verilog so having a boolean would not make a bunch
	 * of sense.
	 * 
	 * @author Jacob Bauer
	 */

	// Expression -> STRING | LOR_Expression

	public Expression parseExpression(){

		Position start = getStart();

		if (willMatch(Token.Type.STRING)) {
			Token stringTok = skip();
			String tokenLexeme = stringTok.getLexeme();
			return new StringNode(start, tokenLexeme);
		} else {
			Expression expression = parseLOR_Expression();

			if (skipIfYummy(Token.Type.QUEST)) {
				Expression left = parseLOR_Expression();
				match(Token.Type.COLON);
				Expression right = parseExpression();
				expression = new TernaryOperation(start, expression, left, right);
			}

			return expression;
		}

	}

	// ExpressionOrNull -> Expression | NULL (ex: a, b, ,d)
	// This is mainly used for Module Items
	private Expression parseExpressionOrNull(){

		if (willMatch(Token.Type.COMMA)) {
			Token comma = peek();
			return new EmptyExpression(comma.getPosition());
		} else {
			return parseExpression();
		}

	}

	// lvalue -> IDENT | IDENT [ Expression ] | IDENT [ Expression : Expression ] |
	// Concatenation
	private LValue parseLValue(){

		if (willMatch(Token.Type.LCURL)) {
			return parseConcatenation();
		} else {
			Position start = getStart();
			String ident = parseRawIdentifier();

			if (willMatch(Token.Type.LBRACK)) {
				skip();
				Position localStart = getStart();
				Expression exp = parseExpression();

				if (willMatch(Token.Type.RBRACK)) {
					skip();
					return new Element(start, ident, exp);
				} else {
					match(Token.Type.COLON);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					return new Slice(start, ident, new ConstantExpression(localStart, exp), exp2);
				}

			} else {
				return new Identifier(start, ident);
			}

		}

	}

	// ConstantExpression -> expression
	private ConstantExpression parseConstantExpression(){
		Position start = getStart();
		Expression constant = parseExpression();
		return new ConstantExpression(start, constant);
	}

	// ExpressionList -> Expression ExpressionListRest
	// ExpressionListRest -> , Expression ExpressionListRest | NULL
	// ExpressionOrNullList -> ExpressionOrNull ExpressionOrNullListRest
	// ExpressionOrNullListRest -> , ExpressionOrNull ExpressionOrNullListRest | NULL
	private List<Expression> parseExpressionList(){
		List<Expression> expList = new ArrayList<>();

		do{
			Expression exp = parseExpression();
			expList.add(exp);
		}while(skipIfYummy(Token.Type.COMMA));

		return expList;
	}

	// ExpressionOrNullList -> ExpressionOrNull ExpressionOrNullListRest
	// ExpressionOrNullListRest -> , ExpressionOrNull ExpressionOrNullListRest | NULL
	private List<Expression> parseExpressionOrNullList(){
		List<Expression> expList = new ArrayList<>();

		do{
			Expression exp = parseExpressionOrNull();
			expList.add(exp);
		}while(skipIfYummy(Token.Type.COMMA));

		return expList;
	}

	// PortConnectionList -> PortConnection PortConnectionListRest
	// PortConnectionListRest -> , PortConenction
	private List<Expression> parsePortConnectionList(){
		List<Expression> expList = new ArrayList<>();

		do{
			Expression exp = parsePortConnection();
			expList.add(exp);
		}while(skipIfYummy(Token.Type.COMMA));

		return expList;
	}

	// PortConnection -> . IDENT ( Expression )
	private PortConnection parsePortConnection(){
		Position start = getStart();
		match(Token.Type.DOT, STRATEGY.REPAIR);
		String ident = parseRawIdentifier();
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		return new PortConnection(start, ident, exp);
	}

	// LOR_Expression -> LAND_Expression BinOp LAND_Expression
	private Expression parseLOR_Expression(){
		Expression left = parseLAND_Expression();

		while(willMatch(Token.Type.LOR)) {
			Position start = getStart();
			skip();
			Expression right = parseLAND_Expression();
			left = new BinaryOperation(start, left, Operator.LOR, right);
		}

		return left;
	}

	// LAND_Expression -> BOR_Expression BinOp BOR_Expression
	private Expression parseLAND_Expression(){
		Position start = getStart();

		Expression left = parseBOR_Expression();

		while(willMatch(Token.Type.LAND)) {
			start = getStart();
			skip();
			Expression right = parseBOR_Expression();
			left = new BinaryOperation(start, left, Operator.LAND, right);
		}

		return left;
	}

	// BOR_Expression -> BXOR_Expression BinOp BXOR_Expression
	private Expression parseBOR_Expression(){
		Expression left = parseBXOR_Expression();

		while(willMatch(Token.Type.BOR, Token.Type.BNOR)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBXOR_Expression();
			if(opType == Token.Type.BOR){
				left = new BinaryOperation(start, left, Operator.BOR, right);
			} else {
				left = new UnaryOperation(start, UnaryOperation.Operator.BNEG, new BinaryOperation(start, left, Operator.BOR, right));
			}
		}

		return left;
	}

	// BXOR_Expression -> BAND_Expression BinOp BAND_Expression
	private Expression parseBXOR_Expression(){
		Expression left = parseBAND_Expression();

		while(willMatch(Token.Type.BXOR, Token.Type.BXNOR)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBAND_Expression();
			if(opType == Token.Type.BXOR){
				left = new BinaryOperation(start, left, Operator.BXOR, right);
			} else {
				left = new BinaryOperation(start, left, Operator.BXNOR, right);
			}
		}

		return left;
	}

	// BAND_Expression -> NE_Expression BinOp NE_Expression
	private Expression parseBAND_Expression(){
		Expression left = parseNE_Expression();

		while(willMatch(Token.Type.BNAND, Token.Type.BAND)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseNE_Expression();
			if(opType == Token.Type.BAND){
				left = new BinaryOperation(start, left, Operator.BAND, right);
			} else {
				left =  new UnaryOperation(start, UnaryOperation.Operator.BNEG, new BinaryOperation(start, left, Operator.BAND, right));
			}
		}

		return left;
	}

	// NE_Expression -> REL_Expression BinOp REL_Expression
	private Expression parseNE_Expression(){
		Expression left = parseREL_Expression();

		if(willMatch(Token.Type.NE1, Token.Type.NE2, Token.Type.EQ2, Token.Type.EQ3)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseREL_Expression();
			if(opType == Token.Type.NE1){
				left = new BinaryOperation(start, left, Operator.NE1, right);
			} else if(opType == Token.Type.NE2){
				left = new BinaryOperation(start, left, Operator.NE2, right);
			} else if(opType == Token.Type.EQ2){
				left = new BinaryOperation(start, left, Operator.EQ2, right);
			} else {
				left = new BinaryOperation(start, left, Operator.EQ3, right);
			}
		}

		return left;
	}

	// REL_Expression -> SHIFT_Expression BinOp SHIFT_Expression
	private Expression parseREL_Expression(){
		Expression left = parseSHIFT_Expression();

		if(willMatch(Token.Type.GE, Token.Type.GT, Token.Type.LT, Token.Type.LE)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseSHIFT_Expression();
			if(opType == Token.Type.GT){
				left = new BinaryOperation(start, left, Operator.GT, right);
			} else if(opType == Token.Type.GE){
				left = new BinaryOperation(start, left, Operator.GE, right);
			} else if(opType == Token.Type.LT){
				left = new BinaryOperation(start, left, Operator.LT, right);
			} else {
				left = new BinaryOperation(start, left, Operator.LE, right);
			}
		}

		return left;
	}

	// SHIFT_Expression -> BIN_Expression BinOp BIN_Expression
	private Expression parseSHIFT_Expression(){
		Expression left = parseBIN_Expression();
		while(willMatch(Token.Type.LSHIFT, Token.Type.RSHIFT)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBIN_Expression();
			if(opType == Token.Type.LSHIFT){
				left = new BinaryOperation(start, left, Operator.LSHIFT, right);
			} else {
				left = new BinaryOperation(start, left, Operator.RSHIFT, right);
			}
		}

		return left;
	}

	// BIN_Expression -> MULT_Expression BinOp MULT_Expression
	private Expression parseBIN_Expression(){
		Expression left = parseMULT_Expression();

		while(willMatch(Token.Type.PLUS, Token.Type.MINUS)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseMULT_Expression();
			if(opType == Token.Type.PLUS){
				left = new BinaryOperation(start, left, Operator.PLUS, right);
			} else {
				left = new BinaryOperation(start, left, Operator.MINUS, right);
			}
		}

		return left;
	}

	// MULT_Expression -> UNARY_Expression BinOp UNARY_Expression
	private Expression parseMULT_Expression(){
		Expression left = parseUNARY_Expression();

		while(willMatch(Token.Type.TIMES, Token.Type.MOD, Token.Type.DIV)) {
			Position start = getStart();
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseUNARY_Expression();
			if(opType == Token.Type.TIMES){
				left = new BinaryOperation(start, left, Operator.TIMES, right);
			}else if(opType == Token.Type.DIV){
				left = new BinaryOperation(start, left, Operator.DIV, right);
			} else {
				left = new BinaryOperation(start, left, Operator.MOD, right);
			}
		}

		return left;
	}

	// UNARY_Expression -> UnOp Primary | Primary
	private Expression parseUNARY_Expression(){

		if (willMatch(Token.Type.PLUS, Token.Type.MINUS, Token.Type.BNEG, Token.Type.LNEG, Token.Type.BAND, Token.Type.BNAND, Token.Type.BOR, Token.Type.BNOR, Token.Type.BXOR, Token.Type.BXNOR)) {
			Position start = getStart();
			Token op = skip();
			Token.Type opType = op.getTokenType();
			Expression rightHandSideExpression = parsePrimary();
			if(opType == Token.Type.PLUS){
				return rightHandSideExpression;
			} else if(opType == Token.Type.MINUS) {
				return new UnaryOperation(start, UnaryOperation.Operator.MINUS, rightHandSideExpression);
			} else if(opType == Token.Type.BNEG){
				return new UnaryOperation(start, UnaryOperation.Operator.BNEG, rightHandSideExpression);
			} else {
				return new UnaryOperation(start, UnaryOperation.Operator.LNEG, rightHandSideExpression);
			}
		} else {
			return parsePrimary();
		}

	}

	// Primary -> NumValue | IDENT | Concatenation | SystemCall | ( Expression ) |
	// MACROIDENT
	private Expression parsePrimary(){

		if (willMatch(Token.Type.DEC)) {
			return parseDecimalNode();
		} else if (willMatch(Token.Type.HEX)) {
			return parseHexaDecimalNode();
		} else if (willMatch(Token.Type.OCT)) {
			return parseOctalNode();
		} else if (willMatch(Token.Type.BIN)) {
			return parseBinaryNode();
		} else if (willMatch(Token.Type.LCURL)) {
			return parseConcatenation();
		} else if (willMatch(Token.Type.DOLLAR)) {
			return parseSystemCall();
		} else if (skipIfYummy(Token.Type.LPAR)) {
			Expression exp = parseExpression();
			match(Token.Type.RPAR);
			return exp;
		} else if (willMatch(Token.Type.IDENT)) {
			Position start = getStart();
			Token identToken = skip();

			if (skipIfYummy(Token.Type.LBRACK)) {
				String ident = identToken.getLexeme();
				
				Position index1Position = getStart();
				Expression index1 = parseExpression();

				if (willMatch(Token.Type.COLON)) {
					skip();
					ConstantExpression index2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					return new Slice(start, ident, new ConstantExpression(index1Position, index1), index2);
				} else {
					match(Token.Type.RBRACK);
					return new Element(start, ident, index1);
				}

			} else if (skipIfYummy(Token.Type.LPAR)){
				String ident = identToken.getLexeme();

				if (!willMatch(Token.Type.RPAR)) {
					List<Expression> expList = parseExpressionList();
					match(Token.Type.RPAR);
					return new FunctionCall(start, ident, expList);
				} else {
					match(Token.Type.RPAR);
					return new FunctionCall(start, ident, new ArrayList<>());
				}

			} else {
				String lexeme = identToken.getLexeme();
				Identifier ident = new Identifier(start, lexeme);
				return ident;
			}
		} else {
			Token matched = peek();
			errorAndExit("Unexpected Primary Expression token of type " + matched.getTokenType()
			+ " and lexeme " + matched.getLexeme() + " found", matched.getPosition());
			return null;
		}

	}

	// SystemCall -> $ IDENT ( ExpressionList )
	private Expression parseSystemCall(){
		Position start = getStart();
		match(Token.Type.DOLLAR);

		String ident = parseRawIdentifier();

		if (skipIfYummy(Token.Type.LPAR)) {
			List<Expression> expList = parseExpressionList();
			match(Token.Type.RPAR);
			return new SystemFunctionCall(start, ident, expList);
		} else {
			return new SystemFunctionCall(start, ident, new ArrayList<>());
		}

	}

	// Concatenation -> { ExpressionList }
	private Concatenation parseConcatenation(){
		Position start = getStart();
		match(Token.Type.LCURL);
		List<Expression> expList = parseExpressionList();
		match(Token.Type.RCURL);
		return new Concatenation(start, expList);
	}

	// RawIdentifier -> IDENT
	private String parseRawIdentifier(){
		Token ident = match(Token.Type.IDENT);
		return ident.getLexeme();
	}

	//DecimalNode -> DEC
	private DecimalNode parseDecimalNode(){
		Position start = getStart();
		Token numToken = match(Token.Type.DEC);
		String numLexeme = numToken.getLexeme();
		return new DecimalNode(start, numLexeme);
	}

	//HexadecimalNode -> HEX
	private HexadecimalNode parseHexaDecimalNode(){
		Position start = getStart();
		Token numToken = match(Token.Type.HEX);
		String numLexeme = numToken.getLexeme();
		return new HexadecimalNode(start, numLexeme);
	}

	//OctalNode -> OCT
	private OctalNode parseOctalNode(){
		Position start = getStart();
		Token numToken = match(Token.Type.OCT);
		String numLexeme = numToken.getLexeme();
		return new OctalNode(start, numLexeme);
	}

	//BinaryNode -> BIN
	private BinaryNode parseBinaryNode(){
		Position start = getStart();
		Token numToken = match(Token.Type.BIN);
		String numLexeme = numToken.getLexeme();
		return new BinaryNode(start, numLexeme);
	}

}
