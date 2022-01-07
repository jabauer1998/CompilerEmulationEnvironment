package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public abstract class ScalarDeclaration extends Declaration {

    protected ScalarDeclaration(Position position) { super(position); }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
