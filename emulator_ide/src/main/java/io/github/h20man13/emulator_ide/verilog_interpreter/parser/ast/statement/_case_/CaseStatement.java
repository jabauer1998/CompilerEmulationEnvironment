package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_;


import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item.*;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class CaseStatement extends AstNode implements Statement {
    public final Expression         exp;
    public final List<CaseItem> itemList;

    public CaseStatement(Position start, Expression exp, List<CaseItem> itemList) {
        super(start);
        this.exp = exp;
        this.itemList = itemList;
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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("case(");
        sb.append(exp.toString());
        sb.append(")\n");
        for(CaseItem item: itemList){
            sb.append(item.toString());
            sb.append(";\n");
        }
        sb.append("endcase");
        return sb.toString();
    }
}
