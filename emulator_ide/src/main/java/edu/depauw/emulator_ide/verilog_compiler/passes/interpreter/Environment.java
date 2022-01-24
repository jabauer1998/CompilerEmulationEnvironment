package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter;

import java.util.Stack;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.FunctionDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.procedure_declaration.TaskDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.SymbolTable;

/**
 * The environment class is what is supplied to the interpreter in order to Interpret the code
 */

public class Environment {
    private SymbolTable<ModuleDeclaration>  moduleTable;
	private SymbolTable<TaskDeclaration> taskTable;
	private SymbolTable<FunctionDeclaration> functionTable;
	private SymbolTable<Pointer<Value>> variableTable;

    private Stack<String>                                callStack = new Stack<String>();
	private Stack<Boolean>                               exitStack  = new Stack<Boolean>();

    public Environment(){
        moduleTable = new SymbolTable<>();
        taskTable = new SymbolTable<>();
        functionTable = new SymbolTable<>();
        variableTable = new SymbolTable<>();
        callStack = callStack;
        exitStack = exitStack;
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



    public void addScope(){
        variableTable.addScope();
    }

    public void removeScope(){
        variableTable.removeScope();
    }

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

}
