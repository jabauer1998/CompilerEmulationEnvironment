package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list;


import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Identifier;
import java.util.Collections;

public class IdentifierList extends AstNode {

    private final List<Identifier> identList;

    public IdentifierList(List<Identifier> identList) {
        super(identList.isEmpty() ? null : identList.get(0).getPosition());
        this.identList = Collections.unmodifiableList(identList);
    }

    public Identifier getIdentifier(int index){ return identList.get(index); }

    public int getSize(){ return identList.size(); }

}
