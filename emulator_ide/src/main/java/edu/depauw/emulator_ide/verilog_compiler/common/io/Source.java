package edu.depauw.emulator_ide.verilog_compiler.common.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Source{
    
    private final Reader input;
    
    private int past;
    private int current;
    private int next;
    
    public Source(InputStream inputStream){
	this(new InputStreamReader(inputStream));
    }

    public Source(Reader inputReader){
	input = inputReader;
	try {
	    next = input.read();
	} catch (Exception e){
	    System.err.println("Error could not read incoming character and could not advance");
	}
	if(next == -1){
	    current = -1;
	    past = -1;
	} else {
	    current = 0;
	    advance();
	}
    }

    public void advance(){
	if(!atEOD()){
	    past = current;
	    current = next;
	    try {
		next = input.read();
	    } catch (Exception e){
		System.err.println("Error could not read incoming character and could not advance");
	    }
	}
    }
    
    public char getPast(){
	return (char)past;
    }

    public char getCurrent(){
	return (char)current;
    }

    public char getNext(){
	return (char)next;
    }

    public boolean hasNext(){
	return next != -1;
    }

    public boolean atEOD(){
	return current == -1;
    }

    public void close(){
	    try {
		input.close();
	    } catch (Exception e){
		System.err.println("Error: could not close input stream/reader correctly");
	    }
    }
}
