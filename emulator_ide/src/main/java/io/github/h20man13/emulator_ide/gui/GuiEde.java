package io.github.H20man13.emulator_ide.gui;

import java.util.concurrent.Callable;
import io.github.H20man13.emulator_ide._interface.Machine;
import io.github.H20man13.emulator_ide.gui.gui_job.GuiJobs;
import io.github.H20man13.emulator_ide.gui.gui_job.GuiJob.TextAreaType;
import io.github.H20man13.emulator_ide.gui.gui_machine.GuiMachine;
import io.github.H20man13.emulator_ide.gui.gui_machine.GuiRam;
import io.github.H20man13.emulator_ide.gui.gui_machine.GuiRegister;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiEde extends VBox implements Machine{
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(int NumberOfBytes, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat, double Width, double Height){
        HBox toolBar = new HBox();
        Button clearMemory = new Button("Clear Memory");
        clearMemory.setPrefHeight(Height/12);
        clearMemory.setPrefWidth(Width/3);
        clearMemory.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event){ // TODO Auto-generated method stub
                Machine.clearMemory();
            }
        });
        
        Button clearRegisters = new Button("Clear Registers");
        clearRegisters.setPrefHeight(Height/12);
        clearRegisters.setPrefWidth(Width/3);
        clearRegisters.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event){
                Machine.clearRegisters();
            }
        });

        Button clearStatus = new Button("Clear Status");
        clearStatus.setPrefHeight(Height/12);
        clearStatus.setPrefWidth(Width/3);
        clearStatus.setOnMouseClicked(new EventHandler<Event>(){
            @Override
            public void handle(Event event){
                Machine.clearStatusValues();
            }
        });

        

        toolBar.getChildren().addAll(clearRegisters, clearMemory, clearStatus);

        HBox mainPane = new HBox();
        this.Jobs = new GuiJobs(Width/3, Height*11/12);
        this.Machine = new GuiMachine(NumberOfBytes, NumberOfBytesInRow, AddrFormat, MemFormat, Width * 2 / 3, Height * 11 / 12);
        
        mainPane.getChildren().addAll(this.Jobs.getJobsPane(), this.Machine);
        
        this.getChildren().addAll(toolBar, mainPane);
    }

    public void AddVerilogJob(String jobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane){
        this.Jobs.AddVerilogJob(jobName, verilogFile, inputFile, inputPane, outputPane, errorPane, this);
    }

    public void AddExeJob(String jobName, TextAreaType type, String execString, String inputFile, String outputFile, String errorFile, String errorPane, String... keywords){
        this.Jobs.AddExeJob(jobName, type, execString, inputFile, outputFile, errorFile, errorPane, this, keywords);
    }

    public void AddJavaJob(String jobName, TextAreaType type, Callable<Void> functionToRun, String inputFile, String outputFile, String errorPane, String... keywords){
        this.Jobs.AddJavaJob(jobName, type, functionToRun, inputFile, outputFile, errorPane, this, keywords);
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