package io.github.h20man13.emulator_ide.common;


import java.util.Stack;
import java.util.HashMap;
import java.lang.String;
import java.lang.StringBuilder;

/**
 * The environment class is used to create symbol tables for the Declan Compiler The
 * symbol tables are implemented using hashmaps and the scopes are implemented using
 * stacks The keys must allways be strings however the entries are an object of your
 * choice
 * 
 * @author Jacob Bauer
 */
public class SymbolTable<TableType> {

    /**
     * The environment stack is how scopes are implemented
     * 
     * @author Jacob Bauer
     */
    private Stack<HashMap<String, TableType>> table;

    /**
     * The costructor dynamicaly initailizes the stack
     * 
     * @author Jacob Bauer
     */
    public SymbolTable() { table = new Stack<>(); }

    /**
     * This is the method that removes the top scope or hashmap in the stack
     * 
     * @author Jacob Bauer
     */
    public void removeScope(){ table.pop(); }

    /**
     * This is the method that adds a scope or hashmap to the stack
     * 
     * @author Jacob Bauer
     */
    public void addScope(){ table.push(new HashMap<>()); }

    /**
     * This method is used to check if a variable exists within the entire stack
     * 
     * @param  <code> symbolName </code> => String => the name of the symbol you want to
     *                find
     * @author        Jacob Bauer
     */
    public boolean entryExists(String symbolName){
        for (int i = table.size() - 1; i >= 0; i--){
            HashMap<String, TableType> current = table.get(i); 
            if (current.containsKey(symbolName)) { 
                return true; 
            }
        }

        return false;
    }

    /**
     * This method is used to check if a variable exists within the current scope
     * 
     * @param  <code> symbolName </code> => String => the name of the symbol you want to
     *                find
     * @author        Jacob Bauer
     */
    public boolean inScope(String symbolName){
        HashMap<String, TableType> list = table.pop();
        boolean tf = list.containsKey(symbolName);
        table.push(list);
        return tf;
    }

    /**
     * The get Entry method tries to find the symbolname passed and it returns the data
     * corresponding to the symbol
     * 
     * @param  <code> symbolName </code> => String => the symbol name passed
     * @author        Jacob Bauer
     */

    public TableType getEntry(String symbolName){

        for (int i = table.size() - 1; i >= 0; i--) {
            HashMap<String, TableType> current = table.get(i);
            if (current.containsKey(symbolName)) { 
                return current.get(symbolName); 
            }
        }

        return null;
    }

    /**
     * To add the entry to the symbol table
     * 
     */
    public void addEntry(String name, TableType description){
        HashMap<String, TableType> saved = table.pop();
        saved.put(name, description);
        table.push(saved);
    }

    /**
     * To String
     */

    @Override
    public String toString(){
        StringBuilder mystring = new StringBuilder();

        for (int i = table.size() - 1; i >= 0; i--) {
            mystring.append("STACK LEVEL -> " + i + '\n');
            HashMap<String, TableType> list = table.get(i);

            for (String key : list.keySet()) {
                mystring.append("KEY: " + key + " VALUE: ");
                mystring.append(list.get(key).toString());
                mystring.append('\n');
            }

        }

        return mystring.toString();
    }
}
