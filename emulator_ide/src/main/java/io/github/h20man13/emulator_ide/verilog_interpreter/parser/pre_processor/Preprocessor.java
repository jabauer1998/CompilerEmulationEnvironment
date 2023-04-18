package io.github.h20man13.emulator_ide.verilog_interpreter.parser.pre_processor;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.LinkedList;
import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.common.SymbolTable;
import io.github.h20man13.emulator_ide.common.debug.ErrorLog;
import io.github.h20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.h20man13.emulator_ide.common.io.Source;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.Lexer;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.Token;

public class Preprocessor {
    
    private ErrorLog errorLog;
    private List<Token> tokenList;
    private List<Token> resultList;

    private SymbolTable<List<Token>> macroDefinitions;
    private SymbolTable<MacroExpansionData> macroExpansions;
    
    public Preprocessor(ErrorLog errorLog, List<Token> tokenList){

        this.errorLog = errorLog;
        this.tokenList = tokenList; //input queue
        this.resultList = new LinkedList<>(); //result queue

        macroDefinitions = new SymbolTable<>();
        macroExpansions = new SymbolTable<>();

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
                if(!willMatch(Token.Type.ESCAPEDLINE)){
                    Token tok = skip();
                    definition.add(tok);
                } else {
                    skip();
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
        try{
            Source fReader = new Source(new FileReader(fileData));
            Lexer Lex = new Lexer(fReader, errorLog);
            List<Token> Toks = Lex.tokenize();

            //Remove the EOF Token if Lexed from an Include Stmt
            //This will prevent having mutiple EOF tokens throughout the file
            Toks.remove(Toks.size() - 1);
            
            //Append all the Tokens to the List so they can be processed by the 
            //Preprocessor before moving on to later Tokens
            Toks.addAll(tokenList);
            this.tokenList = Toks;
        } catch(Exception exp){
            errorLog.addItem(new ErrorItem("Preprocessor could not include the file specified. Exception Occured " + exp));
        }
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

    public List<Token> executePass(){
        while(!willMatch(Token.Type.EOF)) processToken();
        skipAndAppend(); //append eof token

        List<Token> filteredList = new LinkedList<Token>();
        for(Token Tok : resultList){
            if(Tok.getTokenType() != Token.Type.NEWLINE && Tok.getTokenType() != Token.Type.ESCAPEDLINE){
                filteredList.add(Tok);
            }
        }

        return filteredList;
    }


}
