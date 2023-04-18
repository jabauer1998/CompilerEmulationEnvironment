package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process;

import java.util.concurrent.Semaphore;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Interpreter;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class ProcessBase extends AstNode implements ModuleItem, Runnable{
    public final Statement statement;

    private Interpreter interpreter;
    private Semaphore semaphore;
    private ErrorLog errLog;
    
    
    protected ProcessBase(Position start, Statement statement){
        super(start);
        this.statement = statement;
    }

    public void initEnvironment(Interpreter interpreter, ErrorLog errLog, Semaphore semaphore){
        this.interpreter = interpreter;
        this.semaphore = semaphore;
        this.errLog = errLog;
    }

    public void run(){
      if (statement == null) {
        System.out.println("Error need to set a semaphore for AllwaysStatements");
        System.exit(1);
      } else {
        try {
          executeProcess(interpreter, semaphore);
        } catch (Exception e) {
          errLog.addItem(new ErrorItem("Exception " + e.toString() + "\n with \n" + e.getMessage() + "\n"));
          this.semaphore.release();
        }
      }
    }

    public abstract void executeProcess(Interpreter Interpreter, Semaphore semaphore) throws Exception;
    
    @Override
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
