package io.github.h20man13.emulator_ide.gui.gui_machine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GuiFlag extends VBox {
    private Label Name;
    private Label BitSet;
    
    public GuiFlag(String Name, double Width, double Height){
        this.Name = new Label(Name);
        this.Name.setMaxWidth(Width);
        this.Name.setMaxHeight(Height/2);
        this.Name.setPrefWidth(Width);
        this.Name.setPrefHeight(Height/2);
        this.Name.setAlignment(Pos.CENTER);

        this.BitSet = new Label("0");
        this.BitSet.setMaxWidth(Width);
        this.BitSet.setMaxHeight(Height/2);
        this.BitSet.setMinWidth(Width);
        this.BitSet.setMinHeight(Height/2);
        this.BitSet.setPrefWidth(Width);
        this.BitSet.setPrefHeight(Height/2);
        this.BitSet.setAlignment(Pos.CENTER);

        this.getChildren().addAll(this.Name, this.BitSet);
        this.setAlignment(Pos.CENTER);

        this.setMaxHeight(Height);
        this.setMaxWidth(Width);
        this.setPrefWidth(Width);
        this.setPrefHeight(Height);
    }

    public void Set(boolean IsSet){
        if(IsSet){
            this.BitSet.setText("1");
        } else {
            this.BitSet.setText("0");
        }
    }

    public String getName(){
        return Name.getText();
    }

    public boolean isSet(){
        String Text = this.BitSet.getText();
        if(Text.equals("1")){
            return true;
        } else {
            return false;
        }
    }
}