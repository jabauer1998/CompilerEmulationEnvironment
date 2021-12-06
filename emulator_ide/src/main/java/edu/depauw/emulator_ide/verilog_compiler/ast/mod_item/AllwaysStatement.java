package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;


import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;

import java.lang.Runnable;
import java.util.concurrent.Semaphore;

public class AllwaysStatement extends ModItem implements Runnable {

	private static volatile StatementVisitor threadVisitor;
	private static volatile Semaphore        sema;
	private final Statement                  stat;

	public AllwaysStatement(Statement stat) {
		super(stat.getPosition());
		this.stat = stat;
	}

	public Statement getStatement(){ return stat; }

	public static void setThreadParameters(StatementVisitor stat, Semaphore semaphore){
		threadVisitor = stat;
		sema = semaphore;
	}

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
			System.out.println("Error need to set a thread visitor for AllwaysStatements");
			System.exit(1);
		}

		if (stat == null) {
			System.out.println("Error need to set a semaphore for AllwaysStatements");
			System.exit(1);
		}

		boolean tf = true;

		while(tf) { stat.accept(threadVisitor); }

		sema.release();
	}
}
