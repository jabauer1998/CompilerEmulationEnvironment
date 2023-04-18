package io.github.h20man13.emulator_ide.gui.gui_machine;

import java.util.LinkedList;
import java.util.List;
import io.github.h20man13.emulator_ide._interface.Memory;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class GuiRam extends VBox implements Memory{
    private Label[] Bytes;
    private Label[] Addresses;
    
    private int NumberOfBytes;
    private int NumberRowsRounded;
    private int BytesPerRow;

    private ScrollPane Pane;
    private AddressFormat AddrFormat;
    private MemoryFormat MemFormat;

    public enum AddressFormat{
        BINARY,
        HEXIDECIMAL,
        OCTAL,
        DECIMAL
    }

    public enum MemoryFormat{
        BINARY,
        HEXADECIMAL,
    }

    public GuiRam(int NumberOfBytes, int BytesPerRow, AddressFormat AddrFormat, MemoryFormat MemFormat, double Width, double Height){
        this.BytesPerRow = BytesPerRow;
        this.NumberOfBytes = NumberOfBytes;
        this.NumberRowsRounded = (int)Math.ceil((NumberOfBytes / BytesPerRow));

        this.MemFormat = MemFormat;
        this.AddrFormat = AddrFormat;

        this.setMaxHeight(Height);
        this.setMaxWidth(Width);
        this.setPrefWidth(Width);
        this.setPrefHeight(Height);
        
        this.Pane = new ScrollPane();
        this.Pane.setMaxHeight(Height);
        this.Pane.setMaxWidth(Width);
        this.Pane.setPrefHeight(Height);
        this.Pane.setPrefWidth(Width);
        
        this.Pane.setContent(this);
        this.Pane.setHbarPolicy(ScrollBarPolicy.NEVER);

        Bytes = new Label[this.NumberOfBytes];
        Addresses = new Label[NumberRowsRounded];
        int Byte = 0;
        
        for(int Row = 0; Row < this.NumberRowsRounded; Row++){
            //The Following HBoxes are Used to Organize the Memory Content that is Generated
            //They are also stored into arrays to make the Labels Addressable when we need to modify a specific Label
            HBox AddressToMemory = new HBox();
            HBox RowOfMemory = new HBox();

            if(this.AddrFormat == AddressFormat.HEXIDECIMAL){
                Addresses[Row] = new Label(Integer.toHexString(Byte));
            } else if(this.AddrFormat == AddressFormat.BINARY){
                Addresses[Row] = new Label(Integer.toBinaryString(Byte));
            } else if(this.AddrFormat == AddressFormat.OCTAL){
                Addresses[Row] = new Label(Integer.toOctalString(Byte));
            } else {
                Addresses[Row] = new Label(Integer.toString(Byte));
            }
            
            for(int i = 0; i < this.BytesPerRow; i++, Byte++){

                if(this.MemFormat == MemoryFormat.HEXADECIMAL){
                    Bytes[Byte] = new Label("00");
                    Bytes[Byte].setPrefWidth(Width/(this.BytesPerRow * 2));
                    Bytes[Byte].setTextAlignment(TextAlignment.RIGHT);
                } else {
                    Bytes[Byte] = new Label("00000000");
                    Bytes[Byte].setPrefWidth(Width/(this.BytesPerRow * 3));
                    Bytes[Byte].setTextAlignment(TextAlignment.RIGHT);
                }
                RowOfMemory.getChildren().add(Bytes[Byte]);
                RowOfMemory.setAlignment(Pos.CENTER_RIGHT);
            }
            Addresses[Row].setTextAlignment(TextAlignment.LEFT);
            Addresses[Row].setPrefWidth(Width/3);
            AddressToMemory.getChildren().addAll(Addresses[Row], RowOfMemory);
            AddressToMemory.setPrefWidth(Width);
            AddressToMemory.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().add(AddressToMemory);
        }
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

    public ScrollPane getScrollPane(){
        return Pane;
    }

    @Override
    public void setMemoryValue(int address, long dataValue){
        Label Byte = Bytes[address];
        if(MemFormat == MemoryFormat.BINARY){
            String asString = Long.toBinaryString(dataValue);
            if(asString.length() > 8){
                asString = asString.substring(asString.length() - 8);
            } else if(asString.length() < 8){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 8 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        } else {
            String asString = Long.toHexString(dataValue);
            if(asString.length() > 2){
                asString = asString.substring(asString.length() - 2);
            } else if(asString.length() < 2){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 2 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        }
    }

    @Override
    public long getMemoryValue(int address){ // TODO Auto-generated method stub
        Label Byte = Bytes[address];
        String text = Byte.getText();
        
        if(MemFormat == MemoryFormat.BINARY){
            return Long.parseLong(text, 2);
        } else {
            return Long.parseLong(text, 16);
        }
    }
}
