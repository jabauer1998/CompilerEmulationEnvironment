package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GuiRegister extends HBox{
    private Label TitleReg; //Name of the Register
    private Label RegisterValue; //Hold some Current Value
    private int RegisterDecimalLength;
    private double Length;
    private double Height;

    public enum Format{
        HEXIDECIMAL,
        OCTAL,
        DECIMAL,
        BINARY
    }

    private Format Format;

    public GuiRegister(String Title, int Length, Format Format, double Width, double Height){
        TitleReg = new Label(Title);
        TitleReg.setPrefWidth(Width/3);
        TitleReg.setPrefHeight(Height);

        RegisterDecimalLength = Length;
        this.Format = Format;
        
        RegisterValue = new Label(GenZeros());
        RegisterValue.setPrefHeight(Height);
        RegisterValue.setPrefWidth(Width/2);

        this.getChildren().addAll(TitleReg, RegisterValue);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public String getTitle(){
        return TitleReg.getText();
    }

    private String GenZeros(){  
        StringBuilder Sb = new StringBuilder();
        for(int i = 0; i < RegisterDecimalLength; i++){
            Sb.append("0");
        }
        return Sb.toString();
    }

    public void SetRegisterValue(String Value){
        this.RegisterValue.setText(Value);
    }

    public String GetRegisterValue(){
        return this.RegisterValue.getText();
    }
}
