package edu.depauw.emulator_ide.gui;

import edu.depauw.emulator_ide.gui.gui_job.GuiJob;
import edu.depauw.emulator_ide.gui.gui_job.GuiJobs;
import edu.depauw.emulator_ide.gui.gui_machine.GuiFlag;
import edu.depauw.emulator_ide.gui.gui_machine.GuiMachine;
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister;
import javafx.scene.layout.HBox;

public class GuiEde extends HBox{
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(int NumberOfBytes, int NumberOfBytesInRow, double Width, double Height){
        this.setPrefHeight(Height);
        this.setPrefWidth(Width);
        this.setMaxHeight(Height);
        this.setMaxWidth(Width);
        this.setMinHeight(Height);
        this.setMinWidth(Width / 2);
        
        this.Jobs = new GuiJobs(Width * (1/3), Height);
        this.Machine = new GuiMachine(NumberOfBytes, NumberOfBytesInRow, Width * (2/3), Height);

        this.getChildren().addAll(this.Jobs.getJobsPane(), this.Machine);
    }

    public void AddJob(GuiJob Job){
        this.Jobs.AddJob(Job);
    }

    public void AddFlag(GuiFlag Flag){
        this.Machine.AddGuiFlag(Flag);
    }

    public void AddRegister(GuiRegister Register){
        this.Machine.AddGuiRegister(Register);
    }
}