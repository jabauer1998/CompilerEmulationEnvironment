package edu.depauw.emulator_ide.verilog_compiler.parser;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Context;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.table_entry.MacroExpansionData;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

public class Preprocessor {
    
    private ErrorLog errorLog;
    private List<Token> tokenList;
    private List<Token> resultList;
    private Context context;

    private Environment<String, List<Token>> macroDefinitions;
    private Environment<String, MacroExpansionData> macroExpansions;
    
    public Preprocessor(ErrorLog errorLog){
        ;
        this.errorLog = errorLog;
        this.tokenList = new LinkedList<>(); //input queue
        this.resultList = new LinkedList<>(); //result queue
        this.context = Context.getContext();

        macroDefinitions = new Environment<>();
        macroExpansions = new Environment<>();

        macroDefinitions.addScope();
        macroExpansions.addScope();
        
    }

    /**
     * The Preprocessor skip method works a bit strangely it will actually move a token to the result list
     */

    private void skipAndAppend(){
        Token toAppend = skip();
        this.resultList.add(toAppend);
        
    }

    private Token skip(){
        return tokenList.remove(0);
    }

    private boolean willMatch(Token.Type type){
        if(tokenList.isEmpty())
            return false;
            
        return tokenList.get(0).getTokenType() == type;
    }

    private boolean eatIfYummy(Token.Type type){
        if(willMatch(type)) { skip(); return true;}
        else return false;
    }

    private Token match(Token.Type type){
        if(willMatch(type))
            return skip();

        if(tokenList.isEmpty()){
            ErrorItem unexpectedEofError = new ErrorItem("When matching " + type + " unexpected eof found without eof token");
            errorAndExit(unexpectedEofError);
            return null;
        }
        
        Token tok = skip();
        ErrorItem unexpectedTokenError = new ErrorItem("Token of type " + type + " expected but token of type " + tok.getTokenType() + " found", tok.getPosition());
        errorAndExit(unexpectedTokenError);
        return null;
    }

    void errorAndExit(ErrorItem item){
        errorLog.addItem(item);
        errorLog.printLog();
        System.exit(1);
    }

    public void attachList(List<Token> tokenList){
        this.tokenList = tokenList;
    }

    private void processDefine(){
        match(Token.Type.MACRO_DEFINE);
        Token ident = match(Token.Type.IDENT);
        String identLex = ident.getLexeme();

        if(eatIfYummy(Token.Type.LPAR)){ //Process Macro expansion ie... def(A) = A - A

            List<String> params = new LinkedList<>();

            do{
                Token tok = match(Token.Type.IDENT);
                String lex = tok.getLexeme();
                params.add(lex);
            } while(eatIfYummy(Token.Type.COMMA));

            match(Token.Type.RPAR);

            List<Token> definition = new LinkedList<>();
            
            while(!willMatch(Token.Type.NEWLINE)){
                Token tok = skip();
                if(tok.getTokenType() != Token.Type.ESCAPEDLINE){
                    definition.add(tok);
                }
            }

            match(Token.Type.NEWLINE);

            MacroExpansionData data = new MacroExpansionData(params, definition);

            macroExpansions.addEntry(identLex, data);

        } else { //Process standard definition
            List<Token> definition = new LinkedList<>();
            
            while(!willMatch(Token.Type.NEWLINE)){
                Token t = skip();
                definition.add(t);
            }

            match(Token.Type.NEWLINE); //skip the new line at the end

            macroDefinitions.addEntry(identLex, definition);
        }
    }

    private void processInclude(){
        match(Token.Type.MACRO_INCLUDE);
        Token pathTok = match(Token.Type.STRING);
        String pathRaw = pathTok.toString();
        File fileData = new File(pathRaw);
        context.programFiles.put(pathRaw, fileData); //add file to context
    }

    private void processMacroIdentifier(){
        Token callTok = match(Token.Type.MACRO_IDENT);
        String callRaw = callTok.getLexeme().substring(1);
        Position callPosition = callTok.getPosition();

        if(willMatch(Token.Type.LPAR) && macroExpansions.entryExists(callRaw)){
            skip();

            MacroExpansionData macroExpansionData = macroExpansions.getEntry(callRaw);
            List<List<Token>> paramaters = new LinkedList<>();

            do {
                List<Token> tokParamaterExpression = new LinkedList<>();
                while(!willMatch(Token.Type.COMMA) && !willMatch(Token.Type.RPAR)){
                    Token tok = skip();
                    tokParamaterExpression.add(tok);
                }
                paramaters.add(tokParamaterExpression);
            } while(eatIfYummy(Token.Type.COMMA));

            match(Token.Type.RPAR);

            //Now get the function declaration and the list that belongs to that function

            int definedParamAmount = macroExpansionData.paramaterList.size();
            int callParamAmount = paramaters.size();

            if(paramaters.size() != macroExpansionData.paramaterList.size()){
                //Print Error incorrect number of paramaters found
                ErrorItem item = new ErrorItem("The defined paramater amount in expansion " + callRaw + " is " + definedParamAmount + " however when called " + callParamAmount + " were supplied", callPosition);
                errorLog.addItem(item);
                return;
            }
            // Add all Paramaaters to the definitions table
            macroDefinitions.addScope();
            for(int i = 0; i < macroExpansionData.paramaterList.size(); i++){
                String defParam = macroExpansionData.paramaterList.get(i);
                List<Token> callParam = paramaters.get(i);
                macroDefinitions.addEntry(defParam, callParam);
            }

            for(Token tok: macroExpansionData.declarationList){
                //If the token is an ident which can mean it is a possible paramater
                boolean isPossibleParam = tok.getTokenType() == Token.Type.IDENT;

                //Check to see if the identifier is in the top scope
                String identLexeme = tok.getLexeme();
                boolean inCurrentScope = macroDefinitions.inScope(identLexeme);

                //If it is a valid ident token and it is in the top scope that was created for paramaters then it is a paramater
                if(isPossibleParam && inCurrentScope){ //IF it is a valid paramater then substitute it with 
                    resultList.addAll(macroDefinitions.getEntry(tok.getLexeme())); //Add all tokens for param
                } else {
                    resultList.add(tok); //just add the basic token to the result list
                }
            }
            //Delete the new scope that was created
            macroDefinitions.removeScope();

        } else {
            //Collect info from default list
            if(macroDefinitions.entryExists(callRaw)){
                resultList.addAll(macroDefinitions.getEntry(callRaw));
            } else {
                ErrorItem error = new ErrorItem("Error: Entry by the name of " + callRaw + " doesnt exist ", callPosition);
                errorLog.addItem(error);
            }
        }
    }

    private void skipToEndIf(){
        while(!willMatch(Token.Type.MACRO_ENDIF) && !willMatch(Token.Type.EOF)) skip();
    }

    private void skipToElseIfOrElseOrEndIf(){
        while(!willMatch(Token.Type.MACRO_ENDIF) && !willMatch(Token.Type.MACRO_ELSEIF) && !willMatch(Token.Type.MACRO_ELSE) && !willMatch(Token.Type.EOF)) skip();
    }

    private void processElse(){
        match(Token.Type.MACRO_ELSE);
        
        while(!willMatch(Token.Type.MACRO_ENDIF)) processToken();
        
        match(Token.Type.MACRO_ENDIF);
    }

    private void processElseIf(){
        match(Token.Type.MACRO_ELSEIF);
        Token flag = match(Token.Type.IDENT);
        String flagLexeme = flag.getLexeme();

        if(macroDefinitions.entryExists(flagLexeme)){
            while(!willMatch(Token.Type.MACRO_ENDIF) && !willMatch(Token.Type.MACRO_ELSEIF) && !willMatch(Token.Type.MACRO_ELSE)) processToken();

            if(willMatch(Token.Type.MACRO_ELSEIF) || willMatch(Token.Type.MACRO_ELSE)) skipToEndIf();

            match(Token.Type.MACRO_ENDIF);
        } else {
            skipToElseIfOrElseOrEndIf();

            if(willMatch(Token.Type.MACRO_ELSEIF)) processElseIf();
            else if(willMatch(Token.Type.MACRO_ELSE)) processElse();
            else match(Token.Type.MACRO_ENDIF);
        }

    }

    private void processIfDef(){
        match(Token.Type.MACRO_IFDEF);
        Token flag = match(Token.Type.IDENT);
        String flagLexeme = flag.getLexeme();

        //Check if the flag exists
        if(macroDefinitions.entryExists(flagLexeme)){
            while(!willMatch(Token.Type.MACRO_ENDIF) && !willMatch(Token.Type.MACRO_ELSEIF) && !willMatch(Token.Type.MACRO_ELSE)) processToken();

            if(willMatch(Token.Type.MACRO_ELSEIF) || willMatch(Token.Type.MACRO_ELSE)) skipToEndIf();

            match(Token.Type.MACRO_ENDIF);
        } else {
            skipToElseIfOrElseOrEndIf();

            if(willMatch(Token.Type.MACRO_ELSEIF)) processElseIf();
            else if(willMatch(Token.Type.MACRO_ELSE)) processElse();
            else match(Token.Type.MACRO_ENDIF);
        }
    }

    private void processIfNDef(){
        match(Token.Type.MACRO_IFNDEF);
        Token flag = match(Token.Type.IDENT);
        String flagLexeme = flag.getLexeme();

        //Check if the flag exists
        if(!macroDefinitions.entryExists(flagLexeme)){
            while(!willMatch(Token.Type.MACRO_ENDIF) && !willMatch(Token.Type.MACRO_ELSEIF) && !willMatch(Token.Type.MACRO_ELSE)) processToken();

            if(willMatch(Token.Type.MACRO_ELSEIF) || willMatch(Token.Type.MACRO_ELSE)) skipToEndIf();

            match(Token.Type.MACRO_ENDIF);
        } else {
            skipToElseIfOrElseOrEndIf();

            if(willMatch(Token.Type.MACRO_ELSEIF)) processElseIf();
            else if(willMatch(Token.Type.MACRO_ELSE)) processElse();
            else match(Token.Type.MACRO_ENDIF);
        }
    }

    public void processToken(){
        if(willMatch(Token.Type.MACRO_DEFINE)) processDefine();
        else if(willMatch(Token.Type.MACRO_INCLUDE)) processInclude();
        else if(willMatch(Token.Type.MACRO_IDENT)) processMacroIdentifier();
        else if(willMatch(Token.Type.MACRO_IFNDEF)) processIfNDef();
        else if(willMatch(Token.Type.MACRO_IFDEF)) processIfDef();
        else skipAndAppend();
    }

    public void executePass(){
        while(!willMatch(Token.Type.EOF)) processToken();
        skipAndAppend(); //append eof token
    }

    public List<Token> fetchResult(){ //if not filtered filter out all new line and escaped line tokens

        return Lexer.filterWhiteSpace(this.resultList);

    }
}
