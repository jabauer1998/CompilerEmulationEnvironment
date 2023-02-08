package edu.depauw.emulator_ide.verilog_compiler.main;


import org.junit.Test;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor.Preprocessor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PreprocessorTest {
    
    @Test
    public void testBasicDefinition(){
        
        System.out.println("-----Basic Macro Define Test----");
		
        String input = "`define a 5 + 5\n `a"; //The input strig to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        List<Token> tokens = lexer.tokenize();
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();

        //Check to see if error log was empty after preprocessing
        errorLog.printLog();
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC); // 5
        tokTypes.add(Token.Type.DEC); // +
        tokTypes.add(Token.Type.DEC); // 5

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testAdvancedDefinition1(){
        
        System.out.println("-----Advanced Macro Define Test 1----");
		
        String input = "`define a(A) A + A\n `a(30) - `a(50)"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = lexer.tokenize();
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC); //30
        tokTypes.add(Token.Type.PLUS); //+
        tokTypes.add(Token.Type.DEC); //30
        tokTypes.add(Token.Type.MINUS); //-
        tokTypes.add(Token.Type.DEC); //50
        tokTypes.add(Token.Type.PLUS); //+
        tokTypes.add(Token.Type.DEC); //50

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testAdvancedDefinition2(){
        
        System.out.println("-----Basic Macro Define Test 2----");
		
        String input = "`define a(A) A + A\n `a(30 - 50)"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC); //30
        tokTypes.add(Token.Type.PLUS); //-
        tokTypes.add(Token.Type.DEC); //50
        tokTypes.add(Token.Type.MINUS); //+
        tokTypes.add(Token.Type.DEC); //30
        tokTypes.add(Token.Type.PLUS); //-
        tokTypes.add(Token.Type.DEC); //50

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testBasicIfTrue(){
        
        System.out.println("-----Basic If True Test ----");
		
        String input = "`define a 50\n `ifdef a \n90\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC); //90

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testBasicIfFalse(){
        
        System.out.println("-----Basic If False Test ----");
		
        String input = "`define b 50\n `ifdef a \n90\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testIfElseTest(){
        
        System.out.println("-----Basic If Else Test ----");
		
        String input = "`define b 50\n `ifdef a\n90\n`elseif b\n30 + 30 + 30\n`elseif c\n40 - 40\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC);
        tokTypes.add(Token.Type.PLUS);
        tokTypes.add(Token.Type.DEC);
        tokTypes.add(Token.Type.PLUS);
        tokTypes.add(Token.Type.DEC);

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testIfNot(){
        
        System.out.println("-----Basic If Not Else Test ----");
		
        String input = "`define b 50\n `ifndef c\n90\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC);

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test 
    public void testBasicElse(){
        System.out.println("----- Basic Else  ----");
		
        String input = "`define c 50\n `ifndef c\n`elseif d\n50\n`else\n50 + 50\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC);
        tokTypes.add(Token.Type.PLUS);
        tokTypes.add(Token.Type.DEC);

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    @Test
    public void testNestedIfElse(){
        
        System.out.println("----- Nested If Else  ----");
		
        String input = "`define b 50\n `ifndef c\n`ifdef d\n50\n`else\n50 + 50\n`endif\n`endif"; //The input string to pass into the file
        Destination display = new Destination(System.out); //The output to pass into the errorlog
		Source source = new Source(new StringReader(input));

        ErrorLog errorLog = new ErrorLog(display);

        Lexer lexer = new Lexer(source, errorLog);

        //Preprocesses the lexed token list and fetch the result
        LinkedList<Token> tokens = new LinkedList<>(lexer.tokenize());
        Preprocessor preProcessor = new Preprocessor(errorLog);
        preProcessor.attachList(tokens);
        preProcessor.executePass();
        List<Token> preProcessorResultList = preProcessor.fetchResult();
        
        //Check to see if error log was empty after preprocessing
        assertTrue("Error: the error log isnt empty", errorLog.size() == 0);

        //Create array list of types to check against
        //30 + 30 - 50 + 50
        ArrayList<Token.Type> tokTypes = new ArrayList<>();
        tokTypes.add(Token.Type.DEC);
        tokTypes.add(Token.Type.PLUS);
        tokTypes.add(Token.Type.DEC);

        testPreprocessor(tokTypes, preProcessorResultList);
    }

    public static void testPreprocessor(ArrayList<Token.Type> tokTypes, List<Token> preProcResList){
        tokTypes.add(Token.Type.EOF);
        assertNotNull("Null list from preprocessor provided", preProcResList);
        assertNotNull("Null list of token types provided", tokTypes);
        assertFalse("Need " + (preProcResList.size() - tokTypes.size()) + " more token types to test the preprocessed token list", tokTypes.size() < preProcResList.size());
        assertFalse("Have " + (tokTypes.size() - preProcResList.size()) + " to many token types to test the preprocessor output", tokTypes.size() > preProcResList.size());

        //Now check if each tokType is found in the queue
        for(int i = 0; i < tokTypes.size(); i++){
            Token tok = preProcResList.remove(0);
            assertEquals("At token " + i + " exected " + tokTypes.get(i) + " but found " + tok.getTokenType(), tokTypes.get(i), tokTypes.get(i));
        }
    }
}
