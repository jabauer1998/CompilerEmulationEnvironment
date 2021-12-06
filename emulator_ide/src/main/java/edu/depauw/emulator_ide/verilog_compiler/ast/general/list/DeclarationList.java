package edu.depauw.emulator_ide.verilog_compiler.ast.general.list;


import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;

import java.util.List;
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
