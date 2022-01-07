package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list;


import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration.*;
import java.util.Collections;

public class DeclarationList extends AstNode {

    private final List<Declaration> declList;

    public DeclarationList(List<Declaration> declList) {
        super(declList.isEmpty() ? null : declList.get(0).getPosition());
        this.declList = Collections.unmodifiableList(declList);
    }

    public Declaration getDeclaration(int index){ return declList.get(index); }

    public int getSize(){ return declList.size(); }

}
