package edu.depauw.emulator_ide.verilog_compiler.parser;


import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.common.Position;

import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;

import java.util.LinkedList;
import java.util.List;
import java.lang.StringBuilder;

public class Lexer {

	private final Source  source;
	private final ErrorLog ErrorLog;
	private Position      position;

	public Lexer(Source source, ErrorLog ErrorLog) {
		this.ErrorLog = ErrorLog;
		this.source = source;
		this.position = source.getPosition();
	}

    private enum STATE{ INIT, REAL, IDENT, OP, STRING, SINGLECOMMENT, MULTICOMMENT, BIN, DEC, OCT, HEX, ERROR, MACRO }

	private Token genNextToken(){
		StringBuilder lexeme = new StringBuilder();
		STATE state = STATE.INIT;
		int count = 0;
		boolean hasE = false;

		while(!source.atEOD()) {
			char c = source.getCurrent();

			switch(state){
				case INIT:
					if(c == '\\' && source.getNext() == '\n'){
						Position start = source.getPosition();
						source.advance(2);
						return Token.makeNewLineToken("\\n", start);
					} else if(c == '\n'){
						Position start = source.getPosition();
						source.advance();
						return Token.makeNewLineToken("\n", start);
					} else if (Character.isWhitespace(c) || c == '\n') {
						source.advance();
						continue;
					} else if (c == '`' && Character.isLetter(source.getNext())) {
						state = STATE.MACRO;
						position = source.getPosition();
						lexeme.append(c);
						lexeme.append(source.getNext());
						source.advance(2);
						continue;
					} else if (c == '\"') {
						state = STATE.STRING;
						position = source.getPosition();
						lexeme.append(c);
						source.advance();
						continue;
					} else if (Character.isLetter(c)) {
						state = STATE.IDENT;
						position = source.getPosition();
						lexeme.append(c);
						source.advance();
						continue;
					} else if (Character.isDigit(c)) {
						position = source.getPosition();

						if (source.getNext() == '\'') {
							lexeme.append(c);
							source.advance();
							continue;
						} else {
							state = STATE.DEC;
							lexeme.append(c);
							source.advance();
							continue;
						}

					} else if (c == '\'') {
						position = source.getPosition();
						lexeme.append(c);
						char next = source.getNext();

						if (next == 'd' || next == 'D') {
							state = STATE.DEC;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else if (next == 'h' || next == 'H') {
							state = STATE.HEX;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else if (next == 'b' || next == 'B') {
							state = STATE.BIN;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else if (next == 'O' || next == 'o') {
							state = STATE.OCT;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else {
							position = source.getPosition();
							ErrorLog.addItem(
								new ErrorItem("Character representing hex, binary, decimal and octal missing", position));
							state = STATE.ERROR;
							continue;
						}

					} else if (c == '/') {
						position = source.getPosition();

						if (source.getNext() == '/') {
							source.advance(2);
							state = STATE.SINGLECOMMENT;
							continue;
						} else if (source.getNext() == '*') {
							source.advance(2);
							count++;
							state = STATE.MULTICOMMENT;
							continue;
						} else {
							state = STATE.OP;
							continue;
						}

					} else if (Token.containsOp("" + c)) {
						position = source.getPosition();
						state = STATE.OP;
						continue;
					} else {
						position = source.getPosition();
						ErrorLog.addItem(new ErrorItem("Unreconizable character found", source.getPosition()));
						state = STATE.ERROR;
						continue;
					}
				case STRING:
					if (c == '\"') {
						lexeme.append(c);
						source.advance();
						return Token.makeStringToken(lexeme.toString(), position);
					} else {
						lexeme.append(c);
						source.advance();
						continue;
					}
				case OP:
					if (c == '<') {

						if (source.getNext() == '<') {
							int i = 0;

							while(i < 3 && c == '<') {
								lexeme.append(c);
								source.advance();
								c = source.getCurrent();
								i++;
							}

							return Token.makeOpToken(lexeme.toString(), position);
						} else if (source.getNext() == '=') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '>') {

						if (source.getNext() == '>') {
							int i = 0;

							while(i < 3 && c == '>') {
								lexeme.append(c);
								source.advance();
								c = source.getCurrent();
								i++;
							}

							return Token.makeOpToken(lexeme.toString(), position);
						} else if (source.getNext() == '=') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '~') {

						if (source.getNext() == '&' || source.getNext() == '|' || source.getNext() == '^') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '^') {

						if (source.getNext() == '~') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '&') {

						if (source.getNext() == '&') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '|') {

						if (source.getNext() == '|') {
							lexeme.append(c);
							lexeme.append(source.getNext());
							source.advance(2);
							return Token.makeOpToken(lexeme.toString(), position);
						} else {
							lexeme.append(c);
							source.advance();
							return Token.makeOpToken(lexeme.toString(), position);
						}

					} else if (c == '!') {
						lexeme.append(c);
						source.advance();
						int i = 0;

						while(i < 2 && source.getCurrent() == '=') {
							lexeme.append(source.getCurrent());
							source.advance();
							i++;
						}

						return Token.makeOpToken(lexeme.toString(), position);
					} else if (c == '=') {
						int i = 0;

						while(i < 3 && source.getCurrent() == '=') {
							lexeme.append(source.getCurrent());
							source.advance();
							i++;
						}

						return Token.makeOpToken(lexeme.toString(), position);
					} else {
						lexeme.append(c);
						source.advance();
						return Token.makeOpToken(lexeme.toString(), position);
					}
				case SINGLECOMMENT:
					if (c == '\n') {
						state = STATE.INIT;
						continue;
					} else {
						source.advance();
						continue;
					}
				case MULTICOMMENT:
					if (c == '*' && source.getNext() == '/') {
						count--;

						if (count == 0) { state = STATE.INIT; }

						source.advance(2);
						continue;
					} else if (c == '/' && source.getNext() == '*') {
						ErrorLog.addItem(new ErrorItem("In verilog there are no embedded comments allowed", position));
						count++;
						source.advance(2);
						continue;
					} else if (c == '\n') {
						source.advance();
						continue;
					} else {
						source.advance();
						continue;
					}
				case IDENT:
					if (Character.isLetter(c) || c == '_' || Character.isDigit(c)) {
						lexeme.append(c);
						source.advance();
						continue;
					} else {
						return Token.makeIdToken(lexeme.toString(), position);
					}
				case DEC:
					if (Character.isDigit(c) || c == 'X' || c == 'x' || c == 'z' || c == 'Z') {
						lexeme.append(c);
						source.advance();
						continue;
					} else if (c == '\'') {
						position = source.getPosition();
						lexeme.append(c);
						char next = source.getNext();

						if (next == 'd' || next == 'D') {
							state = STATE.DEC;
							lexeme.append(next);
							source.advance(2);

							continue;
						} else if (next == 'h' || next == 'H') {
							state = STATE.HEX;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else if (next == 'b' || next == 'B') {
							state = STATE.BIN;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else if (next == 'O' || next == 'o') {
							state = STATE.OCT;
							lexeme.append(next);
							source.advance(2);
							continue;
						} else {
							position = source.getPosition();
							ErrorLog.addItem(
								new ErrorItem("Character representing hex, binary, decimal and octal missing", position));
							state = STATE.ERROR;
							continue;
						}

					} else if (c == 'e' || c == 'E') {
						hasE = true;
						lexeme.append(c);
						source.advance();

						state = STATE.REAL;
						continue;
					} else if (c == '.') {
						lexeme.append(c);
						source.advance();
						state = STATE.REAL;
						continue;
					} else {
						return Token.makeNumToken(lexeme.toString(), position);
					}
				case OCT:
					if (((int)c >= (int)'0' && (int)c <= (int)'7') || c == 'X' || c == 'x' || c == 'z' || c == 'Z') {
						lexeme.append(c);
						source.advance();
						continue;
					} else {
						return Token.makeNumToken(lexeme.toString(), position);
					}
				case HEX:
					if (Character.isDigit(c) || ((int)c >= (int)'a' && (int)c <= (int)'f')
						|| ((int)c >= (int)'A' && (int)c <= (int)'F') || c == 'X' || c == 'x' || c == 'z' || c == 'Z') {
						lexeme.append(c);
						source.advance();
						continue;
					} else {
						return Token.makeNumToken(lexeme.toString(), position);
					}
				case BIN:
					if (c == '0' || c == '1' || c == 'X' || c == 'x' || c == 'z' || c == 'Z') {
						lexeme.append(c);
						source.advance();
						continue;
					} else {
						return Token.makeNumToken(lexeme.toString(), position);
					}
				case REAL:
					if (hasE) {

						if (Character.isDigit(c)) {
							lexeme.append(c);
							source.advance();
							continue;
						} else {
							return Token.makeNumToken(lexeme.toString(), position);
						}

					} else {

						if (Character.isDigit(c)) {
							lexeme.append(c);
							source.advance();
							continue;
						} else if (c == 'e' || c == 'E') {
							hasE = true;
							lexeme.append(c);
							source.advance();
							continue;
						} else {
							return Token.makeNumToken(lexeme.toString(), position);
						}

					}
				case MACRO:
					if (Character.isLetter(c) || c == '_' || Character.isDigit(c)) {
						lexeme.append(c);
						source.advance();
						continue;
					} else {
						return Token.makeMacroToken(lexeme.toString(), position);
					}
				case ERROR:
					if (!Character.isWhitespace(c)) {
						source.advance();
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
			case HEX:
				return Token.makeNumToken(lexeme.toString(), position);
			case MULTICOMMENT:
				ErrorLog.addItem(new ErrorItem("Unmatched /* found.", null));
				return null;
			case IDENT:
				return Token.makeIdToken(lexeme.toString(), position);
			case OP:
				return Token.makeOpToken(lexeme.toString(), position);
			case STRING:
				ErrorLog.addItem(new ErrorItem("Unmatched \" found", null));
				return null;
			case MACRO:
				return Token.makeMacroToken(lexeme.toString(), position);
			default:
				return null;
		}

	}

	public LinkedList<Token> tokenize(){
		LinkedList<Token> tokenList = new LinkedList<>();

		while(!source.atEOD()) {
		  Token nextToken = genNextToken();
		  if (nextToken != null) tokenList.add(nextToken);
		}

		Token nextToken = Token.makeEofToken(position);
		tokenList.add(nextToken);

		return tokenList;
	}

	public static List<Token> filterWhiteSpace(List<Token> lexedList){
		LinkedList<Token> resultList = new LinkedList<>();

		for(Token tok : lexedList){
			if((tok.getTokenType() != Token.Type.NEWLINE) && (tok.getTokenType() != Token.Type.ESCAPEDLINE)){
				resultList.add(tok);
			}
		}

		return resultList;
	}

	public ErrorLog getErrorLog(){ return ErrorLog; }
}
