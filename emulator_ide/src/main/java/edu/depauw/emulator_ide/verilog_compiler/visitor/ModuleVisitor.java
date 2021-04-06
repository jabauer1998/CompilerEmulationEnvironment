package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
    
public interface ModuleVisitor<ModVisitType>{

    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod 
     *@author Jacob bauer
     */
    
    public ModVisitType visit(ModuleDeclaration mod, Object... argv);

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
     * This is used to visit a macro declaration in verilog
     * @param macro
     */
    
    public ModVisitType visit(MacroDefinition macro, Object... argv);

    /**
     * This is used to visit any input wire scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public ModVisitType visit(InputWireScalarDeclaration decl, Object... argv);

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
}
