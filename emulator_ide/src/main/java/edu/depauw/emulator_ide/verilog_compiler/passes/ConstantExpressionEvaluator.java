package edu.depauw.emulator_ide.verilog_compiler.passes;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.passes.util.NumberUtil;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.*;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;

public class ConstantExpressionEvaluator implements ExpressionVisitor<Object> {

	private ErrorLog errorLog;

	public ConstantExpressionEvaluator(ErrorLog errorLog) { this.errorLog = errorLog; }

	/**
	 * This is the code for visiting binary operations
	 * 
	 * @param op
	 */

	public Object visit(BinaryOperation op, Object... argv){
		Object left = op.getLeft().accept(this);
		Object right = op.getRight().accept(this);

		if (left != null && right != null) {

			switch(op.getOp()){
				case PLUS:
					if (left instanceof Double && right instanceof Integer)
						return (double)left - (int)right;
					else if (left instanceof Integer && right instanceof Double)
						return (int)left - (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left - (double)right;
					else if (left instanceof Integer && right instanceof Integer)
						return (int)left - (int)right;
					else if (left instanceof String && right instanceof Integer)
						return (String)left + (int)right;
					else if (left instanceof String && right instanceof String)
						return (String)left + (String)right;
					else
						return (String)left + (Double)right;
				case MINUS:
					if (left instanceof Double && right instanceof Integer)
						return (double)left - (int)right;
					else if (left instanceof Integer && right instanceof Double)
						return (int)left - (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left - (double)right;
					else
						return (int)left - (int)right;
				case TIMES:
					if (left instanceof Double && right instanceof Integer)
						return (double)left*(int)right;
					else if (left instanceof Integer && right instanceof Double)
						return (int)left*(double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left*(double)right;
					else
						return (int)left*(int)right;
				case MOD:
					return (int)left%(int)right;
				case DIV:
					if (left instanceof Double && right instanceof Integer)
						return (double)left/(int)right;
					else if (left instanceof Integer && right instanceof Double)
						return (int)left/(double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left/(double)right;
					else
						return (int)left/(int)right;
				case EQ2:
					if (left instanceof Double && right instanceof Integer)
						return (double)left == (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left == (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left == (double)right;
					else
						return (int)left == (int)right;
				case EQ3:
					if (left instanceof Double && right instanceof Double)
						return (double)left == (double)right;
					else
						return (int)left == (int)right;
				case NE1:
					if (left instanceof Double && right instanceof Integer)
						return (double)left != (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left != (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left != (double)right;
					else
						return (int)left != (int)right;
				case NE2:
					if (left instanceof Double && right instanceof Double)
						return (double)left != (double)right;
					else
						return (int)left != (int)right;
				case LAND:
					return (boolean)left || (boolean)right;
				case LOR:
					return (boolean)left || (boolean)right;
				case LT:
					if (left instanceof Double && right instanceof Integer)
						return (double)left < (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left < (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left < (double)right;
					else
						return (int)left < (int)right;
				case LE:
					if (left instanceof Double && right instanceof Integer)
						return (double)left <= (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left <= (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left <= (double)right;
					else
						return (int)left <= (int)right;
				case GT:
					if (left instanceof Double && right instanceof Integer)
						return (double)left > (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left > (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left > (double)right;
					else
						return (int)left > (int)right;
				case GE:
					if (left instanceof Double && right instanceof Integer)
						return (double)left >= (double)right;
					else if (left instanceof Integer && right instanceof Double)
						return (double)left >= (double)right;
					else if (left instanceof Double && right instanceof Double)
						return (double)left >= (double)right;
					else
						return (int)left >= (int)right;
				case BAND:
					return (int)left & (int)right;
				case BOR:
					return (int)left | (int)right;
				case BXOR:
					return (int)left ^ (int)right;
				case BXNOR:
					return ~((int)left ^ (int)right);
				case LSHIFT:
					return (int)left<<(int)right;
				case RSHIFT:
					return (int)left>>(int)right;
				default:
					return null;
			}

		}

		return null;
	}

	/**
	 * This is the code for visiting unary operations
	 * 
	 * @param op
	 */

	public Object visit(UnaryOperation op, Object... argv){
		Object right = op.getRight().accept(this);

		if (right == null) {
			return null;
		} else {

			if (right instanceof Integer) {

				if (op.getOp() == UnaryOperation.Type.BNEG) {
					return ~(int)right;
				} else if (op.getOp() == UnaryOperation.Type.PLUS) {
					return right;
				} else {
					return null;
				}

			} else if (right instanceof Boolean) {

				if (op.getOp() == UnaryOperation.Type.LNEG) {
					return !(boolean)right;
				} else {
					return null;
				}

			}

		}

		return null;
	}

	/**
	 * This is the code for visiting concatenations
	 * 
	 * @param concat
	 */

	public Object visit(Concatenation concat, Object... argv){ return null; }

	/**
	 * This is the code for visiting Constant Expressions
	 * 
	 * @param expr
	 */

	public Object visit(ConstantExpression expr, Object... argv){ return expr.getExpression().accept(this); }

	/**
	 * This is the code for visiting Empty Expressions
	 * 
	 * @param expr
	 */

	public Object visit(EmptyExpression expr, Object... argv){
		// this is just a placeholder we do not need to put anything here
		return null;
	}

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public Object visit(FunctionCall call, Object... argv){ return null; }

	/**
	 * This is the code for visiting Function Calls
	 * 
	 * @param call
	 */

	public Object visit(SystemFunctionCall call, Object... argv){ return null; }

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 */

	public Object visit(Identifier ident, Object... argv){ return null; }

	/**
	 * This is the code for visiting an Number in verilog
	 * 
	 * @param number
	 */

	public Object visit(NumValue number, Object... argv){
		String lexeme = number.getLexeme();

		if (lexeme.contains("\'")) {
			int type = NumberUtil.getBase(lexeme);

			switch(type){
				case 2:
					return NumberUtil.getBinary(lexeme);
				case 8:
					return NumberUtil.getOctal(lexeme);
				case 10:
					return NumberUtil.getDecimal(lexeme);
				case 16:
					return NumberUtil.getHexidecimal(lexeme);
				default:
					return null;
			}

		} else if (lexeme.contains(".")) {
			return Double.parseDouble(lexeme);
		} else {
			return Integer.parseInt(lexeme);
		}

	}

	/**
	 * This is the code for visiting a port connection in verilog
	 * 
	 * @param connection
	 */

	public Object visit(PortConnection connection, Object... argv){ return null; }

	/**
	 * This is the code for visiting a string in verilog
	 * 
	 * @param string
	 */

	public Object visit(StrValue string, Object... argv){
		// do nothing
		return null;
	}

	/**
	 * This is the code for visiting a TernaryOperation in verilog
	 * 
	 * @param expr
	 */

	public Void visit(TernaryOperation expr, Object... argv){ return null; }

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public Object visit(VectorElement vector, Object... argv){ return null; }

	/**
	 * This is the code for visiting a Vector in verilog
	 * 
	 * @param string
	 */

	public Object visit(VectorSlice vector, Object... argv){ return null; }

}
