package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.layout.HBox;

public class GuiMachine extends HBox{
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    
    public GuiMachine(int NumberOfBytesInMemory, int NumberOfBytesInRow, double Width, double Height){
        this.setPrefWidth(Width);
        this.setPrefHeight(Height);
        this.setMaxWidth(Width * 3 / 2);
        this.setMaxHeight(Height);
        this.setMinWidth(Width / 2);
        this.setMinHeight(Height);

        this.RegFile = new GuiRegisterFile(Width / 4, Height);
        this.Mem = new GuiRam(NumberOfBytesInMemory, NumberOfBytesInRow, Width / 4, Height);
        this.Flags = new GuiFlags(Width / 2, Height / 8);

        this.getChildren().addAll(this.RegFile.getScrollPane(), this.Mem.getScrollPane(), this.Flags.getScrollPane());
    }

    public void AddGuiRegister(GuiRegister Register){
        this.RegFile.AddGuiRegister(Register);
    }

    public void SetMemory(int Address, String Data){
        this.Mem.SetMemory(Address, Data);
    }

    public void AddGuiFlag(GuiFlag Flag){
        this.Flags.AddGuiFlag(Flag);
    }
}
