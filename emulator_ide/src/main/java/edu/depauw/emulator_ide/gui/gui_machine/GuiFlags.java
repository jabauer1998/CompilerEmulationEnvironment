package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

public class GuiFlags extends HBox {
    private ScrollPane FlagPane;
    
    public GuiFlags(){
        FlagPane = new ScrollPane();
        FlagPane.setContent(this);
    }

    public void AddGuiFlag(GuiFlag Flag){
        this.getChildren().addAll(Flag);
    }
}
