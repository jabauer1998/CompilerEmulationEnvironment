package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;

import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;

public class VectorDeclaration {

    public VectorDeclaration(Expression vectorIndex1, Expression vectorIndex2){
        this.vectorIndex1 = vectorIndex1;
        this.vectorIndex2 = vectorIndex2;
    }

    private Expression vectorIndex1;
    private Expression vectorIndex2;

    public Expression getExpression1(){ return vectorIndex1; }

    public Expression getExpression2(){ return vectorIndex2; }

}
