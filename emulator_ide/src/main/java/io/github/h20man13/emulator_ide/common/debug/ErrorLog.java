package io.github.h20man13.emulator_ide.common.debug;


import io.github.h20man13.emulator_ide.common.debug.item.InfoItem;
import io.github.h20man13.emulator_ide.common.io.Destination;

import java.util.LinkedList;
import java.io.OutputStreamWriter;

/**
 * The Info log class is a class used to print all of the debugging information in the
 * compiler after each pass in theary this information can be notes, warnings, or errors
 * 
 * @author Jacob Bauer
 */

public class ErrorLog {

    private LinkedList<InfoItem> infoLog;
    private Destination          output;

    public ErrorLog(Destination output) {
        infoLog = new LinkedList<>();
        this.output = output;
    }

    public ErrorLog() {
        infoLog = new LinkedList<>();
        this.output = new Destination(new OutputStreamWriter(System.out));
    }

    public void addItem(InfoItem info){ infoLog.add(info); }

    public void printLog(){

        for (InfoItem info : infoLog) { output.println(info.toString()); }

        output.flush();
        output.close();
    }

    public void printLogNoFlush(){ for (InfoItem info : infoLog) { output.println(info.toString()); } }

    public int size(){ return infoLog.size(); }
}
