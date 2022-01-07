package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.gate_declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public abstract class GateDeclaration extends ModItem {

    private final ExpressionList exprList;

    protected GateDeclaration(ExpressionList exprList) {
        super(exprList.getExpression(0).getPosition());
        this.exprList = exprList;
    }

    protected ExpressionList getExpressionList(){ return exprList; }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
