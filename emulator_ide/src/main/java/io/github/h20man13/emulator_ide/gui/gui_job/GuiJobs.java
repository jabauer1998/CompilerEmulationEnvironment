package io.github.H20man13.emulator_ide.gui.gui_job;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.gui.GuiEde;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class GuiJobs extends VBox {
    private ScrollPane JobsPane;

    private double JobHeight;
    private double JobWidth;

    private List<InlineCssTextArea> Jobs;
    
    public GuiJobs(double Width, double Height){
        JobsPane = new ScrollPane();
        JobsPane.setContent(this);

        this.JobHeight = Height / 3;
        this.JobWidth = Width;

        this.setAlignment(Pos.CENTER_LEFT);

        Jobs = new LinkedList<>();
    }

    public void AddExeJob(String JobName, String ExecString, String InputFile, String OutputFile, String ErrorFile, String errorTextBox,  String[] keywords, GuiEde edeInstance){
        ExeJob Job = new ExeJob(JobName, JobWidth, JobHeight, ExecString, InputFile, OutputFile, ErrorFile, errorTextBox, keywords, Jobs, edeInstance);
        this.getChildren().addAll(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddJavaJob(String JobName, Callable<Void> functionToRun, String InputFile, String OutputFile, String errorPane, String[] keywords, GuiEde edeInstance){
        JavaJob Job = new JavaJob(JobName, JobWidth, JobHeight, functionToRun, InputFile, OutputFile, errorPane, keywords, Jobs, edeInstance);
        this.getChildren().add(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddVerilogJob(String JobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, String[] keywords, GuiEde edeInstance){
        VerilogJob Job = new VerilogJob(JobName, JobWidth, JobHeight, verilogFile, inputFile, inputPane, outputPane, errorPane, keywords, edeInstance);
        this.getChildren().addAll(Job);
        Jobs.add(Job.getInputSection());
    }

    public ScrollPane getJobsPane(){
        return JobsPane;
    }
}
