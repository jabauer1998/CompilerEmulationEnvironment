package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.layout.HBox;

public class GuiMachine extends HBox{
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    
    public GuiMachine(int NumberOfBytesInMemory, int NumberOfBytesInRow){
        RegFile = new GuiRegisterFile();
        Mem = new GuiRam(NumberOfBytesInMemory, NumberOfBytesInRow);
        this.Flags = new GuiFlags();
    }

    public void AddGuiRegister(GuiRegister Register){
        RegFile.AddGuiRegister(Register);
    }

    public void SetMemory(int Address, String Data){
        Mem.SetMemory(Address, Data);
    }

    public void AddGuiFlag(GuiFlag Flag){
        this.Flags.AddGuiFlag(Flag);
    }
}
