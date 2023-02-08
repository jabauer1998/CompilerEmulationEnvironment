package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.StatementVisitor;

public class SeqBlockStatement extends AstNode implements Statement {

    public final List<Statement> statementList;

    public SeqBlockStatement(Position start, List<Statement> statementList) {
        super(start);
        this.statementList = statementList;
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
