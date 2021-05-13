package edu.depauw.emulator_ide.assembler;

import java.util.ArrayList;
import java.util.List;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.common.io.Source;

public class Lexer {
	
	private Source source;
	private InfoLog errorLog;
	
	private Position position;
    private int lineNumber = 1;
    private int linePosition = 1;
	
	public Lexer(Source source, InfoLog errorLog) {
		this.source = source;
		this.errorLog = errorLog;
		this.position = getCurrentPosition();
	}
	
	private enum STATE{
		INIT, IDENT, STRING, COMMENT, CHAR, HEX, DECIMAL, BINARY, ERROR, DIRECTIVE
	}
	
	private Position getCurrentPosition() {
		return new Position(lineNumber, linePosition);
	}
	
	private void incrimentPosition(int num) {
		linePosition+=num;
	}
	
	private void incrimentPosition() {
		linePosition++;
	}
	
	private void resetPositionToNewLine() {
		linePosition = 0;
		lineNumber++;
	}
	
	private Token genNextToken() {
		StringBuilder lexeme = new StringBuilder();
		STATE state = STATE.INIT;
		while(!source.atEOD()) {
			char c = source.getCurrent();
			switch(state) {
			case INIT:
				if(c == '\n') {
					resetPositionToNewLine();
					source.advance();
					continue;
				} else if(Character.isWhitespace(c)) {
					source.advance();
					incrimentPosition();
					continue;
				} else if(Token.containsOp("" + c)) {
					position = getCurrentPosition();
					source.advance();
					incrimentPosition();
					return Token.makeOpToken("" + c, position);
				} else if(c == ';') {
					state = STATE.COMMENT;
					source.advance();
					incrimentPosition();
					continue;
				} else if(c == '\"') {
					state = state.STRING;
					position = getCurrentPosition();
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else if (c == '\'') {
					position = getCurrentPosition();
					lexeme.append(c);
					source.advance();
					c = source.getCurrent();
					if(source.getNext() == '\'') {
						lexeme.append(c + '\'');
						source.advance(2);
						incrimentPosition(3);
						return Token.makeCharToken(lexeme.toString(), position);
					} else {
						source.advance();
						errorLog.addItem(new ErrorItem("Expected \' at the end of character declaration but none found ", position));
						state = STATE.ERROR;
						continue;
					}
				} else if ( c == '0') {
					position = getCurrentPosition();
					if(source.getNext() == 'x') {
						lexeme.append(c);
						lexeme.append('x');
						source.advance(2);
						incrimentPosition(2);
						state = STATE.HEX;
						continue;
					} else if(source.getNext() == 'b') {
						lexeme.append(c);
						lexeme.append('b');
						source.advance(2);
						incrimentPosition(2);
						state = STATE.BINARY;
						continue;
					} else {
						source.advance();
						errorLog.addItem(new ErrorItem("Expected \' at the end of character declaration but none found ", position));
						state = STATE.ERROR;
						continue;
					}
				} else if (Character.isDigit(c)) {
					position = getCurrentPosition();
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					state = state.DECIMAL;
				} else if (c == '.') { 
					position = getCurrentPosition();
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					state = state.DIRECTIVE;
				} else if (Character.isLetter(c)) {
					position = getCurrentPosition();
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					state = state.IDENT;
				} else {
					source.advance();
					errorLog.addItem(new ErrorItem("Unexpected initial token " + c, position));
					state = STATE.ERROR;
					continue;
				}
			case COMMENT:
				if(c == '\n') {
					state = STATE.INIT;
					continue;
				} else {
					source.advance();
					incrimentPosition();
					continue;
				}
			case STRING:
				if(c == '\"') {
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
			case ERROR:
				if(c == '\n') {
					source.advance();
					resetPositionToNewLine();
					continue;
				} else if(Character.isWhitespace(c)) {
					source.advance();
					incrimentPosition();
					continue;
				} else {
					state = STATE.INIT;
					continue;
				}
			case IDENT:
				if(c == ':') {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					return Token.makeLabelToken(lexeme.toString(), position);
				} else if(Character.isLetter(c) ||  Character.isDigit(c)) {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else {
					return Token.makeIdentToken(lexeme.toString(), position);
				}
			case BINARY:
				if(c == '1' || c == '0') {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else {
					return Token.makeNumToken(lexeme.toString(), position);
				}
			case HEX:
				if('a' <= c && c <= 'f' || 'A' <= c && c <= 'F' || '0' <= c && c <= '9') {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else {
					return Token.makeNumToken(lexeme.toString(), position);
				}
			case DECIMAL:
				if(Character.isDigit(c)) {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else {
					return Token.makeNumToken(lexeme.toString(), position);
				}
			case DIRECTIVE:
				if(Character.isLetter(c) ||  Character.isDigit(c)) {
					lexeme.append(c);
					source.advance();
					incrimentPosition();
					continue;
				} else {
					return Token.makeDirToken(lexeme.toString(), position);
				}
			}
		}
		switch(state){
		case DIRECTIVE:
			return Token.makeDirToken(lexeme.toString(), position);
		case STRING:
			errorLog.addItem(new ErrorItem("Unexpected end of file while parsing string", position));
			return null;
		case DECIMAL:
			return Token.makeNumToken(lexeme.toString(), position);
		case BINARY:
			return Token.makeNumToken(lexeme.toString(), position);
		case HEX:
			return Token.makeNumToken(lexeme.toString(), position);
		case IDENT:
			return Token.makeIdentToken(lexeme.toString(), position);
		default:
			return null;
		}
	}
	
	public List<Token> tokenize(){
		ArrayList<Token> tokenList = new ArrayList<>();
		while(!source.atEOD()){
		    Token nextToken = genNextToken();
		    if(nextToken != null){
		    	tokenList.add(nextToken);
		    }
		}
		return tokenList;
	}
	
}
