package edu.depauw.emulator_ide.gui.gui_job;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class GuiJobs extends VBox {
    private ScrollPane JobsPane;

    private double JobHeight;
    private double JobWidth;
    
    public GuiJobs(double Width, double Height){
        JobsPane = new ScrollPane();
        JobsPane.setPrefHeight(Height);
        JobsPane.setPrefWidth(Width);
        this.setPrefHeight(Height);
        this.setPrefWidth(Width);
        JobsPane.setContent(this);
        this.JobHeight = Height / 3;
        this.JobWidth = Width;
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public void AddJob(GuiJob Job){
        Job.setPrefWidth(JobWidth);
        Job.setPrefHeight(JobHeight);
        this.getChildren().addAll(Job);
    }

    public ScrollPane getJobsPane(){
        return JobsPane;
    }
}
