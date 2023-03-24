package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.process;

import java.util.concurrent.Semaphore;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class ProcessBase extends AstNode implements ModuleItem, Runnable{
    public final Statement statement;

    protected Interpreter interpreter;
    protected Semaphore semaphore;
    
    protected ProcessBase(Position start, Statement statement){
        super(start);
        this.statement = statement;
    }

    public void initEnvironment(Interpreter interpreter, Semaphore semaphore){
        this.interpreter = interpreter;
        this.semaphore = semaphore;
    }

    public void run(){
      if (statement == null) {
        System.out.println("Error need to set a semaphore for AllwaysStatements");
        System.exit(1);
      } else {
        executeProcess(interpreter, semaphore);
      }
    }

    public abstract void executeProcess(Interpreter Interpreter, Semaphore semaphore);
    
    @Override
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
