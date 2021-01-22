package edu.depauw.emulator_ide.verilog_compiler.common.debug;

import edu.depauw.emulator_ide.verilog_compiler.common.io.Destination;
import java.util.LinkedList;
import java.io.PrintStream;

/**
 * The Info log class is a class used to print all of the debugging information in the compiler after each pass
 * in theary this information can be notes, warnings, or errors
 * @author Jacob Bauer 
 */

public class InfoLog{
    private LinkedList<InfoItem> infoLog;
    private Destination output;

    public InfoLog(Destination output){
	infoLog = new LinkedList<>();
        this.output = output;
    }

    public InfoLog(){
	infoLog = new LinkedList<>();
        this.output = new Destination(new PrintStream(System.out));
    }

    public void addItem(InfoItem info){
	infoLog.add(info);
    }

    public void printLog(){
	for(InfoItem info : infoLog){
	    output.println(info.toString());
	}
    }
}
