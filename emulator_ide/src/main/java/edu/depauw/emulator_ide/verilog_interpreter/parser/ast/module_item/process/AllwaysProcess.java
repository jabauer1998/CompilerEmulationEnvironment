package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.process;

import java.util.concurrent.Semaphore;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class AllwaysProcess extends ProcessBase {
	public AllwaysProcess(Position start, Statement statement) {
		super(start, statement);
	}

	public void executeProcess(Interpreter interpreter, Semaphore Semaphore){
		boolean AllwaysTrue = true;
		while(AllwaysTrue){
			interpreter.interpretShallowStatement(this.statement);
		}
		Semaphore.release();
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

	
}
