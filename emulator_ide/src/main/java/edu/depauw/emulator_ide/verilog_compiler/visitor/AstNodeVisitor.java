package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
    
public interface AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType>{

    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod 
     *@author Jacob bauer
     */
    
    public void visit(ModuleDeclaration mod, Object... argv);

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public ModVisitType visit(AllwaysStatement stat, Object... argv);

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public ModVisitType visit(ContinuousAssignment assign, Object... argv);

    /**
     * This is the code that is used to visit a function declaration in java
     * @param function
     */
    
    public ModVisitType visit(FunctionDeclaration function, Object... argv);

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public ModVisitType visit(InitialStatement stat, Object... argv);

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public ModVisitType visit(ModInstantiation mod, Object... argv);

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public ModVisitType visit(ModInstance mod, Object... argv);

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public ModVisitType visit(TaskDeclaration task, Object... argv);

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any input wire scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputWireScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputVectorDeclaration decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */

    public ModVisitType visit(InputWireVectorDeclaration decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(WireScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(WireVectorDeclaration decl, Object... argv);

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RegScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RegVectorDeclaration decl, Object... argv);


    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any output wire scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputWireScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any output reg scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputRegScalarDeclaration decl, Object... argv);

    /**
     * This is used to visit any output vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputVectorDeclaration decl, Object... argv);

     /**
     * This is used to visit any output wire vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputWireVectorDeclaration decl, Object... argv);

     /**
     * This is used to visit any output reg vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputRegVectorDeclaration decl, Object... argv);

     /**
     * This is used to visit any unidentified declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(UnidentifiedDeclaration decl, Object... argv);

    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(IntegerDeclaration decl, Object... argv);

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RealDeclaration decl, Object... argv);

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(AndGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OrGateDeclaration decl, Object... argv);

     /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NandGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(XorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(XnorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NotGateDeclaration decl, Object... argv);





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public StatVisitType visit(Assignment assign, Object... argv);

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public StatVisitType visit(BlockAssign assign, Object... argv);

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseStatement stat, Object... argv);

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseXStatement stat, Object... argv);

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseZStatement stat, Object... argv);

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public StatVisitType visit(ForStatement forLoop, Object... argv);

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public StatVisitType visit(ForeverStatement foreverLoop, Object... argv);

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public StatVisitType visit(IfElseStatement ifElseStatement, Object... argv);

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public StatVisitType visit(IfStatement ifElseStatement, Object... argv);

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public StatVisitType visit(NonBlockAssign assign, Object... argv);

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public StatVisitType visit(RepeatStatement stat, Object... argv);

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
     public StatVisitType visit(SeqBlockStatement stat, Object... argv);

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
     public StatVisitType visit(TaskStatement task, Object... argv);

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
     public StatVisitType visit(SystemTaskStatement task, Object... argv);

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
     public StatVisitType visit(WaitStatement wait, Object... argv);

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
     public StatVisitType visit(WhileStatement whileLoop, Object... argv);


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public StatVisitType visit(EmptyStatement stat, Object... argv);

    
    /*
     * Below is the code that is used for visiting Expressions
     */


    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public ExprVisitType visit(BinaryOperation op, Object... argv);

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public ExprVisitType visit(UnaryOperation op, Object... argv);


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public ExprVisitType visit(Concatenation concat, Object... argv);

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public ExprVisitType visit(ConstantExpression expr, Object... argv);

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public ExprVisitType visit(EmptyExpression  expr, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public ExprVisitType visit(FunctionCall call, Object... argv);

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public ExprVisitType visit(SystemFunctionCall call, Object... argv);

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public ExprVisitType visit(Identifier ident, Object... argv);

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public ExprVisitType visit(NumValue number, Object... argv);

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public ExprVisitType visit(PortConnection connection, Object... argv);

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public ExprVisitType visit(StrValue string, Object... argv);

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public ExprVisitType visit(TernaryExpression expr, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public ExprVisitType visit(VectorCall string, Object... argv);

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public ExprVisitType visit(VectorSlice string, Object... argv);
}
