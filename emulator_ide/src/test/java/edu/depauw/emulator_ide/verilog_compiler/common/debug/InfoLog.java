package edu.depauw.emulator_ide.verilog_compiler.common.debug;

import java.util.LinkedList;
/**
 * The Info log class is a class used to print all of the debugging information in the compiler after each pass
 * in theary this information can be notes, warnings, or errors
 * @author Jacob Bauer 
 */
public class InfoLog(){
    private List<InfoItem> infoLog;

    public InfoLog(){
	infoLog = new LinkedList<>();
    }

    public addItem(InfoItem info){
	infoLog.add(info);
    }

    public printLog(){
	for(InfoItem info : infoLog){
	    System.out.println(info.toString());
	}
    }
}
