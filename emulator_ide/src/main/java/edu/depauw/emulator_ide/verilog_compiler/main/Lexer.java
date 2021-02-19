package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;


import java.util.ArrayList;
import java.lang.StringBuilder;

public class Lexer{  
    private final Source source;
    private final InfoLog ErrorLog;

    private Position position;
    private int lineNumber = 0;
    private int linePosition = 0;

    public Lexer(Source source, InfoLog ErrorLog){
	this.ErrorLog = ErrorLog;
	this.source = source;
	this.position = getCurrentPosition();
    }

    private enum STATE{
	INIT, REAL, IDENT, OP, STRING, SINGLECOMMENT, MULTICOMMENT, BIN, DEC, OCT, HEX, ERROR
    }

    private Position getCurrentPosition(){
	return new Position(lineNumber, linePosition);
    }

    private  void incrimentPosition(){
	linePosition++;
    }

    private  void incrimentPosition(int num){
	linePosition += num;
    }

    private void resetPositionToNewLine(){
	lineNumber++;
	linePosition = 0;
    }
    
    private Token genNextToken(){
	StringBuilder lexeme = new StringBuilder();
	STATE state = STATE.INIT;
	int count = 0;
	boolean hasE = false;
	while(!source.atEOD()){
	    char c  = source.getCurrent();
	    switch(state){
	    case INIT:
		if(c == '\n'){
		    source.advance();
		    resetPositionToNewLine();
		    continue;
	        } else if(Character.isWhitespace(c)){
		    source.advance();
		    incrimentPosition();
		    continue;
		} else if (c == '\"'){
		    state = STATE.STRING;
		    position = getCurrentPosition();
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else if(Character.isLetter(c)){
		    state = STATE.IDENT;
		    position = getCurrentPosition();
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else if(Character.isDigit(c)){
		    position = getCurrentPosition();
		    if(source.getNext() == '\''){
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			continue;
		    } else {
			state = STATE.DEC;
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			continue;
		    }
		} else if(c == '\''){
		    position = getCurrentPosition();
		    lexeme.append(c);
		    char next = source.getNext();
		    if(next == 'd'){
			state = STATE.DEC;
			lexeme.append(next);
			source.advance(2);
			incrimentPosition();
			continue;
		    } else if(next == 'h'){
			state = STATE.HEX;
			lexeme.append(next);
			source.advance(2);
			incrimentPosition(2);
			continue;
		    } else if(next == 'b'){
			state = STATE.BIN;
			lexeme.append(next);
			source.advance(2);
			incrimentPosition(2);
			continue;
		    } else if(next == '0'){
			state = STATE.OCT;
			lexeme.append(next);
			source.advance(2);
			incrimentPosition(2);
			continue;
		    } else {
			position = getCurrentPosition();
			ErrorLog.addItem(new ErrorItem("Character representing hex, binary, decimal and octal missing", position));
			state = STATE.ERROR;
			continue;
		    }
		} else if(c == '/'){
		    position = getCurrentPosition();
		    if(source.getNext() == '/'){
			source.advance(2);
			incrimentPosition(2);
			state = STATE.SINGLECOMMENT;
			continue;
		    } else if(source.getNext() == '*'){
			source.advance(2);
			incrimentPosition(2);
			count++;
			state = STATE.MULTICOMMENT;
			continue;
		    } else {
			state = STATE.OP;
			continue;
		    }
		} else if(Token.containsOp("" + c)){
		    position = getCurrentPosition();
		    state = STATE.OP;
		    continue;
		} else {
		    position = getCurrentPosition();
		    ErrorLog.addItem(new ErrorItem("Unreconizable character found", source.getPosition()));
		    state = STATE.ERROR;
		    continue;
		}
	    case STRING:
		if(c == '\"'){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    return Token.makeStringToken(lexeme.toString(), position);
		} else {
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		}
	    case OP:
		if(c == '<'){
		    if(source.getNext() == '<'){
			int i = 0;
			while(i < 3 && c == '<'){
			    lexeme.append(c);
			    source.advance();
			    incrimentPosition();
			    c = source.getCurrent();
			    i++;
			}
			return Token.makeOpToken(lexeme.toString(), position);
		    } else if(source.getNext() == '='){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if(c == '>'){
		    if(source.getNext() == '>'){
			int i = 0;
			while(i < 3 && c == '>'){
			    lexeme.append(c);
			    source.advance();
			    incrimentPosition();
			    c = source.getCurrent();
			    i++;
			}
			return Token.makeOpToken(lexeme.toString(), position);
		    } else if(source.getNext() == '='){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if(c == '~'){
		    if(source.getNext() == '&' || source.getNext() == '|' || source.getNext() == '^'){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if(c == '^'){
		    if(source.getNext() == '~'){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if(c == '&'){
		    if(source.getNext() == '&'){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if(c == '|'){
		    if(source.getNext() == '|'){
			lexeme.append(c);
			lexeme.append(source.getNext());
			incrimentPosition(2);
			source.advance(2);
			return Token.makeOpToken(lexeme.toString(), position);
		    } else {
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			return Token.makeOpToken(lexeme.toString(), position);
		    }
		} else if (c == '!') {
		    lexeme.append(c);
		    source.advance();
		    int i = 0;
		    while(i < 2 && source.getCurrent() == '='){
			lexeme.append(source.getCurrent());
			source.advance();
			i++;
		    }
		    return Token.makeOpToken(lexeme.toString(), position);
		} else if (c == '=') {
		    int i = 0;
		    while(i < 3 && source.getCurrent() == '='){
			lexeme.append(source.getCurrent());
			source.advance();
			i++;
		    }
		    return Token.makeOpToken(lexeme.toString(), position);
		} else {
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    return Token.makeOpToken(lexeme.toString(), position);
		}
	    case SINGLECOMMENT:
		if(c == '\n'){
		    state = STATE.INIT;
		    continue;
		} else {
		    source.advance();
		    incrimentPosition();
		    continue;
		}
	    case MULTICOMMENT:
		if(c == '*' && source.getNext() == '/'){
		    count--;
		    if(count == 0){
			state = STATE.INIT;
		    }
		    source.advance(2);
		    incrimentPosition(2);
		    continue;
		} else if(c == '/' && source.getNext() == '*') {
		    ErrorLog.addItem(new ErrorItem("In verilog there are no embedded comments allowed",position));
		    count++;
		    source.advance(2);
		    incrimentPosition(2);
		    continue;
		} else {
		    source.advance();
		    incrimentPosition();
		    continue;
		}
	    case IDENT:
		if(Character.isLetter(c) || c == '_' || Character.isDigit(c)){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else {
		    return Token.makeIdToken(lexeme.toString(), position);
		}
	    case DEC:
		if(Character.isDigit(c) || c == 'X' || c == 'x' || c == 'z' || c == 'Z'){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else if (c == 'e' || c == 'E'){
		    hasE = true;
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    state = STATE.REAL;
		    continue;
		} else if (c == '.') {
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    state = STATE.REAL;
		    continue;
		} else {
		    return Token.makeNumToken(lexeme.toString(), position);
		}
	    case OCT:
		if(((int)c >= (int)'0' && (int)c <= (int)'7') || c == 'X' || c == 'x' || c == 'z' || c == 'Z'){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else {
		    return Token.makeNumToken(lexeme.toString(), position);
		}
	    case HEX:
		if(Character.isDigit(c) || ((int)c >= (int)'a' && (int)c <= (int)'f') || ((int)c >= (int)'A' && (int)c <= (int)'F') || c == 'X' || c == 'x' || c == 'z' || c == 'Z'){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else {
		    return Token.makeNumToken(lexeme.toString(), position);
		}
	    case BIN:
		if(c == '0' || c == '1' || c == 'X' || c == 'x' || c == 'z' || c == 'Z'){
		    lexeme.append(c);
		    source.advance();
		    incrimentPosition();
		    continue;
		} else {
		    return Token.makeNumToken(lexeme.toString(), position);
		}
	    case REAL:
		if(hasE){
		    if(Character.isDigit(c)){
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			continue;
		    } else {
			return Token.makeNumToken(lexeme.toString(), position);
		    }
		} else {
		    if(Character.isDigit(c)){
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			continue;
		    } else if(c == 'e' || c == 'E'){
			hasE = true;
			lexeme.append(c);
			source.advance();
			incrimentPosition();
			continue;
		    } else {
			return Token.makeNumToken(lexeme.toString(), position);
		    }
		}
	    case ERROR:
		if(!Character.isWhitespace(c)){
		    source.advance();
		    incrimentPosition();
		    continue;
		} else {
		    state = STATE.INIT;
		    continue;
		}
	    }
	}
	switch(state){
	case BIN:
	    return Token.makeNumToken(lexeme.toString(), position);
	case REAL:
	    return Token.makeNumToken(lexeme.toString(), position);
	case DEC:
	    return Token.makeNumToken(lexeme.toString(), position);
	case OCT:
	    return Token.makeNumToken(lexeme.toString(), position);
	case MULTICOMMENT:
	    ErrorLog.addItem(new ErrorItem("Unexepected End of File Found in multi line MULTICOMMENT state", null));
	    return null;
	case IDENT:
	    return Token.makeIdToken(lexeme.toString(), position);
	case OP:
	    return Token.makeOpToken(lexeme.toString(), position);
	case STRING:
	    ErrorLog.addItem(new ErrorItem("Unexepected End of File Found in STRING state", null));
	    return null;
	default:
	    return null;
	}
    }
    
    public ArrayList<Token> tokenize(){
	ArrayList<Token> tokenList = new ArrayList<>();
	while(!source.atEOD()){
	    Token nextToken = genNextToken();
	    if(nextToken != null){
		tokenList.add(nextToken);
	    }
	}
	return tokenList;
    }

    public InfoLog getErrorLog(){
	return ErrorLog;
    }
}
