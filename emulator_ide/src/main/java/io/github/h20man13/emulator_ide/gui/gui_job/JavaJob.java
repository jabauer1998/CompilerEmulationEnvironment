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

public class JavaJob extends GuiJob{
    private Callable<Void> functionToRun;
    private GuiEde edeInstance;
    private String errorPane;
    private String inputFile;
    private String outputFile;
    private List<InlineCssTextArea> guiJobs;

    public JavaJob(String buttonText, double width, double height, Callable<Void> functionToRun, String inputFile, String outputFile, String errorPane, String[] keywords, List<InlineCssTextArea> guiJobs,  GuiEde edeInstance){
        super(buttonText, width, height, keywords);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
        this.inputFile = inputFile;
        this.errorPane = errorPane;
        this.outputFile = outputFile;
        this.guiJobs = guiJobs;
    }

    private void copyOverDataToInputFile(){
        String textToCopy = this.getInputSection().getText();
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

    private void collectDataFromOutputFile(){
        for(int i = 0; i < guiJobs.size(); i++){
            InlineCssTextArea localArea = guiJobs.get(i);
            if(localArea.hashCode() == this.getInputSection().hashCode()){
                InlineCssTextArea nextTextArea = guiJobs.get(i + 1);
                nextTextArea.replaceText("");
                try{
                    FileReader reader = new FileReader(outputFile);
                    //Write all Text to the Next Text Area
                    while(reader.ready()){
                        nextTextArea.replaceText(nextTextArea.getText() + (char)reader.read());
                    }
                } catch(Exception exp){
                    edeInstance.appendIoText(errorPane, exp.toString());
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
