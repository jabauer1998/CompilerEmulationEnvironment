package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.function_call;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import java.util.List;

/**
 * The FunctionCall is used to call functions Functions are different than tasks because
 * they have a return value
 * 
 * @author Jacob Bauer
 */

public class SystemFunctionCall extends FunctionCall {
    
    public SystemFunctionCall(Position start, String functionName, List<Expression> argumentList) {
        super(start, functionName, argumentList);
    }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    public Value interpret(Environment env){
        String functionName = this.getSystemFunctionName();

		if (functionName.equals("fopen")) {
            if(){

            }
			String fname = this.getExpression(0).accept(this);
			String basePath = new File("").getAbsolutePath();
			String access = (String)call.getExpression(1).accept(this);

			if (fname.equals("default")) {
				return new Scanner(Main.getByteInputStream());
			} else {
				File filename = new File(basePath + '/' + fname);

				if (access.equals("r")) {
					filename.setReadOnly();

					Scanner ref = null;

					try {
						ref = new Scanner(filename);
					} catch (FileNotFoundException exp) {
						exp.printStackTrace();
						System.exit(1);
					}

					return ref;
				} else if (access.equals("w")) {
					filename.setWritable(true, false);

					FileWriter ref = null;

					try {
						ref = new FileWriter(filename);
					} catch (IOException exp) {
						exp.printStackTrace();
						System.exit(1);
					}

					return ref;
				} else {
					errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.getPosition());
				}

				return null;
            }
        }
    }
}
