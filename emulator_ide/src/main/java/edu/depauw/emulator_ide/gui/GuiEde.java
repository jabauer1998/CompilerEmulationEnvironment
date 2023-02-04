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

    public GuiEde(int NumberOfBytes, int NumberOfBytesInRow){
        this.Jobs = new GuiJobs();
        this.Machine = new GuiMachine(NumberOfBytes, NumberOfBytesInRow);
        this.getChildren().addAll(Jobs, Machine);
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