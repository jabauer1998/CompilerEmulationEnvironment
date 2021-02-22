package edu.depauw.emulator_ide.verilog_compiler.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

import edu.depauw.emulator_ide.verilog_compiler.token.Token;

public class BinOp {
    
    public enum Type{
	PLUS,
	MINUS,
	TIMES,
	MOD,
	DIV,
	EQ2,
	EQ3,
	NE1,
	NE2,
	LAND,
	LOR,
	LT,
	LE,
	GT,
	GE,
	BAND,
	BOR,
	BXOR,
	BXNOR,
	LSHIFT,
	RSHIFT
    }

    private Type type;

    public BinOp(Token op){
	switch(op.getTokenType()){
	case PLUS:
	    type = Type.PLUS;
	    break;
	case MINUS:
	    type = Type.MINUS;
	    break;
	case TIMES:
	    type = Type.TIMES;
	    break;
	case MOD:
	    type = Type.MOD;
	    break;
	case DIV:
	    type = Type.DIV;
	    break;
	case EQ2:
	    type = Type.EQ2;
	    break;
	case EQ3:
	    type = Type.EQ3;
	    break;
	case NE1:
	    type = Type.NE1;
	    break;
	case NE2:
	    type = Type.NE2;
	    break;
	case LAND:
	    type = Type.LAND;
	    break;
	case LOR:
	    type = Type.LOR;
	    break;
	case LT:
	    type = Type.LT;
	    break;
	case LE:
	    type = Type.LE;
	    break;
	case GT:
	    type = Type.GT;
	    break;
	case GE:
	    type = Type.GE;
	    break;
	case BAND:
	    type = Type.BAND;
	    break;
	case BOR:
	    type = Type.BOR;
	    break;
	case BXOR:
	    type = Type.BXOR;
	    break;
	case BXNOR:
	    type = Type.BXNOR;
	    break;
	case LSHIFT:
	    type = Type.LSHIFT;
	    break;
	case RSHIFT:
	    type = Type.RSHIFT;
	    break;
	}
    }

    public Type getOpType(){
	return type;
    }
    
    public static boolean isBinOp(Token.Type opType){
	if(opType == Token.Type.PLUS || opType == Token.Type.MINUS || opType == Token.Type.TIMES || opType == Token.Type.MOD || opType == Token.Type.DIV || opType == Token.Type.EQ2 || opType == Token.Type.EQ3 || opType == Token.Type.NE1 || opType == Token.Type.NE2 || opType == Token.Type.LAND || opType == Token.Type.LOR || opType == Token.Type.LT || opType == Token.Type.LE || opType == Token.Type.GT || opType == Token.Type.GE || opType == Token.Type.BAND || opType == Token.Type.BOR || opType == Token.Type.BXOR || opType == Token.Type.BXNOR || opType == Token.Type.LSHIFT || opType == Token.Type.RSHIFT){
	    return true;
	} else {
	    return false;
	}
    }
}
