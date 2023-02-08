package edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstance;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.instantiation.ModuleInstantiation;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.AllwaysProcess;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process.InitialProcess;

public interface ModuleVisitor<ModVisitType> {

    /**
     * This is the top level visit statement used to visit a Verilog Module which should
     * allways be the root of the AST
     * 
     * @param  mod
     * @author     Jacob bauer
     */

    public ModVisitType visit(ModuleDeclaration mod, Object... argv);

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * 
     * @param stat
     */

    public ModVisitType visit(AllwaysProcess stat, Object... argv);

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * 
     * @param assign
     */

    public ModVisitType visit(ContinuousAssignment assign, Object... argv);

    /**
     * This is the code that is used to visit a function declaration in java
     * 
     * @param function
     */

    public ModVisitType visit(FunctionDeclaration function, Object... argv);

    /**
     * This is the code to visit a Initial Statement in Verilog
     * 
     * @param stat
     */

    public ModVisitType visit(InitialProcess stat, Object... argv);

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * 
     * @param mod
     */

    public ModVisitType visit(ModuleInstantiation mod, Object... argv);

    /**
     * This is the code to visit a Module instance in Verilog
     * 
     * @param mod
     */

    public ModVisitType visit(ModuleInstance mod, Object... argv);

    /**
     * This is used to visit a task declaration in verilog
     * 
     * @param task
     */

    public ModVisitType visit(TaskDeclaration task, Object... argv);

    public ModVisitType visit(EmptyModItem modItem, Object... argv);

    /**
     * This is used to visit any input wire scalar declaration in verilog. Ex. input a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Input.Wire.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any input wire scalar declaration in verilog. Ex. input a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Input.Reg.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Input.Wire.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Input.Reg.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Reg.Scalar.Array decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Reg.Scalar.Array decl, Object... argv);

    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Reg.Vector.Array decl, Object... argv);


    /**
     * This is used to visit any wire scalar wire declaration in verilog. Ex. wire a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Wire.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any wire vector declaration in verilog. Ex. wire [31:0] a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Wire.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Reg.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
     * ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Reg.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any output wire scalar declaration in verilog. Ex. output a, b,
     * c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Wire.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any output reg scalar declaration in verilog. Ex. output a, b,
     * c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Reg.Scalar.Ident decl, Object... argv);

    /**
     * This is used to visit any output wire vector declaration in verilog. Ex. output [2:0]
     * a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Wire.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any output reg vector declaration in verilog. Ex. output [2:0]
     * a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Reg.Vector.Ident decl, Object... argv);

    /**
     * This is used to visit any output reg vector declaration in verilog. Ex. output [2:0]
     * a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Output.Reg.Vector.Array decl, Object... argv);

    /**
     * This is used to visit any unidentified declaration in verilog. Ex. output [2:0] a, b,
     * c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Unidentified.Declaration decl, Object... argv);

    /**
     * This is used to visit any integer declaration in verilog. Ex. integer a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Int.Ident decl, Object... argv);

    /**
     * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Int.Array decl, Object... argv);

    /**
     * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(Real.Ident decl, Object... argv);

     /**
     * This is used to visit any real declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(RegValueList decls, Object... argv);

    /**
     * This is used to visit any andgate declaration in verilog. Ex. integer a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(AndGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any orgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(OrGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any nandgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(NandGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any norgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(NorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any xorgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(XorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any xnorgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(XnorGateDeclaration decl, Object... argv);

    /**
     * This is used to visit any notgate declaration in verilog. Ex. real a, b, c ... ;
     * 
     * @param decl
     */

    public ModVisitType visit(NotGateDeclaration decl, Object... argv);
}
