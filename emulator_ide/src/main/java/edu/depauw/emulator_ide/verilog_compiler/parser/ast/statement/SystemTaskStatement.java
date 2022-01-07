package edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import java.util.List;
import java.util.ArrayList;

public class SystemTaskStatement extends Statement {

    private final Identifier     functionName;
    private final ExpressionList expList;

    public SystemTaskStatement(Identifier functionName) {
        super(functionName.getPosition());
        this.functionName = functionName;
        this.expList = new ExpressionList(new ArrayList<>());
    }

    public SystemTaskStatement(Identifier functionName, ExpressionList expList) {
        super(functionName.getPosition());
        this.functionName = functionName;
        this.expList = expList;
    }

    public Identifier getSystemTaskName(){ return this.functionName; }

    public Expression getExpression(int index){ return expList.getExpression(index); }

    public void setExpression(int index, Expression exp){ this.expList.setExpression(index, exp); }

    public int numExpressions(){ return expList.getSize(); }

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
