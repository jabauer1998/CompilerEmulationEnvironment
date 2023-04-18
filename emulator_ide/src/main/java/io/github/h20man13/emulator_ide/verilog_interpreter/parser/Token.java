package io.github.h20man13.emulator_ide.verilog_interpreter.parser;

import java.util.HashMap;

import io.github.h20man13.emulator_ide.common.Position;

public class Token {

	public enum Type{
		IDENT, // identifiers
		DEC, // Numbers
		REALNUM,
		OCT,
		HEX,
		BIN,
		STRING, // String constants

		//White Space characters
		NEWLINE, // /n
		ESCAPEDLINE, //n or when typed in a text editor it is just a /

		//For dealing with Macros
		MACRO_DEFINE,  
		MACRO_INCLUDE,
		MACRO_IFDEF,
		MACRO_IFNDEF,
		MACRO_ENDIF,
		MACRO_ELSEIF,
		MACRO_ELSE,
		MACRO_IDENT,

		//EOF
		EOF,

		// Operators
		LPAR, // (
		RPAR, // )
		RCURL, // }
		LCURL, // {
		RBRACK, // ]
		LBRACK, // [
		COMMA, // ,
		PLUS, // +
		MINUS, // -
		TIMES, // *
		MOD, // %
		DIV, // /
		QUEST, // ?
		COLON, // :
		LT, // <
		GT, // >
		LE, // <=
		GE, // >=
		DELAY, // #
		SEMI, // ;
		AT, // @
		DOLLAR, // $
		EQ1, // =
		EQ2, // ==
		EQ3, // ===
		NE1, // !=
		NE2, // !==
		LAND, // &&
		LOR, // ||
		LNEG, // !
		BAND, // &
		BNEG, // ~
		BOR, // |
		BXOR, // ^
		BXNOR, // ^~ or ~^
		BNAND, // ~&
		BNOR, // ~|
		LSHIFT, // <<
		RSHIFT, // >>
		DOT, // .

		// keywords
		INITIAL, ALLWAYS, BEGIN, END, MODULE, ENDMODULE, TASK, ENDTASK, CASE, ENDCASE, FUNCTION, ENDFUNCTION, ASSIGN, POSEGE,
		NEGEGE, OR, IF, ELSE, WHILE, FOREVER, REPEAT, FOR, INTEGER, REG, REAL, OUTPUT, INPUT, WAIT, CASEZ, CASEX, DEFAULT, WIRE,
		ORGATE, NORGATE, NANDGATE, ANDGATE, XORGATE, XNORGATE, NOTGATE
	}

	private static HashMap<String, Type> OPS;
	private static HashMap<String, Type> KEY;
	private static HashMap<String, Type> MACRO;

	static {
		OPS = new HashMap<>(); // hashmap to store all of the operators
		OPS.put("(", Type.LPAR);
		OPS.put(")", Type.RPAR);
		OPS.put("{", Type.LCURL);
		OPS.put("}", Type.RCURL);
		OPS.put("[", Type.LBRACK);
		OPS.put("]", Type.RBRACK);
		OPS.put(",", Type.COMMA);
		OPS.put("+", Type.PLUS);
		OPS.put("-", Type.MINUS);
		OPS.put("*", Type.TIMES);
		OPS.put("/", Type.DIV);
		OPS.put("%", Type.MOD);
		OPS.put("?", Type.QUEST);
		OPS.put(":", Type.COLON);
		OPS.put("<", Type.LT);
		OPS.put(">", Type.GT);
		OPS.put("<=", Type.LE);
		OPS.put(">=", Type.GE);
		OPS.put("#", Type.DELAY);
		OPS.put(";", Type.SEMI);
		OPS.put("@", Type.AT);
		OPS.put("$", Type.DOLLAR);
		OPS.put("=", Type.EQ1);
		OPS.put("==", Type.EQ2);
		OPS.put("===", Type.EQ3);
		OPS.put("!=", Type.NE1);
		OPS.put("!==", Type.NE2);
		OPS.put("&&", Type.LAND);
		OPS.put("||", Type.LOR);
		OPS.put("!", Type.LNEG);
		OPS.put("&", Type.BAND);
		OPS.put("|", Type.BOR);
		OPS.put("~", Type.BNEG);
		OPS.put("^", Type.BXOR);
		OPS.put("^~", Type.BXNOR);
		OPS.put("~^", Type.BXNOR);
		OPS.put("~|", Type.BNOR);
		OPS.put("~&", Type.BNAND);
		OPS.put("<<", Type.LSHIFT);
		OPS.put(">>", Type.RSHIFT);
		OPS.put(".", Type.DOT);

		KEY = new HashMap<>(); // hashmap to store all of the key words
		KEY.put("initial", Type.INITIAL);
		KEY.put("allways", Type.ALLWAYS);
		KEY.put("begin", Type.BEGIN);
		KEY.put("end", Type.END);
		KEY.put("module", Type.MODULE);
		KEY.put("endmodule", Type.ENDMODULE);
		KEY.put("task", Type.TASK);
		KEY.put("endtask", Type.ENDTASK);
		KEY.put("function", Type.FUNCTION);
		KEY.put("endfunction", Type.ENDFUNCTION);
		KEY.put("assign", Type.ASSIGN);
		KEY.put("posedge", Type.POSEGE);
		KEY.put("negedge", Type.NEGEGE);
		KEY.put("or", Type.OR);
		KEY.put("if", Type.IF);
		KEY.put("else", Type.ELSE);
		KEY.put("while", Type.WHILE);
		KEY.put("forever", Type.FOREVER);
		KEY.put("repeat", Type.REPEAT);
		KEY.put("for", Type.FOR);
		KEY.put("integer", Type.INTEGER);
		KEY.put("real", Type.REAL);
		KEY.put("reg", Type.REG);
		KEY.put("wire", Type.WIRE);
		KEY.put("output", Type.OUTPUT);
		KEY.put("input", Type.INPUT);
		KEY.put("wait", Type.WAIT);
		KEY.put("casez", Type.CASEZ);
		KEY.put("casex", Type.CASEX);
		KEY.put("default", Type.DEFAULT);
		KEY.put("and", Type.ANDGATE);
		KEY.put("nand", Type.NANDGATE);
		KEY.put("or", Type.ORGATE);
		KEY.put("nor", Type.NORGATE);
		KEY.put("xor", Type.XORGATE);
		KEY.put("xnor", Type.XNORGATE);
		KEY.put("not", Type.NOTGATE);
		KEY.put("case", Type.CASE);
		KEY.put("endcase", Type.ENDCASE);

		MACRO = new HashMap<>();
		MACRO.put("`define", Type.MACRO_DEFINE);
		MACRO.put("`include", Type.MACRO_INCLUDE);
		MACRO.put("`ifdef", Type.MACRO_IFDEF);
		MACRO.put("`ifndef", Type.MACRO_IFNDEF);
		MACRO.put("`elseif", Type.MACRO_ELSEIF);
		MACRO.put("`else", Type.MACRO_ELSE);
		MACRO.put("`endif", Type.MACRO_ENDIF);
	}

	private final Type     type;
	private final String   lexeme;
	private final Position position;

	private Token(String lexeme, Position position, Type type) {
		this.lexeme = lexeme;
		this.type = type;
		this.position = position;
	}

	public String getLexeme(){ return lexeme; }

	public Token.Type getTokenType(){ return type; }

	public Position getPosition(){ return position; }

	@Override
	public String toString(){ 
		if( this.type == Type.NEWLINE ) return "Token " + this.type + " ( \\n ) at " + this.position.toString();
		else if (this.type == Type.ESCAPEDLINE ) return "Token " + this.type + " ( \\\\n ) at " + this.position.toString();
		else return "Token " + this.type + " ( " + this.lexeme + " ) at " + this.position.toString();
	}

	private static Token makeToken(String lexeme, Position position, Type type){ return new Token(lexeme, position, type); }

    public static Token makeEofToken(Position position) { return new Token("EOF", position, Type.EOF); }

	public static Token makeBinToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.BIN); }
	
	public static Token makeHexToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.HEX); }

	public static Token makeDecToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.DEC); }

	public static Token makeRealToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.REALNUM); }

	public static Token makeOctToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.OCT); }
		
	public static Token makeNewLineToken(String lexeme, Position position){
		if(lexeme == "\\n"){
			return new Token(lexeme, position, Token.Type.ESCAPEDLINE);
		} else {
			return new Token(lexeme, position, Token.Type.NEWLINE);
		}
	}

	public static Token makeIdToken(String lexeme, Position position){

		if (KEY.containsKey(lexeme)) {
			return makeToken(lexeme, position, KEY.get(lexeme));
		} else {
			return makeToken(lexeme, position, Type.IDENT);
		}

	}

	public static Token makeMacroToken(String lexeme, Position position){

		if (MACRO.containsKey(lexeme)) {
			return makeToken(lexeme, position, MACRO.get(lexeme));
		} else {
			return makeToken(lexeme, position, Type.MACRO_IDENT);
		}

	}

	public static Token makeStringToken(String lexeme, Position position){ return makeToken(lexeme, position, Type.STRING); }

	public static Token makeOpToken(String lexeme, Position position){ return makeToken(lexeme, position, OPS.get(lexeme)); }

	public static boolean containsOp(String op){ return OPS.containsKey(op); }
}
