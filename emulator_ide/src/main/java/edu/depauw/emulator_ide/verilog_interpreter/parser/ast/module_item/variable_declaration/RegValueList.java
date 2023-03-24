package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class RegValueList<RegValueType> extends AstNode implements ModuleItem {

    private List<RegValueType> regValueList;
    
    public RegValueList(Position start, List<RegValueType> regValueList){
        super(start);
        this.regValueList = regValueList;
    }

    public RegValueType getRegValue(int index){
        return regValueList.get(index);
    }

    public int numberOfRegValues(){
        return regValueList.size();
    }

    public <ModItemVisitType> ModItemVisitType accept(ModuleVisitor<ModItemVisitType> regValueVisitor, Object... argv){
        return regValueVisitor.visit(this, argv);
    }
}
