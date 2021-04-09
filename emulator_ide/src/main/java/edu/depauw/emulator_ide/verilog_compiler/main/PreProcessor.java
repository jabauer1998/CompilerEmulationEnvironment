package edu.depauw.emulator_ide.verilog_compiler.main;

import edu.depauw.emulator_ide.verilog_compiler.visitor.*;
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
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.*;
import edu.depauw.emulator_ide.verilog_compiler.main.util.NumberUtil;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.TypeCheckerVariableData;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.TypeCheckerFunctionData;
import edu.depauw.emulator_ide.common.io.*;
import edu.depauw.emulator_ide.common.debug.*;
import edu.depauw.emulator_ide.common.debug.item.*;
    
public class PreProcessor implements ExpressionVisitor<Expression>, StatementVisitor<Void>, ModuleVisitor<Void>, RegValueVisitor<Void>{
    
    private final Environment<String, Expression> macroEnv;
    private final InfoLog errorLog;
    
    public PreProcessor(InfoLog errorLog){
	this.macroEnv = new Environment<>();
	this.errorLog = errorLog;
    }

    public InfoLog getErrorLog(){
	return this.errorLog;
    }
    
    public Void visit(ModuleDeclaration mod, Object... argv){
	macroEnv.addScope();
	for(int i = 0; i < mod.numParameters(); i++){
	    mod.getParameter(i).accept(this);
	}
	for(int i = 0; i < mod.numModItems(); i++){
	    mod.getModItem(i).accept(this);
	}
	macroEnv.removeScope();
	return null;
    }

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public Void visit(AllwaysStatement stat, Object... argv){
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public Void visit(ContinuousAssignment assign, Object... argv){
	for(int i = 0; i < assign.numAssignments(); i++){
	    assign.getAssignment(i).accept(this);
	}
	return null;
    }

    public Void visit(FunctionDeclaration function, Object... argv){
	function.getFunctionName().accept(this);
	for(int i = 0; i < function.numDeclarations(); i++){
	    function.getDeclaration(i).accept(this);
	}
	function.getStatement().accept(this);
	return null;
    }

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public Void visit(InitialStatement stat, Object... argv){
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod, Object... argv){
	for(int i = 0; i < mod.numModInstances(); i++){
	    mod.getModInstance(i).accept(this);
	}
	return null;
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod, Object... argv){
	for(int i = 0; i < mod.numExpressions(); i++){
	    Expression exp = mod.getExpression(i).accept(this);
	    mod.setExpression(i, exp);
	}
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(TaskDeclaration task, Object... argv){
	for(int i = 0; i < task.numDeclarations(); i++){
	    task.getDeclaration(i).accept(this);
	}
	task.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(MacroDefinition macro, Object... argv){
	String lexeme = macro.getIdentifier().getLexeme();
	if(macroEnv.entryExists(lexeme)){
	    errorLog.addItem(new ErrorItem("Redefinition of Macro " +  lexeme, macro.getPosition()));
	} else {
	    macroEnv.addEntry(lexeme, macro.getExpression());
	}
	return null; //this should allready be handled at this point
    }
    
    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireScalarDeclaration decl, Object... argv){
	return null;
    }

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputWireVectorDeclaration decl, Object... argv){
	Expression exp1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	Expression exp2 = decl.getExpression2().accept(this);

	decl.setExpression1(exp1);
	decl.setExpression2(exp2);
	return null;
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireScalarDeclaration decl, Object... argv){
	return null;
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireVectorDeclaration decl, Object... argv){
	Expression exp1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	Expression exp2 = decl.getExpression2().accept(this);

	decl.setExpression1(exp1);
	decl.setExpression2(exp2);
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegVectorDeclaration decl, Object... argv){

	Expression exp1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	Expression exp2 = decl.getExpression2().accept(this);

	decl.setExpression1(exp1);
	decl.setExpression2(exp2);
	
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputWireScalarDeclaration decl, Object... argv){
	return null;
    }

    /** This is where I will declare the output Register Scalar declaration
     * @param Jacob Bauer
     */

    public Void visit(OutputRegScalarDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    public Void visit(OutputWireVectorDeclaration decl, Object... argv){
	Expression exp1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	Expression exp2 = decl.getExpression2().accept(this);

	decl.setExpression1(exp1);
	decl.setExpression2(exp2);
	return null;
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputRegVectorDeclaration decl, Object... argv){
	
	Expression exp1 = decl.getExpression1().accept(this); //check whether the expressions return ints
	Expression exp2 = decl.getExpression2().accept(this);

	decl.setExpression1(exp1);
	decl.setExpression2(exp2);
	    
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }
    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(IntegerDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numRegValues(); i++){
	    decl.getRegValue(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RealDeclaration decl, Object... argv){
	return null;
    }

     /**
     * This is used to visit any Unidentified declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(UnidentifiedDeclaration decl, Object... argv){
	return null;
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OrGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }

     /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NandGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XnorGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NotGateDeclaration decl, Object... argv){
	for(int i = 0; i < decl.numExpressions(); i++){
	    Expression exp = decl.getExpression(i).accept(this);
	    decl.setExpression(i, exp);
	}
	return null;
    }





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public Void visit(Assignment assign, Object... argv){
	Expression exp = assign.getLValue().accept(this);
	assign.setLValue(exp);
	exp = assign.getExpression().accept(this);
	assign.setExpression(exp);
	return null;
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign, Object... argv){
	Expression exp = assign.getLValue().accept(this);
	assign.setLValue(exp);
	exp = assign.getExpression().accept(this);
	assign.setExpression(exp);
	return null;
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat, Object... argv){
	Expression exp = stat.getExpression().accept(this);
	stat.setExpression(exp);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Expression expInner = exprItem.getExpression(x).accept(this);
		    exprItem.setExpression(x, expInner);
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public Void visit(CaseXStatement stat, Object... argv){
	Expression exp = stat.getExpression().accept(this);
	stat.setExpression(exp);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Expression expInner = exprItem.getExpression(x).accept(this);
		    exprItem.setExpression(x, expInner);
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public Void visit(CaseZStatement stat, Object... argv){
	Expression exp = stat.getExpression().accept(this);
	stat.setExpression(exp);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExprCaseItem){
		ExprCaseItem exprItem = (ExprCaseItem)item;
		for(int x = 0; x < exprItem.numExpressions(); x++){
		    Expression expInner = exprItem.getExpression(x).accept(this);
		    exprItem.setExpression(x, expInner);
		}
	    }
	    item.getStatement().accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public Void visit(ForStatement forLoop, Object... argv){
	forLoop.getInit().accept(this);
	Expression exp = forLoop.getExpression().accept(this);
	forLoop.setExpression(exp);
	forLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop, Object... argv){
	foreverLoop.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement, Object... argv){
	Expression exp = ifElseStatement.getExpression().accept(this);
	ifElseStatement.setExpression(exp);
	ifElseStatement.getIfStatement().accept(this);
	ifElseStatement.getElseStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement, Object... argv){
	Expression exp = ifStatement.getExpression().accept(this);
	ifStatement.setExpression(exp);
	ifStatement.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign, Object... argv){
	Expression exp = assign.getLValue().accept(this);
	assign.setLValue(exp);
	exp = assign.getExpression().accept(this);
	assign.setExpression(exp);
	return null;
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat, Object... argv){
	stat.getExpression().accept(this);
	stat.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat, Object... argv){
	for(int i = 0; i < stat.numStatements(); i++){
	    stat.getStatement(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
    public Void visit(TaskStatement task, Object... argv){
	for(int i = 0; i < task.numExpressions(); i++){
	    task.getExpression(i).accept(this);
	}
	return null;
    }

    /**
     * This is used to visit a system task statement in verilog
     * @param stat
     */
     
    public Void visit(SystemTaskStatement task, Object... argv){
	for(int i = 0; i < task.numExpressions(); i++){
	    task.getExpression(i).accept(this);
	}
      	return null;
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait, Object... argv){
	Expression exp = wait.getExpression().accept(this);
	wait.setExpression(exp);
        wait.getStatement().accept(this);
	return null;
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop, Object... argv){
	Expression exp = whileLoop.getExpression().accept(this);
	whileLoop.setExpression(exp);
	whileLoop.getStatement().accept(this);
	return null;
    }


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public Void visit(EmptyStatement stat, Object... argv){
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
    
    public Expression visit(BinaryOperation op, Object... argv){
	Expression left = op.getLeft().accept(this);
	if(left != null){
	    op.setLeft(left);
	}
	Expression right = op.getRight().accept(this);
	if(right != null){
	    op.setRight(right);
	}
	return op;
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public Expression visit(UnaryOperation op, Object... argv){
	Expression right = op.getRight().accept(this);
	op.setRight(right);
	return op;
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public Expression visit(Concatenation concat, Object... argv){
	for(int i = 0; i < concat.numExpressions(); i++){
	    Expression exp = concat.getExpression(i).accept(this);
	    concat.setExpression(i, exp);
	}
	return concat;
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public Expression visit(ConstantExpression expr, Object... argv){
	Expression exp = expr.getExpression().accept(this);
	expr.setExpression(exp);
	return expr;
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public Expression visit(EmptyExpression expr, Object... argv){
	return expr;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Expression visit(FunctionCall call, Object... argv){
	for(int i = 0; i < call.numExpressions(); i++){
	    Expression exp = call.getExpression(i).accept(this);
	    call.setExpression(i, exp);
	}
	return call;
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Expression visit(SystemFunctionCall call, Object... argv){
	for(int i = 0; i < call.numExpressions(); i++){
	    Expression exp = call.getExpression(i).accept(this);
	    call.setExpression(i , exp);
	}
	return call;
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public Expression visit(Identifier ident, Object... argv){
	return ident;
    }

    public Expression visit(MacroIdentifier ident, Object... argv){
	String lexeme = ident.getLexeme().substring(1);
	if(macroEnv.entryExists(lexeme)){
	    return macroEnv.getEntry(lexeme);
	} else {
	    errorLog.addItem(new ErrorItem("Macro by the name of " + ident.getLexeme() + " not found ", ident.getPosition()));
	    return ident;
	}
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public Expression visit(NumValue number, Object... argv){
	return number;
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public Expression visit(PortConnection connection, Object... argv){
	Expression exp = connection.getExpression().accept(this);
	connection.setExpression(exp);
	return connection;
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public Expression visit(StrValue string, Object... argv){
	return string;
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public Expression visit(TernaryExpression expr, Object... argv){
	Expression exp = expr.getCondition().accept(this);
	expr.setCondition(exp);
	expr.getLeft().accept(this);
	expr.getRight().accept(this);
	return expr;
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Expression visit(VectorCall vector, Object... argv){
	Expression exp = vector.getExpression().accept(this);
	vector.setExpression(exp);
	return vector;
    }

    public Expression visit(VectorSlice vector, Object... argv){
	Expression exp1 = vector.getExpression1().accept(this);
	vector.setExpression1(exp1);
	Expression exp2 = vector.getExpression2().accept(this);
	vector.setExpression2(exp2);
	return vector;
    }

    /*
     *Below are RegValue visitors. These are used in the declarations of Integers and Registers
     */

    public Void visit(RegVectorIdent regVector, Object... argv){
	return null;
    }

    public Void visit(RegScalarIdent regScalar, Object... argv){
	return null;
    }

    public Void visit(OutputRegVectorIdent regVector, Object... argv){
	return null;
    }

    public Void visit(OutputRegScalarIdent regScalar, Object... argv){
	return null;
    }

    public Void visit(IntegerIdent intIdent, Object... argv){
	return null;
    }

    public Void visit(RegVectorArray regVector, Object... argv){
	Expression exp = regVector.getExpression1().accept(this);
	regVector.setExpression1(exp);
	exp = regVector.getExpression2().accept(this);
	regVector.setExpression2(exp);
	return null;
    }

    public Void visit(RegScalarArray regScalar, Object... argv){
	Expression exp = regScalar.getExpression1().accept(this);
	regScalar.setExpression1(exp);
	exp = regScalar.getExpression2().accept(this);
	regScalar.setExpression2(exp);
	return null;
    }

    public Void visit(OutputRegVectorArray regVector, Object... argv){
	Expression exp = regVector.getExpression1().accept(this);
	regVector.setExpression1(exp);
	exp = regVector.getExpression2().accept(this);
	regVector.setExpression2(exp);
	return null;
    }

    public Void visit(OutputRegScalarArray regScalar, Object... argv){
	Expression exp = regScalar.getExpression1().accept(this);
	regScalar.setExpression1(exp);
	exp = regScalar.getExpression2().accept(this);
	regScalar.setExpression2(exp);
	return null;
    }

    public Void visit(IntegerArray intIdent, Object... argv){
	Expression exp = intIdent.getExpression1().accept(this);
	intIdent.setExpression1(exp);
	exp = intIdent.getExpression2().accept(this);
	intIdent.setExpression2(exp);
	return null;
    }
}
