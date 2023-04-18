package io.github.h20man13.emulator_ide;


import io.github.h20man13.emulator_ide.common.io.Destination;
import io.github.h20man13.emulator_ide.common.io.Source;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.Lexer;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.Parser;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.Token;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.Indexer;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.type_checker.TypeChecker;
import io.github.h20man13.emulator_ide.common.debug.ErrorLog;
import org.junit.Test;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;

public class CheckpointTest {

	@Test
	public void Checkpoint1(){

		String input = "module fulladder (a, b, cin, cout, sum);\n" + "// declare all of the inputs \n" + "input [31:0] a, b;\n"
			+ "input cin;\n" + "wire [31:0] a, b;\n" + "wire cin;\n" + "// declare all of the outputs \n" + "output cout;\n"
			+ "output [31:0] sum;\n" + "wire cout;\n" + "wire [31:0] sum;\n"
			+ "// assign the outputs of the module to the sum of each bit\n" + "assign {cout, sum} = a + b + cin;\n"
			+ "endmodule\n";

		System.out.print("\n\n---------------Checkpoint 1 Input Program------------------\n\n");

		System.out.print(input);

		System.out.print("\n\n-----------Checkpoint 1 Tokenizer Test Results---------------\n\n");

		Destination display = new Destination(System.out);
		Source source = new Source(new StringReader(input));
		ErrorLog errorLog = new ErrorLog(display);
		Lexer lex = new Lexer(source, errorLog);

		LinkedList<Token> tokens = lex.tokenize();

		for (Token i : tokens) { System.out.println(i.toString()); }

		System.out.print("\n\n-----------Checkpoint 1 Indexer Test Results---------------\n\n");
		Parser parse = new Parser(tokens, errorLog);
		ModuleDeclaration moddec = parse.parseModuleDeclaration();
		Indexer idVisitor = new Indexer(display, errorLog);
		idVisitor.visit(moddec);
	}

	@Test
	public void Checkpoint2(){
		System.out.print("\n\n-----------Checkpoint 2 Input Binary Program---------------\n\n");
		String binaryProgram = "0x00 | 11100011101000000000000011111111 //MOV R0, #255\n"
			+ "0x04 | 11100011101000010001000000000001 //MOV R1, #1\n"
			+ "0x08 | 11100011101000100010000000001100 //MOV R2, #0x0C\n"
			+ "0x0C | 11101111000000000000000000000001 //SWI 1 (Prints value loaded in R0)\n"
			+ "0x10 | 11100000010100000000000000000001 //SUB R0, R0, R1 (Sets condition codes)\n"
			+ "0x14 | 11000001001011111111111100010010 //BXGT R2\n" + "0x18 | 00000110000000000000000000010000 //STOP";

		System.out.print(binaryProgram);

		String path = "src/main/java/edu/depauw/emulator_ide/processor";
		String command1 = "iverilog ARM7TDMIS.v";

		try {
			System.out.print("\n\n-----------Checkpoint 2 Compiling Error Output---------------\n\n");
			Process process = Runtime.getRuntime().exec(command1, null, new File(path));
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;

			while((line = reader.readLine()) != null) { System.out.println(line); }

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String command2 = "vvp a.out";

		try {
			Process process = Runtime.getRuntime().exec(command2, null, new File(path));
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			System.out.print("\n\n-----------Checkpoint 2 Binary Program Output---------------\n\n");
			String line;

			while((line = reader.readLine()) != null) { System.out.println(line); }

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print("\n\n-----------Checkpoint 2 Type Checking Program---------------\n\n");
		String input = "module fulladder (//declare all of the inputs and output ports\n" + "\t\tinput wire [31:0] a,\n"
			+ "\t\tinput wire [31:0] b,\n" + "\t\tinput wire cin,\n" + "\t\toutput wire cout,\n" + "\t\toutput [31:0] sum,\n"
			+ "\t\tUNDECLARED_YET\n" + "\t\t);\n" + "//assign the outputs of the module to the sum of each bit\n"
			+ "assign {cout, sum} = a + b + UNDECLAREDYET;\n" + "allways begin\n"
			+ "\ta = !a; //treating a like a clock signal usually you would add a delay like #10 or #20 aswell\n" + "end\n"
			+ "initial begin\n" + "\tb = ~b;\n" + "\tb = -h;\n" + "end\n" + "endmodule\n";

		System.out.print(input);
		System.out.print("\n\n-----------Checkpoint 2 Type Checking Result---------------\n\n");
		// tokenise the tokens
		Destination display = new Destination(System.out);
		Source source = new Source(new StringReader(input));
		ErrorLog errorLog = new ErrorLog(display);
		Lexer lex = new Lexer(source, errorLog);

		LinkedList<Token> tokens = lex.tokenize();

		// parse the tokens
		Parser parse = new Parser(tokens, errorLog);
		ModuleDeclaration moddec = parse.parseModuleDeclaration();

		TypeChecker typeChecker = new TypeChecker(errorLog);
		typeChecker.visit(moddec);
		errorLog.printLog();

		System.out.print("\n\n---------------------------------------------------------\n\n");
	}

	@Test
	public void Checkpoint3(){
		System.out.print("\n\n-----------Checkpoint 3 Full Program Parser Test---------------\n\n");
		System.out.println("The checkpoint 3 test was removed because the application now requires a gui to run");
	}
}
