package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;
    
public class ConstantExpressionVisitor implements AstNodeVisitor<Void, Void, Object>{
    
    private InfoLog errorLog;
    
    public ConstantExpressionVisitor(InfoLog errorLog){
	this.dest = dest;
	this.errorLog = errorLog;
	this.node = node;
    }
    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod
     *@author Jacob bauer
     */
    
    public void visit(ModuleDeclaration mod){
	
    }

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public Void visit(AllwaysStatement stat){
	return null;
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public Void visit(ContinuousAssignment assign){
	return null;
    }

    /**
     * This is the code that is used to visit a function declaration in java
     * @param function
     */
    
    public Void visit(FunctionDeclaration function){
	return null;
    }

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public Void visit(InitialStatement stat){
	return null;
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod){
	return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod){
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(TaskDeclaration task){
	return null;
    }

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputRegScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputRegVectorDeclaration decl){
	return null;
    }


    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputScalarDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any output vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputVectorDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(IntegerDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any unidentified declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(UnidentifiedDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RealDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OrGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NandGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NorGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XorGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XnorGateDeclaration decl){
	return null;
    }

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NotGateDeclaration decl){
	return null;
    }





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public Void visit(Assignment assign){
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign){
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat){
	return null;
    }

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public Void visit(CaseXStatement stat){
	return null;
    }

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public Void visit(CaseZStatement stat){
	return null;
    }

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public Void visit(ForStatement forLoop){
	return null;
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop){
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement){
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement){
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign){
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat){
	return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat){
	return null;
    }

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
    public Void visit(TaskStatement task){
	return null;
    }

    /**
     * This is used to visit a systemtaskcall in verilog
     * @param stat
     */
     
    public Void visit(SystemTaskStatement task){
	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait){
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop){
	return null;
    }


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public Void visit(EmptyStatement stat){
	//this is empty it is just a placeholder
	return null;
    }

    
    /*
     * Below is the code that is used for visiting Expressions
     */


    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public Object visit(BinaryOperation op){
	Object left = op.getLeft().accept(this);
	Object right = op.getRight().accept(this);
	if(left != null && right != null){
	    switch(op.getOp().getOpType()){
	    case PLUS:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left - (int)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (int)left - (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left - (double)right;
		else if(left instanceof Integer && right instanceof Integer)
		    return (int)left - (int)right;
		else if(left instanceof String && right instanceof Integer)
		    return (String)left + (int)right;
		else if(left instanceof String && right instanceof String)
		    return (String)left + (String)right;
		else
		    return (String)left + (Double)right;
	    case MINUS:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left - (int)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (int)left - (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left - (double)right;
		else
		    return (int)left - (int)right;
	    case TIMES:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left * (int)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (int)left * (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left * (double)right;
		else
		    return (int)left * (int)right;
	    case MOD: return (int)left % (int)right;
	    case DIV:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left / (int)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (int)left / (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left / (double)right;
		else
		    return (int)left / (int)right;
	    case EQ2:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left == (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left == (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left == (double)right;
		else
		    return (int)left == (int)right;
	    case EQ3:
		if(left instanceof Double && right instanceof Double)
		    return (double)left == (double)right;
		else
		    return (int)left == (int)right;
	    case NE1:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left != (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left != (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left != (double)right;
		else
		    return (int)left != (int)right;
	    case NE2:
		if(left instanceof Double && right instanceof Double)
		    return (double)left != (double)right;
		else
		    return (int)left != (int)right;
	    case LAND: return (boolean)left || (boolean)right;
	    case LOR: return (boolean)left || (boolean)right;
	    case LT:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left < (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left < (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left < (double)right;
		else
		    return (int)left < (int)right;
	    case LE:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left <= (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left <= (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left <= (double)right;
		else
		    return (int)left <= (int)right;
	    case GT:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left > (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left > (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left > (double)right;
		else
		    return (int)left > (int)right;
	    case GE:
		if(left instanceof Double && right instanceof Integer)
		    return (double)left >= (double)right;
		else if(left instanceof Integer && right instanceof Double)
		    return (double)left >= (double)right;
		else if(left instanceof Double && right instanceof Double)
		    return (double)left >= (double)right;
		else
		    return (int)left >= (int)right;
	    case BAND: return (int)left & (int)right;
	    case BOR: return (int)left | (int)right;
	    case BXOR: return (int)left ^ (int)right;
	    case BXNOR: return ~((int)left ^ (int)right);
	    case LSHIFT: return (int)left << (int)right;
	    case RSHIFT: return (int)left >> (int)right;
	    default:
		return null;
	    }
	}
	return null;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public Object visit(UnaryOperation op){
	Object right = op.getRight().accept(this);
	if(right == null){
	    return null;
	} else {
	    if(right instanceof Integer){
		if(op.getOp().getOpType() == UnOp.Type.BNEG){
		    return ~(int)right;
		} else if (op.getOp().getOpType() == UnOp.Type.PLUS){
		    return right;
		} else {
		    return null;
		}
	    } else if(right instanceof Boolean){
		if(op.getOp().getOpType() == UnOp.Type.LNEG){
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
     * @param concat
     */

     public Object visit(Concatenation concat){
	return null;
     }
    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public Object visit(ConstantExpression expr){
	return expr.getExpression().accept(this);
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public Object visit(EmptyExpression  expr){
	//this is just a placeholder we do not need to put anything here
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Object visit(FunctionCall call){
	return null;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Object visit(SystemFunctionCall call){
	return null;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public Object visit(Identifier ident){
	return null;
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public Object visit(NumValue number){
	String lexeme = number.getLexeme();
	if(lexeme.contains("\'")){
	    int type = NumberUtil.getBase(lexeme);
	    switch(type){
	    case 2: return NumberUtil.getBinary(lexeme); 
	    case 8: return NumberUtil.getOctal(lexeme); 
	    case 10: return NumberUtil.getDecimal(lexeme);
	    case 16: return NumberUtil.getHexidecimal(lexeme);
	    default: return null;
	    }
	} else if (lexeme.contains(".")){
	    return Double.parseDouble(lexeme);
	} else {
	    return Integer.parseInt(lexeme);
	}
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public Object visit(PortConnection connection){
	return null;
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public Object visit(StrValue string){
	// do nothing
	return null;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public Void visit(TernaryExpression expr){
	return null;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Object visit(VectorCall vector){
	return null;
    }

}
