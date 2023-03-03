package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GuiRegister extends HBox{
    private Label TitleReg; //Name of the Register
    private Label RegisterValue; //Hold some Current Value
    private int RegisterDecimalLength;

    public enum Format{
        HEXIDECIMAL,
        OCTAL,
        DECIMAL,
        BINARY
    }

    private Format Format;

    public GuiRegister(String Title, int Length, Format Format){
        TitleReg = new Label(Title);
        RegisterDecimalLength = Length;
        this.Format = Format;
        RegisterValue = new Label(GenZeros());
        this.getChildren().addAll(TitleReg, RegisterValue);
        this.setAlignment(Pos.CENTER_LEFT);
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
}
