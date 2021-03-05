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
    
    public void visit(ModuleDeclaration mod);

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public ModVisitType visit(AllwaysStatement stat);

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public ModVisitType visit(ContinuousAssignment assign);

    /**
     * This is the code that is used to visit a function declaration in java
     * @param function
     */
    
    public ModVisitType visit(FunctionDeclaration function);

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public ModVisitType visit(InitialStatement stat);

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public ModVisitType visit(ModInstantiation mod);

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public ModVisitType visit(ModInstance mod);

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public ModVisitType visit(TaskDeclaration task);

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputScalarDeclaration decl);

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputVectorDeclaration decl);

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(WireScalarDeclaration decl);

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(WireVectorDeclaration decl);

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RegScalarDeclaration decl);

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RegVectorDeclaration decl);


    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputScalarDeclaration decl);

    /**
     * This is used to visit any output vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OutputVectorDeclaration decl);

    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(IntegerDeclaration decl);

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(RealDeclaration decl);

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(AndGateDeclaration decl);

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(OrGateDeclaration decl);

     /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NandGateDeclaration decl);

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NorGateDeclaration decl);

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(XorGateDeclaration decl);

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(XnorGateDeclaration decl);

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(NotGateDeclaration decl);





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public StatVisitType visit(Assignment assign);

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public StatVisitType visit(BlockAssign assign);

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseStatement stat);

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseXStatement stat);

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public StatVisitType visit(CaseZStatement stat);

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public StatVisitType visit(ForStatement forLoop);

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public StatVisitType visit(ForeverStatement foreverLoop);

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public StatVisitType visit(IfElseStatement ifElseStatement);

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public StatVisitType visit(IfStatement ifElseStatement);

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public StatVisitType visit(NonBlockAssign assign);

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public StatVisitType visit(RepeatStatement stat);

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
     public StatVisitType visit(SeqBlockStatement stat);

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
     public StatVisitType visit(TaskStatement task);

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
     public StatVisitType visit(WaitStatement wait);

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
     public StatVisitType visit(WhileStatement whileLoop);


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public StatVisitType visit(EmptyStatement stat);

    
    /*
     * Below is the code that is used for visiting Expressions
     */


    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public ExprVisitType visit(BinaryOperation op);

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public ExprVisitType visit(UnaryOperation op);


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public ExprVisitType visit(Concatenation concat);

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public ExprVisitType visit(ConstantExpression expr);

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public ExprVisitType visit(EmptyExpression  expr);

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public ExprVisitType visit(FunctionCall call);

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public ExprVisitType visit(Identifier ident);

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public ExprVisitType visit(NumValue number);

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public ExprVisitType visit(PortConnection connection);

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public ExprVisitType visit(StrValue string);

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public ExprVisitType visit(TernaryExpression expr);

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public ExprVisitType visit(VectorCall string);
}
