package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.gate_declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class NorGateDeclaration extends GateDeclaration {

    public NorGateDeclaration(ExpressionList exprList) { super(exprList); }

    public Expression getExpression(int index){ return super.getExpressionList().getExpression(index); }

    public void setExpression(int index, Expression exp){ super.getExpressionList().setExpression(index, exp); }

    public int numExpressions(){ return super.getExpressionList().getSize(); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

}
