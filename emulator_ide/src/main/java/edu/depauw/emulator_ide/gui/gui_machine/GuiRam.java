package edu.depauw.emulator_ide.gui.gui_machine;

import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiRam extends VBox {
    private Label[] Bytes;
    private Label[] Addresses;
    
    private int NumberOfBytes;
    private int NumberRowsRounded;
    private int BytesPerRow;

    enum AddressFormat{
        BINARY,
        HEXIDECIMAL,
        OCTAL,
        DECIMAL
    }

    public GuiRam(int NumberOfBytes, int BytesPerRow){
        this.BytesPerRow = BytesPerRow;
        this.NumberOfBytes = NumberOfBytes;
        this.NumberRowsRounded = (int)Math.ceil((NumberOfBytes / BytesPerRow));
        Bytes = new Label[this.NumberOfBytes];
        Addresses = new Label[NumberRowsRounded];
        int Byte = 0;
        
        for(int Row = 0; Row < this.NumberRowsRounded; Row++){
            //The Following HBoxes are Used to Organize the Memory Content that is Generated
            //They are also stored into arrays to make the Labels Addressable when we need to modify a specific Label
            HBox AddressToMemory = new HBox();
            HBox RowOfMemory = new HBox();
            Addresses[Row] = new Label(Integer.toString(Byte));
            for(int i = 0; i < this.BytesPerRow; i++, Byte++){
                Bytes[Byte] = new Label("00");
                RowOfMemory.getChildren().add(Bytes[Byte]);
            }
            AddressToMemory.getChildren().addAll(Addresses[Row], RowOfMemory);
            this.getChildren().add(AddressToMemory);
        }
    }

    public void SetMemory(int Address, String Data){
        this.Bytes[Address].setText(Data);
    }

    public void LoadProgram(String Program){
        Program = Program.trim();
        int Length = Program.length();
        List<String> Results = new LinkedList<String>(); 
        for (int i = 0; i < Length; i += 8) {
            Results.add(Program.substring(i, Math.min(Length, i + 8)));
        }

        for(int i = 0; i < Results.size() && i < NumberOfBytes; i++){
            String First = Results.remove(0);
            Bytes[i].setText(First);
        }
    }
}
