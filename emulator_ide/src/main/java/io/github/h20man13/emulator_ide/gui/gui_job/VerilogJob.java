package io.github.H20man13.emulator_ide.gui.gui_job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.io.Destination;
import io.github.H20man13.emulator_ide.gui.GuiEde;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.EdeInterpreter;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class VerilogJob extends GuiJob{
    private GuiEde edeInstance;
    private String errorPane;
    private String verilogFile;
    private String inputFile;
    private String outputPane;
    private String inputPane;

    public VerilogJob(String JobName, double Width, double Height, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde Ede){
        super(JobName, TextAreaType.DEFAULT, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.inputFile = inputFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
    }

    @Override
    public void RunJob(){ // TODO Auto-generated method stub
        CopyDataToOutputFile();
        StringWriter writer = new StringWriter();
        Destination Dest = new Destination(writer);
        ErrorLog errLog = new ErrorLog(Dest);
        EdeInterpreter interpreter = new EdeInterpreter(errLog, edeInstance, outputPane, inputPane);
        interpreter.interpretFile(verilogFile);
        errLog.printLog();
        edeInstance.appendIoText(errorPane, writer.toString());
    }

    public void CopyDataToOutputFile(){
        File File = new File(inputFile);
        if(File.exists()){
            File.delete();
        }

        try {
            File.createNewFile();
            FileWriter Writer = new FileWriter(File);
            Region tr = this.getInputSection();
            
            if(tr instanceof InlineCssTextArea){
                InlineCssTextArea ta = (InlineCssTextArea)tr;
                Writer.write(ta.getText());
            } else {
                TextArea ta = (TextArea)tr;
                Writer.write(ta.getText());
            }
            
            Writer.close();
        } catch (IOException e) {
            edeInstance.appendIoText(errorPane, e.toString());
        }
    }
}
