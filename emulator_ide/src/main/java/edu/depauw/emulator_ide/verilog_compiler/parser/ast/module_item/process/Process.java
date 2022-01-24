package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.process;

import java.util.concurrent.Semaphore;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;

public abstract class Process extends AstNode implements ModuleItem, Runnable{

    private static int processCount = 0;
    
    protected static volatile Semaphore exitTicket = null;
    protected static StatementVisitor executionEngine;
    protected final Statement statement;
    
    protected Process(Position start, Statement statement){
        super(start);
        this.executionEngine = null;
        this.statement = statement;
        processCount++;
    }

    public void setUpExecutionEngine(StatementVisitor visitor){
        this.executionEngine = visitor;
    }

    public static void initializeCleanExitSemaphore(){
        exitTicket = new Semaphore(processCount);
    }

    public Statement getStatement(){
        return statement;
    }

    public void run(){
        
        if (executionEngine == null) {
			System.out.println("Error need to set a thread visitor for AllwaysStatements");
			System.exit(1);
		}

		if (statement == null) {
			System.out.println("Error need to set a semaphore for AllwaysStatements");
			System.exit(1);
		}

        executeProcess();
    }

    public abstract void executeProcess();
    
    @Override
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
