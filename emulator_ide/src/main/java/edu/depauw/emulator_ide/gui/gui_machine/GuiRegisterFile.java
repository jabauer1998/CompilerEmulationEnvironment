package edu.depauw.emulator_ide.gui.gui_machine;

import java.util.HashMap;
import edu.depauw.emulator_ide._interface.RegFile;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public class GuiRegisterFile extends VBox implements RegFile {
    private ScrollPane Pane;

    private double RegisterWidth;
    private double RegisterHeight;

    private double actualWidth;
    private double actualHeight;

    private HashMap<String, GuiRegister> regFile;
    
    public GuiRegisterFile(double Width, double Height){
        actualWidth = Width;
        actualHeight = Height;
        
        Pane = new ScrollPane();
        Pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        Pane.setContent(this);

        this.RegisterWidth = Width;
        this.RegisterHeight = Height / 6;

        regFile = new HashMap<>();
    }

    public ScrollPane getScrollPane(){
        return Pane;
    }

    public void AddGuiRegister(String Title, int Length, GuiRegister.Format Format){
        GuiRegister Register = new GuiRegister(Title, Length, Format, RegisterWidth, RegisterHeight);
        
        regFile.put(Register.getTitle(), Register);

        this.getChildren().add(Register);
        resizeRegisterFile();
    }

    private void resizeRegisterFile(){
        this.setPrefWidth(actualWidth);
        this.setPrefHeight(actualHeight);

        Pane.setPrefHeight(actualHeight);
        Pane.setPrefWidth(actualWidth);
    }

    @Override
    public long getRegisterValue(String regName){
        GuiRegister Reg = regFile.get(regName);

        String value = Reg.GetRegisterValue();

        return Long.parseLong(value);
    }

    @Override
    public void setRegisterValue(String regName, long regValue){ 
       GuiRegister Reg = regFile.get(regName);
       String regValueString = Long.toBinaryString(regValue);
       Reg.SetRegisterValue(regValueString);
    }
}
