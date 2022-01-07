package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.StatementVisitor;
import java.lang.Runnable;
import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;

public class InitialStatement extends ModItem implements Runnable {

    private final Statement                  stat;
    private static volatile StatementVisitor threadVisitor;
    private static volatile Semaphore        sema;

    public InitialStatement(Statement stat) {
        super(stat.getPosition());
        this.stat = stat;
    }

    public static void setThreadParameters(StatementVisitor stat, Semaphore semaphore){
        threadVisitor = stat;
        sema = semaphore;
    }

    public Statement getStatement(){ return stat; }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

    public void run(){

        if (threadVisitor == null) {
            System.out.println("Error: thread visitor may have not been initialized in initial statement");
            System.exit(1);
        } else if (sema == null) {
            System.out.println("Error: semaphore may not have been initialized in Allways Statement");
            System.exit(1);
        }

        stat.accept(threadVisitor);
        sema.release();
    }
}
