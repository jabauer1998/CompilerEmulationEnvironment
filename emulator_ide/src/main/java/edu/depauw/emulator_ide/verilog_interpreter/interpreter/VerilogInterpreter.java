package edu.depauw.emulator_ide.verilog_interpreter.interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_interpreter.OpUtil;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.BoolVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.StrVal;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_interpreter.parser.Lexer;
import edu.depauw.emulator_ide.verilog_interpreter.parser.Parser;
import edu.depauw.emulator_ide.verilog_interpreter.parser.Token;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.VerilogFile;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import edu.depauw.emulator_ide.verilog_interpreter.parser.pre_processor.Preprocessor;

public class VerilogInterpreter extends Interpreter {

    public VerilogInterpreter(ErrorLog errLog){
        super(errLog);
    }
    /**
     * Brlow are the private methods that an interpreter can call
     * 
     * @param Expression
     * @return
     * @throws Exception
     */

     public Value interpretExpression(String Expression){
        Source source = new Source(new StringReader(Expression));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        Expression exp = parse.parseExpression();

		try{
        	return interpretShallowExpression(exp);
		} catch(Exception exception){
			errorLog.addItem(new ErrorItem(exception.toString()));
			return OpUtil.errorOccured();
		}
    }

    public IntVal interpretStatement(String Statement){
        Source source = new Source(new StringReader(Statement));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        Statement Stat = parse.parseStatement();

		try{
        	interpretShallowStatement(Stat);
		} catch(Exception exp) {
			errorLog.addItem(new ErrorItem(exp.toString()));
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
    }

	public IntVal interpretModuleItem(String moduleItem){
		Source source = new Source(new StringReader(moduleItem));
		Lexer lex = new Lexer(source, errorLog);
		LinkedList<Token> tokens = lex.tokenize();
		Parser parse = new Parser(tokens, errorLog);
		List<ModuleItem> items = parse.parseModuleItem();
		for(ModuleItem item : items){
			try{
				Value Result = interpretModuleItem(item);
				if(Result == OpUtil.errorOccured()){
					return OpUtil.errorOccured();
				}
			} catch(Exception exp){
				errorLog.addItem(new ErrorItem(exp.toString()));
				return OpUtil.errorOccured();
			}
		}
		return OpUtil.success();
	}

    public IntVal interpretModule(String Module){
        Source source = new Source(new StringReader(Module));
        Lexer lex = new Lexer(source, errorLog);
        LinkedList<Token> tokens = lex.tokenize();
        Parser parse = new Parser(tokens, errorLog);
        ModuleDeclaration Decl = parse.parseModuleDeclaration();

		try{
        	interpretModule(Decl);
		} catch(Exception exp){
			errorLog.addItem(new ErrorItem(exp.toString()));
		}

        if(errorLog.size() > 0){
            errorLog.printLog();
            return OpUtil.errorOccured();
        } else {
            return OpUtil.success();
        }
    }

    public IntVal interpretFile(String FileName){
        try{
			FileReader Reader = new FileReader(FileName);
			return interpretFile(Reader);
		} catch(FileNotFoundException exc) {
			errorLog.addItem(new ErrorItem("Could not make file stream exception thrown" + exc.toString()));
		} catch (Exception exp){
			errorLog.addItem(new ErrorItem("Exception occured when interpreting file " + exp.toString()));
		}

		return OpUtil.errorOccured();
    }

	public IntVal interpretFile(FileReader Reader){
		Source Source = new Source(Reader);
		Lexer lex = new Lexer(Source, errorLog);

		/**
		 * When Interpereting a File we need to 
		 * run a preprocessor because include statemnts can also occur
		 */
        
		List<Token> tokens = lex.tokenize();
        
		Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		Parser P = new Parser(tokens, errorLog);
		VerilogFile File = P.parseVerilogFile();

		try{
			return interpretFile(File);
		} catch(Exception exp){
			errorLog.addItem(new ErrorItem(exp.toString()));
			return OpUtil.errorOccured();
		}
	}

	public IntVal interpretFile(FileInputStream Stream){
		Source Source = new Source(Stream);
		Lexer lex = new Lexer(Source, errorLog);
        
		List<Token> tokens = lex.tokenize();
        Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		 Parser P = new Parser(tokens, errorLog);
		 VerilogFile File = P.parseVerilogFile();


		try{
			IntVal interpreterResult = interpretFile(File);
			return interpreterResult;
		} catch(Exception exp) {
			errorLog.addItem(new ErrorItem(exp.toString()));
		}
		
		return OpUtil.errorOccured();
	}

    protected IntVal interpretSystemTaskCall(SystemTaskStatement task) throws Exception{
		String taskName = task.taskName;

		if (taskName.equals("fclose")) {
			Value fileDescriptor = interpretShallowExpression(task.argumentList.get(0));
			FileReader Scanner = environment.getFileReader(fileDescriptor.intValue());
			try{
				Scanner.close();
				environment.clearFileReader(fileDescriptor.intValue());
			} catch (Exception exp){
				OpUtil.errorAndExit(exp.toString());
				return OpUtil.errorOccured();
			}
		} else if (taskName.equals("display")) {
			if (task.argumentList.size() == 2) {
			 	Value fString = interpretShallowExpression(task.argumentList.get(0));
				Value  fData = interpretShallowExpression(task.argumentList.get(1));

			} else if (task.argumentList.size() == 1) {
				Value data = interpretShallowExpression(task.argumentList.get(0));				
			} else {
				OpUtil.errorAndExit("Unknown number of print arguments in " + task.taskName, task.position);
			}
		} else if (taskName.equals("finish")) {
			OpUtil.errorAndExit("Program is finished!!! Program exited successfully!!!");
		} else {
			OpUtil.errorAndExit("Unknown system task declaration " + taskName, task.position);
		}

		return OpUtil.success();
    }

    protected Value interpretSystemFunctionCall(SystemFunctionCall call) throws Exception{
		String functionName = call.functionName;

		if (functionName.equals("fopen")) {
			StrVal fname = (StrVal)interpretShallowExpression(call.argumentList.get(0));
			String basePath = OpUtil.GetRuntimeDir();
			StrVal access = (StrVal)interpretShallowExpression(call.argumentList.get(1));
			String fullPath = basePath + '/' + fname;

			if (access.toString().equals("r")) {
				int fileDescriptor = environment.createReadOnlyFileDescriptor(fullPath);
				return new IntVal(fileDescriptor);
			} else if (access.equals("w")) {
				int fileDescriptor = environment.createWritableFileDescriptor(fullPath);
				return new IntVal(fileDescriptor);
			} else {
				OpUtil.errorAndExit("Unexpected Access type " + access + " for file " + basePath + '/' + fname, call.position);
			} 
		} else if (functionName.equals("feof")) {
			Value fileDescriptor = interpretShallowExpression(call.argumentList.get(0));
			FileReader reader = environment.getFileReader(fileDescriptor.intValue());

			try{
				return new BoolVal(reader.ready());
			} catch(Exception exp){
				return OpUtil.errorOccured();
			}
		} else if (functionName.equals("fscanf")) {
			Value fileDescriptor = interpretShallowExpression(call.argumentList.get(0));
			Value fString = interpretShallowExpression(call.argumentList.get(1));
			Value location = interpretShallowExpression(call.argumentList.get(2));
			
			FileReader fReader = environment.getFileReader(fileDescriptor.intValue());
			Scanner fScanner = new Scanner(fReader);

			String Data = fScanner.findInLine(fString.toString());
			StrVal DataVal = new StrVal(Data);
			
			return OpUtil.errorOccured(); // allways true just for consistency with verilog
		} else {
			OpUtil.errorAndExit("Could not find a systemcall with the name " + functionName, call.position);
		}

		return OpUtil.errorOccured();
    }
}
