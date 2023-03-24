package edu.depauw.emulator_ide.gui.gui_machine;

import edu.depauw.emulator_ide._interface.Machine;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiMachine extends HBox implements Machine{
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    private GuiIO Io;
    
    public GuiMachine(int NumberOfBytesInMemory, int NumberOfBytesInRow, double Width, double Height){
        this.RegFile = new GuiRegisterFile(Width / 3, Height);
        this.Mem = new GuiRam(NumberOfBytesInMemory, NumberOfBytesInRow, Width / 3, Height);
        
        VBox FlagsAndIo = new VBox();
        this.Flags = new GuiFlags(Width/3, Height/2);
        this.Io = new GuiIO(Width/3, Height/2);

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

    public long getMemoryValue(int address){
        return this.Mem.getMemoryValue(address);
    }

    public long getStatusValue(String statusName){
        return this.Flags.getStatusValue(statusName);
    }

}
