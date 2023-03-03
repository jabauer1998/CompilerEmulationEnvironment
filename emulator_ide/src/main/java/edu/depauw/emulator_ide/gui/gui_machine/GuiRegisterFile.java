package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public class GuiRegisterFile extends VBox {
    private ScrollPane Pane;
    private VBox InnerVBox;

    private double RegisterWidth;
    private double RegisterHeight;
    
    public GuiRegisterFile(double Width, double Height){
        this.setPrefWidth(Width);
        this.setPrefHeight(Height);

        Pane = new ScrollPane();
        Pane.setPrefHeight(Height);
        Pane.setPrefWidth(Width);
        Pane.setHbarPolicy(ScrollBarPolicy.NEVER);

        InnerVBox = new VBox();
        InnerVBox.setPrefHeight(Height);
        InnerVBox.setPrefWidth(Width);
        
        super.getChildren().addAll(Pane);
        Pane.setContent(InnerVBox);

        this.RegisterWidth = Width;
        this.RegisterHeight = Height / 16;
    }

    public void AddGuiRegister(GuiRegister Register){
        Register.setPrefWidth(RegisterWidth);
        Register.setPrefHeight(RegisterHeight);
        InnerVBox.getChildren().add(Register);
    }
}
