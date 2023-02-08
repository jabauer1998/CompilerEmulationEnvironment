package edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor;

import java.util.LinkedList;
import java.util.List;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;

/**
 * This entry class is designed to hold MacroExpansions
 * Macro expansions have two types of data. They consist of A Paramater List, Which is a list of Tokens. THey also consist of a List of tokens to represent the definitions.

 */
public class MacroExpansionData {
    public List<String> paramaterList;
    public List<Token> declarationList;

    public MacroExpansionData(){
        paramaterList = new LinkedList<>();
        declarationList = new LinkedList<>();
    }

    public MacroExpansionData(List<String> paramaterList, List<Token> declarationList){
        this.paramaterList = paramaterList;
        this.declarationList = declarationList;
    }
}