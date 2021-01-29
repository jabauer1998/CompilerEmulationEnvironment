package edu.depauw.emulator_ide.verilog_compiler.token;

import java.lang.String;

public class Position{
    private final int lineNumber;
    private final int linePosition;

    public Position(int lineNumber, int linePosition){
	this.lineNumber = lineNumber;
	this.linePosition = linePosition;
    }

    @Override
    public String toString(){
	return "Line number: " + lineNumber + " Position: " + linePosition; 
    }
}
