package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public abstract class Process extends AstNode implements ModuleItem, Runnable{
    public final Statement statement;
    
    protected Process(Position start, Statement statement){
        super(start);
        this.statement = statement;
    }

    public void run(){
      if (statement == null) {
        System.out.println("Error need to set a semaphore for AllwaysStatements");
        System.exit(1);
      }
    }
    
    @Override
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
