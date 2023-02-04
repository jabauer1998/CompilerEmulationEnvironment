package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GuiFlag extends VBox {
    private Label Name;
    private CheckBox BitSet;
    
    public GuiFlag(String Name){
        this.Name = new Label(Name);
        this.BitSet = new CheckBox();
        this.BitSet.selectedProperty().setValue(false);

        this.getChildren().addAll(this.Name, this.BitSet);
    }

    public void Set(boolean IsSet){
        this.BitSet.selectedProperty().setValue(IsSet);
    }
}