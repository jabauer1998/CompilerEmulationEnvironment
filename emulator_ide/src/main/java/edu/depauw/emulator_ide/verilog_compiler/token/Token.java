package edu.depauw.emulator_ide.verilog_compiler.token;
import java.util.HashMap;

public class Token{
	enum Type {
	    IDENT, //identifiers
	    NUM, //Numbers
	    
	    //Operators
	    LPAR,   // (
	    RPAR,   // )
	    RCURL,  // }
	    LCURL,  // {
	    RBRACK, // ]
	    LBRACK, // [
	    COMMA,  // ,
	    PLUS,   // +
	    MINUS,  // -
	    TIMES,  // *
	    MOD,    // %
	    DIV,    // /
	    QUEST,  // ?
	    COLON,  // :
	    LT,     // <
	    GT,     // >
	    LE,     // <=
	    GE,     // >=
	    DELAY,  // #
	    SEMI,   // ;
	    AT,     // @
	    DOLLAR, // $
	    EQ1,    // =
	    EQ2,    // ==
	    EQ3,    // ===
	    NE1,    // !=
	    NE2,    // !==
	    LAND,   // &&
	    LOR,    // ||
	    LNEG,   // !
	    BAND,   // &
	    BNEG,   // ~
	    BOR,    // |
	    BXNOR,  // ^~ or ~^
	    BNAND,  // ~&
	    BNOR,   // ~|
	    LSHIFT, // <<
	    RSHIFT, // >>
	    ALSHIFT,// <<<
	    ARSHIFT,// >>>
	    
	    //keywords
	    INIT,
	    ALLWAYS,
	    BEGIN,
	    END,
	    MODULE,
	    ENDMODULE,
	    TASK,
	    ENDTASK,
	    CASE,
	    ENDCASE,
	    FUNCTION,
	    ENDFUNCTION,
	    ASSIGN,
	    POSEGE,
	    NEGEGE,
	    OR,
	    IF,
	    ELSE,
	    WHILE,
	    FOREVER,
	    REPEAT,
	    FOR,
	    INT,
	    REAL,
	    RTIME,
	    TIME,
	};
    private static HashMap<String, Type> OPS;
    private static HashMap<String, Type> KEY;
    static {
	OPS = new HashMap<>(); //hashmap to store all of the operators
	OPS.put("(",Type.LPAR);
	OPS.put(")", Type.RPAR);
	OPS.put("{", Type.LCURL);
	OPS.put("}", Type.RCURL);
	OPS.put("[", Type.LBRACK);
	OPS.put("]", Type.RBRACK);
	OPS.put(",", Type.COMMA);
	OPS.put("+",  Type.PLUS);
	OPS.put("-",  Type.MINUS);
	OPS.put("*",  Type.TIMES);
	OPS.put("/",  Type.DIV);
	OPS.put("%",  Type.MOD);
	OPS.put("?",  Type.QUEST);
	OPS.put(":",  Type.COLON);
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
	OPS.put("^~", Type.BXNOR);
	OPS.put("~^", Type.BXNOR);
	OPS.put("~|", Type.BNOR);
	OPS.put("~&", Type.BNAND);
	OPS.put("<<", Type.LSHIFT);
	OPS.put(">>", Type.RSHIFT);
	OPS.put("<<<", Type.ALSHIFT);
	OPS.put(">>>", Type.ARSHIFT);

	KEY = new HashMap<>(); //hashmap to store all of the key words
	KEY.put("initial", Type.INIT);
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
	KEY.put("integer", Type.INT);
	KEY.put("real", Type.REAL);
	KEY.put("realtime", Type.RTIME);
	KEY.put("time", Type.TIME);
    };

    private Type type;
    private String lexeme;
    private Position position;

    private Token(String lexeme, Position position, Type type){
	this.lexeme = lexeme;
	this.type = type;
	this.position = position;
    }

    public static Token makeNumToken(String lexeme, Position position){
	return new Token(lexeme, position, Type.NUM);
    }

    public static Token makeIdToken(String lexeme, Position position){
	if(KEY.containsKey(lexeme)){
	    return new Token(lexeme, position, KEY.get(lexeme));
	} else {
	    return new Token(lexeme, position, Type.IDENT);
	}
    }

    public static Token makeOpToken(String lexeme, Position position){
	return new Token(lexeme, position, OPS.get(lexeme));
    }

    public static Token makeToken(String lexeme, Position position, Type type){
	return new Token(lexeme, position, type);
    }
    
}
