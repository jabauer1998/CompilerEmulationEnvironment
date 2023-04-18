package io.github.h20man13.emulator_ide.gui;

import io.github.h20man13.emulator_ide._interface.Machine;
import io.github.h20man13.emulator_ide.gui.gui_job.GuiJobs;
import io.github.h20man13.emulator_ide.gui.gui_machine.GuiMachine;
import io.github.h20man13.emulator_ide.gui.gui_machine.GuiRam;
import io.github.h20man13.emulator_ide.gui.gui_machine.GuiRegister;
import javafx.scene.layout.HBox;

public class GuiEde extends HBox implements Machine{
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(int NumberOfBytes, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat, double Width, double Height){
        this.Jobs = new GuiJobs(Width/3, Height);
        this.Machine = new GuiMachine(NumberOfBytes, NumberOfBytesInRow, AddrFormat, MemFormat, Width * 2 / 3, Height);

        this.getChildren().addAll(this.Jobs.getJobsPane(), this.Machine);
    }

    public void AddVerilogJob(String jobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane){
        this.Jobs.AddVerilogJob(jobName, verilogFile, inputFile, inputPane, outputPane, errorPane, this);
    }

    public void AddExeJob(String jobName, String execString, String inputFile, String outputFile, String errorFile, String errorPane){
        this.Jobs.AddExeJob(jobName, execString, inputFile, outputFile, errorFile, errorPane, this);
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

    public long getRegisterValue(int RegNumber){
        return this.Machine.getRegisterValue(RegNumber);
    }

    public long getMemoryValue(int memoryAddress){
        return this.Machine.getMemoryValue(memoryAddress);
    }

    public long getStatusValue(String statusName){
        return this.Machine.getStatusValue(statusName);
    }

    @Override
    public void setRegisterValue(int regNumber, long regValue){
        this.Machine.setRegisterValue(regNumber, regValue);
    }

    public void writeIoText(String textAreaName, String textToWrite){
        this.Machine.writeIoText(textAreaName, textToWrite);
    }

    public void appendIoText(String textAreaName, String textToAppend){
        this.Machine.appendIoText(textAreaName, textToAppend);
    }

    public String readIoText(String textAreaName){
        return this.Machine.readIoText(textAreaName);
    }
}