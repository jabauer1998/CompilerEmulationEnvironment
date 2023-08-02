package io.github.H20man13.emulator_ide.gui.gui_job;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.gui.GuiEde;
import io.github.H20man13.emulator_ide.gui.gui_job.GuiJob.TextAreaType;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GuiJobs extends VBox {
    private ScrollPane JobsPane;

    private double JobHeight;
    private double JobWidth;

    private List<Region> Jobs;
    
    public GuiJobs(double Width, double Height){
        JobsPane = new ScrollPane();
        JobsPane.setContent(this);

        this.JobHeight = Height / 3;
        this.JobWidth = Width;

        this.setAlignment(Pos.CENTER_LEFT);

        Jobs = new LinkedList<>();
    }

    public void AddExeJob(String JobName, TextAreaType type, String ExecString, String InputFile, String OutputFile, String ErrorFile, String errorTextBox, GuiEde edeInstance, String... keywords){
        ExeJob Job = new ExeJob(JobName, type, JobWidth, JobHeight, ExecString, InputFile, OutputFile, ErrorFile, errorTextBox, Jobs, edeInstance, keywords);
        this.getChildren().addAll(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddJavaJob(String JobName, TextAreaType type, Callable<Void> functionToRun, String InputFile, String OutputFile, String errorPane, GuiEde edeInstance, String... keywords){
        JavaJob Job = new JavaJob(JobName, type, JobWidth, JobHeight, functionToRun, InputFile, OutputFile, errorPane, keywords, Jobs, edeInstance);
        this.getChildren().add(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddVerilogJob(String JobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde edeInstance){
        VerilogJob Job = new VerilogJob(JobName, JobWidth, JobHeight, verilogFile, inputFile, inputPane, outputPane, errorPane, edeInstance);
        this.getChildren().addAll(Job);
        Jobs.add(Job.getInputSection());
    }

    public ScrollPane getJobsPane(){
        return JobsPane;
    }
}
