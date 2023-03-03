package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GuiFlag extends VBox {
    private Label Name;
    private CheckBox BitSet;
    
    public GuiFlag(String Name){
        this.Name = new Label(Name);
        this.BitSet = new CheckBox();
        this.BitSet.selectedProperty().setValue(false);
        this.BitSet.setDisable(true);
        this.BitSet.setOpacity(100);
        this.getChildren().addAll(this.Name, this.BitSet);
        this.setAlignment(Pos.CENTER);
    }

    public void Set(boolean IsSet){
        this.BitSet.selectedProperty().setValue(IsSet);
    }
}