package io.github.h20man13.emulator_ide.common.debug;


import io.github.h20man13.emulator_ide.common.io.Destination;

import java.io.PrintStream;

public class Debugger {

    private Destination output;
    private boolean     debugEnabled;

    public Debugger(Destination output) {
        this.output = output;
        this.debugEnabled = false;
    }

    public Debugger() { this(new Destination(new PrintStream(System.out))); }

    public void enable(){ debugEnabled = true; }

    public void disable(){ debugEnabled = false; }

    public void print(String toPrint){ if (debugEnabled) { output.print(toPrint); } }

    public void println(String toPrint){ if (debugEnabled) { output.println(toPrint); } }

}
