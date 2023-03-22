package edu.depauw.emulator_ide.verilog_compiler.interpreter;


import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;

import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.circuit_elem.test_utils.Tuple;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.RegVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.web.WireVal;
import org.junit.Test;
import static edu.depauw.emulator_ide.TestUtils.*;
import static org.junit.Assert.*;
import java.io.StringReader;

public class InterpreterTest {

	@Test
	public void testAdditionOperation(){
		String input = "8 + 9\n";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The Result is not equal to 17", Result.intValue() == 17);
	}

	@Test
	public void testSubtractionOperation(){
		String input = "20 - 9";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result isnt of type Long", Result.isLongValue());
		assertTrue("The result is not equal to 11", Result.intValue() == 11);
	}

	@Test
	public void testDivisionOperation(){
		String input = "20 / 10";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("Result is not equal to 2", Result.intValue() == 2);
	}

	@Test
	public void testMultiplicationOperation(){
		String input = "20 * 10";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The Result is not equal to 200", Result.intValue() == 200);
	}

	@Test
	public void testBitwiseOrOperation(){
		String input = "1 | 2";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is 3", Result.intValue() == 3);
	}

	
	
	@Test
	public void testBitwiseAndOperation(){
		String input = "1 & 2";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is 0", Result.intValue() == 0);
	}

	@Test
	public void testBitwiseXorOperation(){
		String input = "1 | 2";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is 3", Result.intValue() == 3);
	}

	@Test
	public void testLogicalAndOperationFalse(){
		String input = "1 && 0";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is False", Result.boolValue() == false);
	}

	@Test
	public void testLogicalOrOperationTrue(){
		String input = "1 || 0";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is True", Result.boolValue() == true);
	}

	@Test
	public void testLogicalAndOperationTrue(){
		String input = "1 && 1";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is True", Result.boolValue() == true);
	}

	@Test
	public void testLogicalOrOperationFalse(){
		String input = "0 || 0";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is False", Result.boolValue() == false);
	}

	@Test
	public void testBitwiseNegation(){
		String input = "~255";
		Destination dest = new Destination(System.out);
		ErrorLog errorLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errorLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The Result is 0", Result.byteValue() == 0);
	}

	@Test
	public void testLogicalNegation(){
		String input = "-255";
		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errLog);
		Value Result = interpreter.interpretExpression(input);
		assertTrue("The result is -255", Result.intValue() == -255);
	}

	@Test 
	public void testBlockingAssignment(){
		String decl = "reg [31:0] var1, var2;\n";
		String assign1 = "begin\n" 
					   + "	var1 = 60;\n"
					   + "  var2 = 20;\n"
					   + "  var1 = var1 - var2;\n"
					   + "  var2 = var1 + var2;\n"
					   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);
		
		intr.interpretModuleItem(decl);
		intr.interpretStatement(assign1);

		Value result1 = intr.interpretExpression("var1");
		Value result2 = intr.interpretExpression("var2");

		assertTrue("Var1 is not equal to 40", result1.intValue() == 40);
		assertTrue("Var2 is not equal to 60", result2.intValue() == 60);
	}

	@Test
	public void testNonBlockingAssignment(){
		String decl = "reg [31:0] var1, var2;\n";
		String assign1 = "begin\n" 
					   + "	var1 = 60;\n"
					   + "  var2 = 20;\n"
					   + "  var1 <= var1 - var2;\n"
					   + "  var2 <= var1 + var2;\n"
					   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);
		
		intr.interpretModuleItem(decl);
		intr.interpretStatement(assign1);

		Value result1 = intr.interpretExpression("var1");
		Value result2 = intr.interpretExpression("var2");

		assertTrue("Var1 is not equal to 40", result1.intValue() == 40);
		assertTrue("Var2 is not equal to 80", result2.intValue() == 80);
	}

	@Test
	public void testIfStatementTrue(){
		String decl = "reg [31:0] var1, var2;";
		String ifStatement = "begin\n"
						   + "  var1 = 60;\n"
						   + "  if(var1 == 60)\n"
						   + "    var2 = 100\n"
						   + "  else\n"
						   + "    var2 = 50\n"
						   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);
						   
		intr.interpretModuleItem(decl);
		intr.interpretStatement(ifStatement);

		Value result = intr.interpretExpression("var2");

		assertTrue("Var2 is not equal to 100", result.intValue() == 100);
	}

	@Test
	public void testIfStatementFalse(){
		String decl = "reg [31:0] var1, var2;";
		String ifStatement = "begin\n"
						   + "  var1 = 50;\n"
						   + "  if(var1 == 60)\n"
						   + "    var2 = 100\n"
						   + "  else\n"
						   + "    var2 = 50\n"
						   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);
						   
		intr.interpretModuleItem(decl);
		intr.interpretStatement(ifStatement);

		Value result = intr.interpretExpression("var2");

		assertTrue("Var2 is not equal to 50", result.intValue() == 50);
	}

	@Test
	public void testCaseStatement(){
		String decl = "reg [31:0] var1, var2;";
		String caseStatement = "begin\n"
							 + "  var1 = 30;\n"
							 + "  var2 = 60;\n"
							 + "  case (var1)\n"
							 + "    30: var1 = var1 + 1;\n"
							 + "    31: var1 = var1 + 1;\n"
							 + "    var2: var1 = var1 + var2;\n"
							 + "    default: var1 = var1 + 1;\n"
							 + "  endcase\n"
							 + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);

		intr.interpretModuleItem(decl);
		intr.interpretStatement(caseStatement);

		Value result1 = intr.interpretExpression("var1");

		assertTrue("var1 is not equal to 33", result1.intValue() == 33);
	}

	@Test
	public void testRepeatStatement(){
		String decl = "reg [31:0] var1, var2;";
		String repeatStat = "begin\n"
						  + "  var1 = 10;"
						  + "  var2 = 20;"
						  + "  repeat(10)"
						  + "    var1 = var1 + var2;"
						  + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter interpreter = new Interpreter(errLog);

		interpreter.interpretModuleItem(decl);
		interpreter.interpretStatement(repeatStat);

		Value result1 = interpreter.interpretExpression("var1");

		assertTrue("var1 is not equal to 210", result1.intValue() == 210);
	}

	@Test
	public void testWhileLoop(){
		String decl = "reg [31:0] var1, var2;";
		String whileLoop = "begin\n"
						   + "  var1 = 50;\n"
						   + "  var2 = 100;\n"
						   + "  while(var1 > 0) begin\n"
						   + "    var2 = var2 - 1;\n"
						   + "    var1 = var1 - 1;\n"
						   + "  end\n"
						   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);

		intr.interpretModuleItem(decl);
		intr.interpretStatement(whileLoop);

		Value result2 = intr.interpretExpression("var2");
		assertTrue("Var2 is not equal to 50", result2.intValue() == 50);
	}

	@Test
	public void testForLoop(){
		String decl = "reg [31:0] var1, var2;";
		String forLoop = "begin\n"
					   + "  var2 = 100;\n"
					   + "  for(var1 = 50; var1 > 0; var1 = var1 - 1) begin\n"
					   + "    var2 = var2 - 1;\n"
					   + "  end\n"
					   + "end\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);

		intr.interpretModuleItem(decl);
		intr.interpretStatement(forLoop);

		Value result2 = intr.interpretExpression("var2");
		assertTrue("Var2 is not equal to 50", result2.intValue() == 50);
	}

	@Test
	public void testFunctionDeclaration(){
		String input = "function reg [32:0] myFunc;\n"
					 + "  input reg [32:0] arg1;\n"
					 + "  input reg [32:0] arg2;\n"
					 + "  reg [32:0] tmp1;\n"
					 + "  begin\n" 
					 + "    tmp1 = arg1 - arg2;\n"
					 + "    myFunc = arg1 + arg2 - tmp1;\n"
					 + "  end\n"
					 + "endfunction //Function Declaration\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);
		IntVal result = intr.interpretModuleItem(input);
		assertTrue("Error: Result is not 0", !result.boolValue());
		
		Value funcCall = intr.interpretExpression("myFunc(4, 5)");
		assertTrue("Error: Result is not equal to 10", funcCall.intValue() == 10);

		funcCall = intr.interpretExpression("myFunc(20, 30)");
		assertTrue("Error: Result is not equal to 60", funcCall.intValue() == 60);
	}

	@Test
	public void testTaskDeclaration(){
		String decl = "reg [31:0] var1;";
		String assign = "var1 = 0;";
		String input = "task myTask;\n"
					 + "input reg [31:0] arg1;\n"
					 + "  begin\n"
					 + "    var1 = var1 + arg1;\n"
					 + "  end\n"
					 + "endtask\n";
		
		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);

		intr.interpretModuleItem(decl);
		intr.interpretStatement(assign);
		intr.interpretModuleItem(input);

		intr.interpretStatement("myTask(5);");
		intr.interpretStatement("myTask(10);");
		intr.interpretStatement("myTask(15);");

		Value result = intr.interpretExpression("var1");

		assertTrue("Result is not equal to 30", result.intValue() == 30);
	}

	@Test
	public void testRecursiveFunctionCall(){
		String functionDeclaration = "function reg [31:0] fibonacci;\n"
								   + "  input reg[31:0] n;"
								   + "  begin\n"
								   + "    if(n <= 1) fibonacci = n;\n"
								   + "    else fibonacci = fibonacci(n - 1) + fibonacci(n - 2);\n"
								   + "  end\n"
								   + "endfunction\n";

		Destination dest = new Destination(System.out);
		ErrorLog errLog = new ErrorLog(dest);
		Interpreter intr = new Interpreter(errLog);

		intr.interpretModuleItem(functionDeclaration);

		Value fib0 = intr.interpretExpression("fibonacci(0)");
		assertTrue("Error: result is not equal to 0", fib0.intValue() == 0);

		Value fib1 = intr.interpretExpression("fibonacci(1)");
		assertTrue("Error: result is not equal to 1", fib1.intValue() == 1);

		Value fib2 = intr.interpretExpression("fibonacci(2)");
		assertTrue("Error: result is not equal to 1", fib2.intValue() == 1);

		Value fib3 = intr.interpretExpression("fibonacci(3)");
		assertTrue("Error: result is not equal to 2", fib3.intValue() == 2);

		Value fib4 = intr.interpretExpression("fibonacci(4)");
		assertTrue("Error: Result is not equal to 3", fib4.intValue() == 3);

		Value fib5 = intr.interpretExpression("fibonacci(5)");
		assertTrue("Error: Result is not equal to 5", fib5.intValue() == 5);
	}

	
}
