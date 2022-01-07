package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list;


import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import java.util.Collections;

public class ExpressionList extends AstNode {

    private final List<Expression> expList;

    public ExpressionList(List<Expression> expList) {
        super(expList.isEmpty() ? null : expList.get(0).getPosition());
        this.expList = Collections.unmodifiableList(expList);
    }

    public Expression getExpression(int index){ return expList.get(index); }

    public Expression setExpression(int index, Expression exp){ return expList.set(index, exp); }

    public int getSize(){ return expList.size(); }

}
