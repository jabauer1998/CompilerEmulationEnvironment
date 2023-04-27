package io.github.H20man13.emulator_ide.gui.gui_machine;

import io.github.H20man13.emulator_ide._interface.Machine;
import io.github.H20man13.emulator_ide.gui.gui_machine.GuiRam.AddressFormat;
import io.github.H20man13.emulator_ide.gui.gui_machine.GuiRam.MemoryFormat;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiMachine extends HBox implements Machine{
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    private GuiIO Io;
    
    public GuiMachine(int NumberOfBytesInMemory, int NumberOfBytesInRow, AddressFormat AddrFormat, MemoryFormat MemFormat, double Width, double Height){
        this.RegFile = new GuiRegisterFile(Width / 3, Height);
        this.Mem = new GuiRam(NumberOfBytesInMemory, NumberOfBytesInRow, AddrFormat, MemFormat, Width / 3, Height);
        
        VBox FlagsAndIo = new VBox();
        this.Flags = new GuiFlags(Width/3, Height/7);
        this.Io = new GuiIO(Width/3, Height*6/7);

        FlagsAndIo.getChildren().addAll(Flags.getScrollPane(), this.Io.getTabPane());


        this.getChildren().addAll(this.RegFile.getScrollPane(), this.Mem.getScrollPane(), FlagsAndIo);
    }

    public void AddGuiRegister(String Title, int Length, GuiRegister.Format Format){
        this.RegFile.AddGuiRegister(Title, Length, Format);
    }

    public void AddGuiFlag(String Name){
        this.Flags.AddGuiFlag(Name);
    }

    public void AddIoSection(String TabTitle, String... PaneTitles){
        this.Io.AddIoSection(TabTitle, PaneTitles);
    }

    public void setMemoryValue(int Address, long dataValue){
        this.Mem.setMemoryValue(Address, dataValue);
    }

    public void setRegisterValue(String regName, long regValue){
        this.RegFile.setRegisterValue(regName, regValue);
    }

    public void setStatusValue(String statusName, long statusValue){
        this.Flags.setStatusValue(statusName, statusValue);
    }

    public long getRegisterValue(String regName){
        return this.RegFile.getRegisterValue(regName);
    }

    public long getRegisterValue(int RegNumber){
        return this.RegFile.getRegisterValue(RegNumber);
    }

    public long getMemoryValue(int address){
        return this.Mem.getMemoryValue(address);
    }

    public long getStatusValue(String statusName){
        return this.Flags.getStatusValue(statusName);
    }

    @Override
    public void setRegisterValue(int regNumber, long regValue){
        this.RegFile.setRegisterValue(regNumber, regValue);
    }

    public void appendIoText(String Name, String textToAppend){
        this.Io.appendIoText(Name, textToAppend);
    }

    public void writeIoText(String Name, String textToWrite){
        this.Io.writeIoText(Name, textToWrite);
    }

    public String readIoText(String Name){
        return this.Io.readIoText(Name);
    }

    public void clearStatusValues(){
        this.Flags.clearStatusValues();
    }

    public void clearMemory(){
        this.Mem.clearMemory();
    }

    public void clearRegisters(){
        this.RegFile.clearRegisters();
    }
}
