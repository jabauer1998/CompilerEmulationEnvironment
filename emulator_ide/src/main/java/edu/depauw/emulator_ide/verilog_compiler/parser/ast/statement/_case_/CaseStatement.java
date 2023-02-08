package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement._case_.item.*;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.StatementVisitor;
import java.util.List;
import edu.depauw.emulator_ide.common.Position;

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
}
