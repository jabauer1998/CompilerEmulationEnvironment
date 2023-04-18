package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.common.io.FormattedScanner;
import io.github.H20man13.emulator_ide.common.io.Source;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.BoolVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.StrVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Lexer;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Parser;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Token;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.VerilogFile;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.pre_processor.Preprocessor;

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
		List<Token> tokens = lex.tokenize();

		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

        
		Preprocessor preProc = new Preprocessor(errorLog, tokens);
		tokens = preProc.executePass();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}
		
        Parser parse = new Parser(tokens, errorLog);
        Expression exp = parse.parseExpression();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		try{
        	return interpretShallowExpression(exp);
		} catch(Exception exception){
			errorLog.addItem(new ErrorItem(exception.toString()));
			errorLog.printLog();
			return OpUtil.errorOccured();
		}
    }

    public IntVal interpretStatement(String Statement){
        Source source = new Source(new StringReader(Statement));
        Lexer lex = new Lexer(source, errorLog);
        List<Token> tokens = lex.tokenize();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		Preprocessor preProc = new Preprocessor(errorLog, tokens);
		tokens = preProc.executePass();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

        Parser parse = new Parser(tokens, errorLog);
        Statement Stat = parse.parseStatement();

		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		try{
        	interpretShallowStatement(Stat);
		} catch(Exception exp) {
			errorLog.addItem(new ErrorItem(exp.toString()));
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
    }

	public IntVal interpretModuleItem(String moduleItem){
		Source source = new Source(new StringReader(moduleItem));
		
		Lexer lex = new Lexer(source, errorLog);
		List<Token> tokens = lex.tokenize();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}


		Preprocessor preProc = new Preprocessor(errorLog, tokens);
		tokens = preProc.executePass();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		Parser parse = new Parser(tokens, errorLog);
		List<ModuleItem> items = parse.parseModuleItem();
		for(ModuleItem item : items){
			try{
				Value Result = interpretModuleItem(item);
				if(Result == OpUtil.errorOccured()){
					errorLog.printLog();
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
        List<Token> tokens = lex.tokenize();

		Preprocessor preProc = new Preprocessor(errorLog, tokens);
		tokens = preProc.executePass();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

        Parser parse = new Parser(tokens, errorLog);
        ModuleDeclaration Decl = parse.parseModuleDeclaration();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}


		try{
        	interpretModule(Decl);
		} catch(Exception exp){
			errorLog.addItem(new ErrorItem(exp.toString()));
			if(errorLog.size() > 0){
				errorLog.printLog();
				return OpUtil.errorOccured();
			}
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

		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}
        
		Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();

		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		Parser P = new Parser(tokens, errorLog);
		VerilogFile File = P.parseVerilogFile();

		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		try{
			return interpretFile(File);
		} catch(Exception exp){
			errorLog.addItem(new ErrorItem(exp.toString()));
			errorLog.printLog();
			return OpUtil.errorOccured();
		}
	}

	public IntVal interpretFile(FileInputStream Stream){
		Source Source = new Source(Stream);
		Lexer lex = new Lexer(Source, errorLog);
		List<Token> tokens = lex.tokenize();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		
        Preprocessor Prepros = new Preprocessor(errorLog, tokens);
		tokens = Prepros.executePass();
		if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		/**
		 * On the preprocessed file
		 * run the Parser
		 */

		 Parser P = new Parser(tokens, errorLog);
		 VerilogFile File = P.parseVerilogFile();
		 if(errorLog.size() > 0){
			errorLog.printLog();
			return OpUtil.errorOccured();
		}

		try{
			IntVal interpreterResult = interpretFile(File);
			return interpreterResult;
		} catch(Exception exp) {
			errorLog.addItem(new ErrorItem(exp.toString()));
			errorLog.printLog();
		}
		
		return OpUtil.errorOccured();
	}

    protected IntVal interpretSystemTaskCall(SystemTaskStatement task) throws Exception{
		String taskName = task.taskName;

		if (taskName.equals("fclose")) {
			Value fileDescriptor = interpretShallowExpression(task.argumentList.get(0));
			FormattedScanner Scanner = environment.getFileReader(fileDescriptor.intValue());
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
			FormattedScanner reader = environment.getFileReader(fileDescriptor.intValue());
			try{
				return new BoolVal(reader.atEof());
			} catch(Exception exp){
				errorLog.addItem(new ErrorItem("Error: FileStream ready failed with exception" + exp.toString()));
				return OpUtil.errorOccured();
			}
		} else if (functionName.equals("fscanf")) {
			Value fileDescriptor = interpretShallowExpression(call.argumentList.get(0));
			Value fString = interpretShallowExpression(call.argumentList.get(1));
			Value location = interpretShallowExpression(call.argumentList.get(2));
			
			FormattedScanner fScanner = environment.getFileReader(fileDescriptor.intValue());

			List<Object> result = fScanner.scanf(fString.toString());
			
			if(result.size() == 0){
				OpUtil.errorAndExit("Result in Scanf returned no Objects");
				return OpUtil.errorOccured();
			} else {
				Value scanfResult = OpUtil.convertToRawValue(result.get(0));

				if(location.isVector()){
					VectorVal vecVal = (VectorVal)location;
					OpUtil.shallowAssign(vecVal, scanfResult.longValue());
					return OpUtil.success();					
				} else {
					OpUtil.errorAndExit("Invalid location type of " + location.getClass().getName());
					return OpUtil.errorOccured();
				}
			}
		} else {
			OpUtil.errorAndExit("Could not find a systemcall with the name " + functionName, call.position);
		}

		return OpUtil.errorOccured();
    }
}
