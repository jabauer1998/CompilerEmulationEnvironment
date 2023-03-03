package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;

public class GuiFlags extends HBox {
    private ScrollPane FlagPane;
    
    public GuiFlags(double Width, double Height){
        FlagPane = new ScrollPane();
        FlagPane.setContent(this);
        FlagPane.setPrefWidth(Width);
        FlagPane.setPrefHeight(Height);
        FlagPane.setVbarPolicy(ScrollBarPolicy.NEVER);
    }

    public void AddGuiFlag(GuiFlag Flag){
        this.getChildren().addAll(Flag);
    }

    public ScrollPane getScrollPane(){
        return FlagPane;
    }
}
