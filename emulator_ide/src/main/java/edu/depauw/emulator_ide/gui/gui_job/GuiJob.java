package edu.depauw.emulator_ide.gui.gui_job;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class GuiJob extends VBox{
    private Button ExeButton;
    private TextArea InputSection;

    public GuiJob(String ButtonText){
        ExeButton = new Button(ButtonText);
        InputSection = new TextArea();
        this.getChildren().addAll(ExeButton, InputSection);
    }
}