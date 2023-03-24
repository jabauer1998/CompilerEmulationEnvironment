package edu.depauw.emulator_ide.gui;

import edu.depauw.emulator_ide._interface.Machine;
import edu.depauw.emulator_ide.gui.gui_job.GuiJob;
import edu.depauw.emulator_ide.gui.gui_job.GuiJobs;
import edu.depauw.emulator_ide.gui.gui_machine.GuiFlag;
import edu.depauw.emulator_ide.gui.gui_machine.GuiMachine;
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister;
import javafx.scene.layout.HBox;

public class GuiEde extends HBox implements Machine{
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(int NumberOfBytes, int NumberOfBytesInRow, double Width, double Height){
        this.Jobs = new GuiJobs(Width/3, Height);
        this.Machine = new GuiMachine(NumberOfBytes, NumberOfBytesInRow, Width * 2 / 3, Height);

        this.getChildren().addAll(this.Jobs.getJobsPane(), this.Machine);
    }

    public void AddVerilogJob(String JobName, String VerilogFile, String ErrorFile){
        this.Jobs.AddVerilogJob(JobName, VerilogFile, this, ErrorFile);
    }

    public void AddExeJob(String JobName, String ExecString, String InputFile, String OutputFile, String ErrorFile){
        this.Jobs.AddExeJob(JobName, ExecString, InputFile, OutputFile, ErrorFile);
    }

    public void AddFlag(String Name){
        this.Machine.AddGuiFlag(Name);
    }

    public void AddIoSection(String TabTitle, String... PaneTitles){
        this.Machine.AddIoSection(TabTitle, PaneTitles);
    }

    public void AddRegister(String Title, int Length, GuiRegister.Format Format){
        this.Machine.AddGuiRegister(Title, Length, Format);
    }

    public void setRegisterValue(String registerName, long registerValue){
        this.Machine.setRegisterValue(registerName, registerValue);
    }

    public void setMemoryValue(int memoryAddress, long registerValue){
        this.Machine.setMemoryValue(memoryAddress, registerValue);
    }

    public void setStatusValue(String statusName, long registerName){
        this.Machine.setStatusValue(statusName, registerName);
    }

    public long getRegisterValue(String regName){
        return this.Machine.getRegisterValue(regName);
    }

    public long getMemoryValue(int memoryAddress){
        return this.Machine.getMemoryValue(memoryAddress);
    }

    public long getStatusValue(String statusName){
        return this.Machine.getStatusValue(statusName);
    }
}