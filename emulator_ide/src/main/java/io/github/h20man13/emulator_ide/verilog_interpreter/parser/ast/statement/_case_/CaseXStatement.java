package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_;


import java.util.List;
import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.*;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class CaseXStatement extends CaseStatement {

    public CaseXStatement(Position start, Expression exp, List<CaseItem> itemList) {
        super(start, exp, itemList);
    }

    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
        return statVisitor.visit(this, argv);
    }
}