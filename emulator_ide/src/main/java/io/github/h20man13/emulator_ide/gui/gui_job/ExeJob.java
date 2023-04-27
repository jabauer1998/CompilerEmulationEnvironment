package io.github.H20man13.emulator_ide.gui.gui_job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.gui.GuiEde;

public class ExeJob extends GuiJob {
    private String ExeString;
    private String InputFile;
    private String OutputFile;
    private String ErrorFile;
    private List<InlineCssTextArea> guiJobs;
    private String errorTextAreaName;

    private GuiEde edeInstance;

    public ExeJob(String ButtonText, double Width, double Height, String ExeString, String InputFile, String OutputFile, String ErrorFile, String errorTextAreaName, String[] keywords, List<InlineCssTextArea> guiJobs, GuiEde edeInstance) { 
        super(ButtonText, Width, Height, keywords);
        this.ExeString = ExeString;
        this.InputFile = InputFile;
        this.OutputFile = OutputFile;
        this.ErrorFile = ErrorFile;
        this.guiJobs = guiJobs;
        this.edeInstance = edeInstance;
        this.errorTextAreaName = errorTextAreaName;
    }

    public void RunJob(){
        CreateFiles();
        RunCommand();
        CopyOverOutputData();
        CollectErrorData();
    }

    private void RunCommand(){
        try {
            Runtime.getRuntime().exec(ExeString);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void CreateFiles(){
        CreateInputFile();
    }

    private void CreateInputFile(){
        File File = new File(InputFile);
        if(File.exists()){
            File.delete();
        }
        CopyOverInputData();
    }

    private void CopyOverInputData(){
        File File = new File(InputFile);
        try {
            File.createNewFile();
            FileWriter Writer = new FileWriter(InputFile);
            Writer.write(this.getInputSection().getText());
            Writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void CopyOverOutputData(){
        int i;
        for(i = 0; i < guiJobs.size(); i++){
            if(this.getInputSection().hashCode() == guiJobs.get(i).hashCode()){
                break;
            }
        }

        InlineCssTextArea OutputTextArea = guiJobs.get(i + 1);
        File OutputFilePtr = new File(OutputFile);

        if(OutputFilePtr.exists()){
            try {
                StringBuilder memText = new StringBuilder();
                FileReader outputReader = new FileReader(OutputFilePtr);
                while(true){
                    int outputCharFull = (char)outputReader.read();
                    if(outputCharFull == -1)
                        break;
                    memText.append((char)outputCharFull);
                }
                OutputTextArea.replaceText(memText.toString());
                outputReader.close();
            } catch (FileNotFoundException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            } catch (IOException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            }
        }
    }

    private void CollectErrorData(){
        File errorFilePtr = new File(ErrorFile);
        if(errorFilePtr.exists()){
            //If it exists we need to collect the Error Data
            try {
                FileReader fReader = new FileReader(errorFilePtr);
                StringBuilder memText = new StringBuilder();
                
                while(true){
                    int outputCharFull = fReader.read();
                     if(outputCharFull == -1){
                        break;
                     }
                     memText.append((char)outputCharFull);
                }

                edeInstance.appendIoText(errorTextAreaName, memText.toString());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            }

        }
    }
}
