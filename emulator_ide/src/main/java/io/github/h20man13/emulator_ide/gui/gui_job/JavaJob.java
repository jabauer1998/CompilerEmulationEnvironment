package io.github.H20man13.emulator_ide.gui.gui_job;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Callable;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.gui.GuiEde;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class JavaJob extends GuiJob{
    private Callable<Void> functionToRun;
    private GuiEde edeInstance;
    private String errorPane;
    private String inputFile;
    private String outputFile;
    private List<Region> guiJobs;
    

    public JavaJob(String buttonText, TextAreaType type, double width, double height, Callable<Void> functionToRun, String inputFile, String outputFile, String errorPane, String[] keywords, List<Region> guiJobs,  GuiEde edeInstance){
        super(buttonText, type, width, height, keywords);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
        this.inputFile = inputFile;
        this.errorPane = errorPane;
        this.outputFile = outputFile;
        this.guiJobs = guiJobs;
    }

    private void copyOverDataToInputFile(){
        Region reg = this.getInputSection();

        if(reg instanceof InlineCssTextArea){
            InlineCssTextArea ta = (InlineCssTextArea)reg;
            String textToCopy = ta.getText();
            File iFile = new File(inputFile);
            if(!iFile.exists()){
                iFile.delete();
            }

            try {
                iFile.createNewFile();
                FileWriter Writer = new FileWriter(iFile);
                Writer.write(textToCopy);
                Writer.flush();
                Writer.close();
            } catch(Exception exp){
                edeInstance.appendIoText(errorPane, exp.toString());
            }
        } else {
            TextArea ta = (TextArea)reg;
            String textToCopy = ta.getText();
            File iFile = new File(inputFile);
            if(!iFile.exists()){
                iFile.delete();
            }

            try {
                iFile.createNewFile();
                FileWriter Writer = new FileWriter(iFile);
                Writer.write(textToCopy);
                Writer.flush();
                Writer.close();
            } catch(Exception exp){
                edeInstance.appendIoText(errorPane, exp.toString());
            }
        }
    }

    private void collectDataFromOutputFile(){
        for(int i = 0; i < guiJobs.size(); i++){
            Region localArea = guiJobs.get(i);
            if(localArea.hashCode() == this.getInputSection().hashCode()){
                Region nextTextArea = guiJobs.get(i + 1);
                if(nextTextArea instanceof InlineCssTextArea){
                    InlineCssTextArea ta = (InlineCssTextArea)nextTextArea;
                    ta.replaceText("");
                    try{
                        FileReader reader = new FileReader(outputFile);
                        //Write all Text to the Next Text Area
                        StringBuilder sb = new StringBuilder();
                        while(reader.ready()){
                            sb.append((char)reader.read());
                        }
                        ta.replaceText(sb.toString());
                        reader.close();
                    } catch(Exception exp){
                        edeInstance.appendIoText(errorPane, exp.toString());
                    }
                } else {
                    TextArea ta = (TextArea)nextTextArea;
                    ta.setText("");
                    try{
                        FileReader reader = new FileReader(outputFile);
                        //Write all Text to the Next Text Area
                        StringBuilder sb = new StringBuilder();
                        while(reader.ready()){
                            sb.append((char)reader.read());
                        }
                        ta.setText(sb.toString());

                        reader.close();
                    } catch(Exception exp){
                        edeInstance.appendIoText(errorPane, exp.toString());
                    }
                }
            }
        }
    }

    @Override
    public void RunJob(){
        copyOverDataToInputFile();
        try {
            functionToRun.call();
        } catch (Exception e) {
            // TODO Auto-generated catch block
           edeInstance.appendIoText(errorPane, e.toString());
        }
        collectDataFromOutputFile();
    }

    


}
