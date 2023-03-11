package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public class GuiRegisterFile extends VBox {
    private ScrollPane Pane;

    private double RegisterWidth;
    private double RegisterHeight;
    
    public GuiRegisterFile(double Width, double Height){
        Pane = new ScrollPane();
        Pane.setMaxHeight(Height);
        Pane.setMaxWidth(Width * 2);
        Pane.setMinHeight(Height);
        Pane.setMinWidth(Width / 2);
        Pane.setPrefHeight(Height);
        Pane.setPrefWidth(Width);
        Pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        Pane.setContent(this);

        this.setMaxWidth(Width * 2 - 5);
        this.setMaxHeight(Height);
        this.setMinWidth(Width / 2 - 5);
        this.setMinHeight(Height);
        this.setPrefWidth(Width - 5);
        this.setPrefHeight(Height);

        this.RegisterWidth = Width;
        this.RegisterHeight = Height / 16;
    }

    public ScrollPane getScrollPane(){
        return Pane;
    }

    public void AddGuiRegister(GuiRegister Register){
        Register.setMaxWidth(RegisterWidth + 5);
        Register.setMaxHeight(RegisterHeight + 5);
        Register.setMinWidth(RegisterWidth - 5);
        Register.setMinHeight(RegisterHeight - 5);
        Register.setPrefWidth(RegisterWidth);
        Register.setPrefHeight(RegisterHeight);
        this.getChildren().add(Register);
    }
}
