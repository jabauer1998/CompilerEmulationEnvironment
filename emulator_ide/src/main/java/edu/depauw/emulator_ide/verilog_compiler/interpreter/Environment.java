package edu.depauw.emulator_ide.verilog_compiler.interpreter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import edu.depauw.emulator_ide.common.Pointer;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;

/**
 * The environment class is what is supplied to the interpreter in order to Interpret the code
 * This includes several SymbolTables for each Type of Entry as well as a call stack
 */

public class Environment {
    private SymbolTable<ModuleDeclaration>  moduleTable;
	private SymbolTable<TaskDeclaration> taskTable;
	private SymbolTable<FunctionDeclaration> functionTable;
	private SymbolTable<Pointer<Value>> variableTable;

    private Stack<String> callStack;
	private Stack<Boolean> exitStack;

    private ArrayList<FileReader> readOnlyFileDescriptorArray;
    private ArrayList<FileWriter> writableFileDescriptorArray;

    private boolean InParamaterSequence;
    private boolean InFunctionBody;

    public Environment(){
        moduleTable = new SymbolTable<>();
        taskTable = new SymbolTable<>();
        functionTable = new SymbolTable<>();
        variableTable = new SymbolTable<>();
        callStack = new Stack<String>();
        exitStack = new Stack<Boolean>();
        readOnlyFileDescriptorArray = new ArrayList<FileReader>();
        writableFileDescriptorArray = new ArrayList<FileWriter>();

        moduleTable.addScope();
        taskTable.addScope();
        functionTable.addScope();
        variableTable.addScope();
    }

    /**
     * the module exists function is supposed to determine if a module exists ot not
     * @param symbol
     * @return boolean rerpresenting whether the module exists ot not
     */

    public boolean moduleExists(String symbol){
        return moduleTable.entryExists(symbol);
    }

    /**
     * the task Exists function is supposed to determine if a task exists ot not
     * @param symbol
     * @return boolean rerpresenting whether the task Exists ot not
     */

    public boolean taskExists(String symbol){
        return taskTable.entryExists(symbol);
    }

    /**
     * the functionExists function is supposed to determine if a functionExists or not
     * @param symbol
     * @return boolean rerpresenting whether the function exists ot not
     */

    public boolean functionExists(String symbol){
        return functionTable.entryExists(symbol);
    }

    /**
     * the localVariableExists function is supposed to determine if a variableExists or not
     * @param symbol
     * @return boolean rerpresenting whether the variable exists ot not
     */


    public boolean localVariableExists(String symbol){
        return variableTable.inScope(symbol);
    }

    /**
     * the variableExists function is supposed to determine if a variableExists or not
     * @param symbol
     * @return boolean rerpresenting whether the variable exists ot not
     */


    public boolean variableExists(String symbol){
        return variableTable.entryExists(symbol);
    }

    /**
     * 
     */

    public FunctionDeclaration lookupFunction(String symbol){
        return functionTable.getEntry(symbol);
    }

    public Pointer<Value> lookupVariable(String symbol){
        return variableTable.getEntry(symbol);
    }

    public TaskDeclaration lookupTask(String symbol){
        return taskTable.getEntry(symbol);
    }

    public ModuleDeclaration lookupModule(String symbol){
        return moduleTable.getEntry(symbol);
    }

    public void addModule(String symbol, ModuleDeclaration decl){
        moduleTable.addEntry(symbol, decl);
    }

    public void addTask(String symbol, TaskDeclaration decl){
        taskTable.addEntry(symbol, decl);
    }

    public void addFunction(String symbol, FunctionDeclaration decl){
        functionTable.addEntry(symbol, decl);
    }

    public void addVariable(String symbol, Value value){
        Pointer<Value> valuePointer = new Pointer<Value>(value);
        variableTable.addEntry(symbol, valuePointer);
    }

    public void addStackFrame(String ScopeName){
        exitStack.push(false);
        callStack.push(ScopeName);
        variableTable.addScope();
    }

    public String removeStackFrame(){
        if(!exitStack.isEmpty())
            exitStack.pop();

        variableTable.removeScope();
        
        if(!callStack.isEmpty())
            return callStack.pop();

        return null;
    }

    public String stackFrameTitle(){
        if(callStack.isEmpty()) 
            return null;
        return callStack.peek();
    }

    public boolean stackFrameInExit(){
        if(exitStack.isEmpty())
            return false;

        return exitStack.peek();
    }

    public void setFunctionExit(){
        if(!exitStack.isEmpty()) 
            exitStack.pop();
        
        exitStack.push(true);
    }

    public void BeginParamaterDeclarations(){
        InParamaterSequence = true;
    }

    public void EndParamaterDeclarations(){
        InParamaterSequence = false;
    }

    public boolean DeclarationIsParamater(){
        return InParamaterSequence;
    }

    public void BeginFunctionBody(){
        InFunctionBody = true;
    }

    public void EndFunctionBody(){
        InFunctionBody = false;
    }

    public boolean InFunctionBody(){
        return InFunctionBody;
    }

    public int createReadOnlyFileDescriptor(String fileName){
        try{
            FileReader scanner = new FileReader(fileName);
            for(int i = 0; i < readOnlyFileDescriptorArray.size(); i++){
                if(readOnlyFileDescriptorArray.get(i) == null){
                    readOnlyFileDescriptorArray.set(i, scanner);
                    return i;
                }
            }

            readOnlyFileDescriptorArray.add(scanner);
            return readOnlyFileDescriptorArray.size() - 1;
        } catch(Exception exp) {
            return -1;
        }
    }

    public int createWritableFileDescriptor(String fileName){
        try{
            FileWriter writer = new FileWriter(fileName);
            for(int i = 0; i < writableFileDescriptorArray.size(); i++){
                if(writableFileDescriptorArray.get(i) == null){
                    writableFileDescriptorArray.set(i, writer);
                    return i;
                }
            }

            writableFileDescriptorArray.add(writer);
            return writableFileDescriptorArray.size() - 1;
        } catch(Exception exp) {
            return -1;
        }
    }

    public FileReader getFileReader(int fileDescriptor){
        return readOnlyFileDescriptorArray.get(fileDescriptor);
    }

    public void clearFileReader(int fileDescriptor){
        readOnlyFileDescriptorArray.set(fileDescriptor, null);
    }

    public FileWriter getFileWriter(int fileDescriptor){
        return writableFileDescriptorArray.get(fileDescriptor);
    }

    public void clearFileWriter(int fileDescriptor){
        writableFileDescriptorArray.set(fileDescriptor, null);
    }
}
