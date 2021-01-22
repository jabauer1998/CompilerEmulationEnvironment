package edu.depauw.emulator_ide.verilog_compiler.common.source;

import java.io.InputStream;
import java.io.Reader;

public class Source{
    
    private final Reader input;
    
    private int past;
    private int current;
    private int next;
    
    public Source(InputStream inputStream){
	this(new inputStreamReader(inputStream));
    }

    public Source(Reader inputReader){
	input = inputReader;
	next = inputReader.read();
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
	    next = inputStream.read();
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

    public boolean atEOD(){
	return current == -1;
    }

    public void close(){
	input.close();
    }
}
