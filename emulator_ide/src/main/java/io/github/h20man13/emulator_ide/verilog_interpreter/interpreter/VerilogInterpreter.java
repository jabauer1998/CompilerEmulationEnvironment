package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.H20man13.emulator_ide.common.io.Destination;
import io.github.H20man13.emulator_ide.common.io.FormattedScanner;
import io.github.H20man13.emulator_ide.common.io.Source;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ArrayVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.BoolVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.StrVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.CircuitElem;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.RegVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Lexer;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Parser;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.Token;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.ModuleDeclaration;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.VerilogFile;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Input;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Output;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration.Reg;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.NonBlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.pre_processor.Preprocessor;

public class VerilogInterpreter extends Interpreter {
	private Destination standardOutput;

    public VerilogInterpreter(ErrorLog errLog, Destination standardOutput){
        super(errLog);
		this.standardOutput = standardOutput;
    }

	public VerilogInterpreter(ErrorLog errorLog){
		super(errorLog);
		this.standardOutput = new Destination(new OutputStreamWriter(System.out));
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

	protected IntVal interpretDeclaration(Reg.Scalar.Array decl) throws Exception{
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			ArrayVal<RegVal> arrayDec = new ArrayVal<RegVal>(ArraySize);
			for(int i = 0; i < ArraySize; i++){
				arrayDec.AddElem(new RegVal(false));
			}
			environment.addVariable(decl.declarationIdentifier, arrayDec);
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	protected IntVal interpretDeclaration(Reg.Vector.Array decl) throws Exception{
		Expression RegIndex1 = decl.arrayIndex1;
		Expression RegIndex2 = decl.arrayIndex2;

		Value RegVal1 = interpretShallowExpression(RegIndex1);
		Value RegVal2 = interpretShallowExpression(RegIndex2);

		Expression vecIndex1 = decl.GetIndex1();
		Expression vecIndex2 = decl.GetIndex2();

		Value vecVal1 = interpretShallowExpression(vecIndex1);
		Value vecVal2 = interpretShallowExpression(vecIndex2);

		int ArraySize = RegVal2.intValue() - RegVal1.intValue();

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			ArrayVal<VectorVal> arrVal = new ArrayVal<VectorVal>(ArraySize);
			for(int i = 0; i < ArraySize; i++){
				arrVal.AddElem(new VectorVal(vecVal1.intValue(), vecVal2.intValue()));
			}
			environment.addVariable(decl.declarationIdentifier, arrVal);
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input scalar declaration in verilog. 
	 * Ex. input a, b, c ...;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Input.Reg.Vector.Ident decl) throws Exception{
		Expression exp1 = decl.GetIndex1();
		Expression exp2 = decl.GetIndex2();

		Value exp1Val = interpretShallowExpression(exp1);
		Value exp2Val = interpretShallowExpression(exp2);

		if (!environment.localVariableExists(decl.declarationIdentifier)) {
			environment.addVariable(decl.declarationIdentifier, new VectorVal(exp1Val.intValue(), exp2Val.intValue()));
		} else {
			OpUtil.errorAndExit("Error Variable allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is used to visit any input VectorVal declaration in verilog. 
	 * Ex. input a, b, c ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	 protected IntVal interpretDeclaration(Input.Reg.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg a, b, c ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	 protected IntVal interpretDeclaration(Reg.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	 protected IntVal interpretDeclaration(Reg.Vector.Ident decl) throws Exception{
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return null;
	}

	/**
	 * This is used to visit any reg scalar declaration in verilog. Ex. reg [2:0] a, b, c
	 * ... ;
	 * 
	 * @param decl
	 * @throws Exception
	 */

	protected IntVal interpretDeclaration(Output.Reg.Vector.Ident decl) throws Exception{
		Expression index1 = decl.GetIndex1();
		Expression index2 = decl.GetIndex2();

		Value index1Value = interpretShallowExpression(index1);
		Value index2Value = interpretShallowExpression(index2);

		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new VectorVal(index1Value.intValue(), index2Value.intValue()));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}

		return OpUtil.success();
	}

	/**
	 * This is where I will declare the output Register Scalar declaration
	 * 
	 * @param Jacob Bauer
	 * @throws Exception
	 */

	 protected IntVal interpretDeclaration(Output.Reg.Scalar.Ident decl) throws Exception{
		if(!environment.localVariableExists(decl.declarationIdentifier)){
			environment.addVariable(decl.declarationIdentifier, new RegVal(false));
		} else {
			OpUtil.errorAndExit("Error Register allready exists with the name " + decl.declarationIdentifier);
			return OpUtil.errorOccured();
		}
		return OpUtil.success();
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
			if(task.argumentList.size() >= 1){
				Value fString = interpretShallowExpression(task.argumentList.get(0));

				Object[] Params = new Object[task.argumentList.size() - 1];
				for(int paramIndex = 0, i = 1; i < task.argumentList.size(); i++, paramIndex++){
					 Value  fData = interpretShallowExpression(task.argumentList.get(i));
					 Object rawValue = OpUtil.getRawValue(fData);
					 Params[paramIndex] = rawValue;
				}
				
				String formattedString = String.format(fString.toString(), Params);

				standardOutput.println(formattedString);
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

	protected IntVal interpretShallowBlockingAssignment(BlockingAssignment assign) throws Exception {
		 Expression exp = assign.rightHandSide;
		 Value expVal = interpretShallowExpression(exp);
		 
		 if(assign.leftHandSide instanceof Element){
			Element leftHandElement = (Element)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
			if(leftHandDeref instanceof ArrayVal){
				ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
				leftHandArray.SetElemAtIndex(leftHandIndex.intValue(), expVal);
			} else if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;
				CircuitElem elem = leftHandVector.getValue(leftHandIndex.intValue());
				if(elem instanceof RegVal){
					RegVal elemReg = (RegVal)elem;
					elemReg.setSignal(expVal.boolValue());
				} else {
					OpUtil.errorAndExit("Error: Invalid Type for soft assignment " + elem.getClass().getName());
				}
			} else {
				OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
			}
		 } else if(assign.leftHandSide instanceof Slice){
			Slice leftHandSlice = (Slice)assign.leftHandSide;

			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
			Value leftHandDeref = leftHandPtr.deRefrence();

			Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
			Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);

			if(leftHandDeref instanceof VectorVal){
				VectorVal leftHandVector = (VectorVal)leftHandDeref;

				OpUtil.shallowAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), expVal.longValue());
			} else {
				OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
				return OpUtil.errorOccured();
			}
		 } else if(assign.leftHandSide instanceof Identifier){
			Identifier leftHandIdent = (Identifier)assign.leftHandSide;
			Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);

			Value leftHandDeref = leftHandPtr.deRefrence();
			if(leftHandDeref instanceof VectorVal){
				//If it is a vector then we need to use the OpUtil.shallowAssign on the Vector
				VectorVal Vec = (VectorVal)leftHandDeref;
				OpUtil.shallowAssign(Vec, expVal.longValue());
			} else {
				//If it is not a vector then just replace the value with whatever is on the Right Hand Side
				leftHandPtr.assign(expVal);
			}
			

			String currentStackFrameTitle = environment.stackFrameTitle();
			if(leftHandIdent.labelIdentifier.equals(currentStackFrameTitle)){
				environment.setFunctionExit(); //Makes it so we are in the Return Part of a Verilog Function
			}
		 } else {
			OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
			return OpUtil.errorOccured();
		 }

		 return OpUtil.success();
	}

	protected IntVal interpretShallowNonBlockingAssignment(NonBlockingAssignment assign) throws Exception{
		List<Value> resultList = new LinkedList<Value>();
		for(Expression exp: assign.rightHandSide){
		 	Value rhsVal = interpretShallowExpression(exp);
			resultList.add(rhsVal);
		}

		for(int i = 0; i < assign.leftHandSide.size(); i++){
			if(assign.leftHandSide.get(i) instanceof Element){
				Element leftHandElement = (Element)assign.leftHandSide.get(i);
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandElement.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandIndex = interpretShallowExpression(leftHandElement.index1);
				if(leftHandDeref instanceof ArrayVal){
					ArrayVal<Value> leftHandArray = (ArrayVal<Value>)leftHandDeref;
					leftHandArray.SetElemAtIndex(leftHandIndex.intValue(), resultList.get(i));
				} else if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
					CircuitElem elem = leftHandVector.getValue(leftHandIndex.intValue());
					if(elem instanceof RegVal){
						RegVal elemReg = (RegVal)elem;
						elemReg.setSignal(resultList.get(i).boolValue());
					} else {
						OpUtil.errorAndExit("Error: Invalid Type for soft assignment " + elem.getClass().getName());
					}
				} else {
					OpUtil.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
				}
			 } else if(assign.leftHandSide.get(i) instanceof Slice){
				Slice leftHandSlice = (Slice)assign.leftHandSide.get(i);
	
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandSlice.labelIdentifier);
				Value leftHandDeref = leftHandPtr.deRefrence();
	
				Value leftHandStartIndex = interpretShallowExpression(leftHandSlice.index1);
				Value leftHandEndIndex = interpretShallowExpression(leftHandSlice.index2);
	
				if(leftHandDeref instanceof VectorVal){
					VectorVal leftHandVector = (VectorVal)leftHandDeref;
	
					OpUtil.shallowAssign(leftHandVector, leftHandStartIndex.intValue(), leftHandEndIndex.intValue(), resultList.get(i).longValue());
				} else {
					OpUtil.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
					return OpUtil.errorOccured();
				}
			 } else if(assign.leftHandSide.get(i) instanceof Identifier){
				Identifier leftHandIdent = (Identifier)assign.leftHandSide.get(i);
				Pointer<Value> leftHandPtr = environment.lookupVariable(leftHandIdent.labelIdentifier);
				leftHandPtr.assign(resultList.get(i));
			 } else {
				OpUtil.errorAndExit("Invalid Left Hand side of the expression " + assign.leftHandSide.getClass().getName());
				return OpUtil.errorOccured();
			 }
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

	/**
	 * This is the code for visiting an Identifier
	 * 
	 * @param ident
	 * @throws Exception
	 */

	 protected Value interpretShallowIdentifier(Identifier ident) throws Exception{
		if (environment.variableExists(ident.labelIdentifier)) {
			Pointer<Value> data = environment.lookupVariable(ident.labelIdentifier);
			return data.deRefrence();
		} else {
			OpUtil.errorAndExit("Variable Entry " + ident.labelIdentifier + " Doesnt Exist", ident.position);
			return OpUtil.errorOccured();
		}
	}


	protected Value interpretShallowSlice(Slice vector) throws Exception{
		String ident = vector.labelIdentifier;
		Value startIndex = interpretShallowExpression(vector.index1);
		Value endIndex = interpretShallowExpression(vector.index2);

		if (environment.variableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof VectorVal) {
				VectorVal toRet = ((VectorVal)dataObject).getShallowSlice(startIndex.intValue(), endIndex.intValue());
				return toRet;
			} else {
				OpUtil.errorAndExit("Unkown slice type for " + ident + " [ Type -> " + dataObject.getClass() + " ]");
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or VectorVal " + ident + " not found");
			return OpUtil.errorOccured();
		}
	}

	/**
	 * This is the code for visiting a VectorVal in verilog
	 * 
	 * @param string
	 * @throws Exception
	 */

	protected Value interpretShallowElement(Element Elem) throws Exception{
		String ident = Elem.labelIdentifier;
		Value expr = interpretShallowExpression(Elem.index1);

		if (environment.variableExists(ident)) {
			Pointer<Value> data = environment.lookupVariable(ident);
			Value dataObject = data.deRefrence();

			if (dataObject instanceof ArrayVal) {
				ArrayVal<VectorVal> arr = (ArrayVal<VectorVal>)dataObject;
				VectorVal vec = arr.ElemAtIndex(expr.intValue());
				return vec;
			} else if (dataObject instanceof VectorVal) {
				return ((VectorVal)dataObject).getValue(expr.intValue());
			} else if (dataObject instanceof ArrayVal) {
				return ((ArrayVal<IntVal>)dataObject).ElemAtIndex(expr.intValue());
			} else {
				OpUtil.errorAndExit("Unkown array type for " + ident + " [ Type -> " + dataObject.getClass() + " ]",
					Elem.position);
				return OpUtil.errorOccured();
			}

		} else {
			OpUtil.errorAndExit("Array or VectorVal " + ident + " not found", Elem.position);
			return OpUtil.errorOccured();
		}
	}
}
