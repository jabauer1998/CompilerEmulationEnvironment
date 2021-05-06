package edu.depauw.emulator_ide.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

    
public class Main extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
    	Screen screen = Screen.getPrimary();
    	Rectangle2D bounds = screen.getVisualBounds();
    	
    	stage.setX(bounds.getMinX());
    	stage.setY(bounds.getMinY());

    	stage.setMaxWidth(bounds.getWidth());
    	stage.setMaxHeight(bounds.getHeight());
    	stage.setWidth(bounds.getWidth());
    	stage.setHeight(bounds.getHeight());
    	
    	stage.setTitle("Emulator Development Environment");
        
        HBox halves = new HBox(); //Width
        halves.setMaxWidth(stage.getWidth());
        halves.setMaxHeight(stage.getHeight());

        
        VBox leftSide = new VBox(); //left side of horizontal alignment
        leftSide.setMaxWidth(halves.getMaxWidth() / 3);
        leftSide.setMaxHeight(halves.getMaxHeight());
        leftSide.setPrefWidth(halves.getMaxWidth() / 3);
        leftSide.setPrefHeight(halves.getMaxHeight());

        Button assemble = new Button("Assemble");
        assemble.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        TextArea assemblerInput = new TextArea();
        assemblerInput.setMaxWidth(leftSide.getMaxWidth());
		assemblerInput.setMaxHeight(leftSide.getMaxHeight());
	    assemblerInput.setPrefWidth(leftSide.getMaxWidth());
		assemblerInput.setPrefHeight(leftSide.getMaxHeight());
	
        Button execute = new Button("Execute");
        execute.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        TextArea binaryInput = new TextArea();
        binaryInput.setMaxWidth(leftSide.getMaxWidth());
		binaryInput.setMaxHeight(leftSide.getMaxHeight());
		binaryInput.setPrefWidth(leftSide.getMaxWidth());
		binaryInput.setPrefHeight(leftSide.getMaxHeight());
        
        leftSide.getChildren().addAll(assemble, assemblerInput, execute, binaryInput);
        
        VBox middleLeftSide = new VBox();
        Label registerFile = new Label("Register File");
        registerFile.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        registerFile.setAlignment(Pos.CENTER);
        
        middleLeftSide.getChildren().add(registerFile);
        
        middleLeftSide.setMaxWidth(halves.getMaxWidth() / 6);
		middleLeftSide.setMaxHeight(halves.getMaxHeight());
		middleLeftSide.setPrefWidth(halves.getMaxWidth() / 6);
		middleLeftSide.setPrefHeight(halves.getMaxHeight());
		
		GuiRegister.setParent(middleLeftSide);
		GuiRegister.setRegisterSize(32);
		
		GuiRegister CSPR = new GuiRegister("CSPR");
		GuiRegister R0 = new GuiRegister("R0");
		GuiRegister R1 = new GuiRegister("R1");
		GuiRegister R2 = new GuiRegister("R2");
		GuiRegister R3 = new GuiRegister("R3");
		GuiRegister R4 = new GuiRegister("R4");
		GuiRegister R5 = new GuiRegister("R5");
		GuiRegister R6 = new GuiRegister("R6");
		GuiRegister R7 = new GuiRegister("R7");
		GuiRegister R8 = new GuiRegister("R8");
		GuiRegister R9 = new GuiRegister("R9");
		GuiRegister R10 = new GuiRegister("R10");
		GuiRegister R11 = new GuiRegister("R11");
		GuiRegister R12 = new GuiRegister("R12");
		GuiRegister R13 = new GuiRegister("R13");
		GuiRegister R14 = new GuiRegister("R14");
		GuiRegister R15 = new GuiRegister("R15");
		
        VBox middleRightSide = new VBox();
        middleRightSide.setMaxWidth(halves.getMaxWidth() / 6);
		middleRightSide.setMaxHeight(halves.getMaxHeight());
		middleRightSide.setPrefWidth(halves.getMaxWidth() / 6);
		middleRightSide.setPrefHeight(halves.getMaxHeight());
        
        Label memDumpTitle = new Label("Memory Dump");
        memDumpTitle.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        memDumpTitle.setAlignment(Pos.CENTER);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxWidth(middleRightSide.getMaxWidth());
		scrollPane.setMaxHeight(middleRightSide.getMaxHeight());
		scrollPane.setPrefWidth(middleRightSide.getMaxWidth());
		scrollPane.setPrefHeight(middleRightSide.getMaxHeight());
        
		
		VBox memoryDump = new VBox();
		memoryDump.setMaxWidth(scrollPane.getMaxWidth());
		memoryDump.setMaxHeight(scrollPane.getMaxHeight());
		memoryDump.setPrefWidth(scrollPane.getMaxWidth());
		memoryDump.setPrefHeight(scrollPane.getMaxHeight());
		
		
		int MEMSIZE = 10000;
		GuiMemory.setParent(memoryDump);
		GuiMemory.setMemSize(MEMSIZE);
        
        for(int i = 0; i < MEMSIZE; i++) {
        	new GuiMemory(i); //creates a byte of memory to display
        }
        
        scrollPane.setContent(memoryDump);

        middleRightSide.getChildren().addAll(memDumpTitle, scrollPane);
        
        VBox rightSide = new VBox();
        rightSide.setMaxWidth(halves.getMaxWidth() / 3);
        rightSide.setMaxHeight(halves.getMaxHeight());
        rightSide.setPrefWidth(halves.getMaxWidth() / 3);
        rightSide.setPrefHeight(halves.getMaxHeight());
        
        Label status = new Label("Status Bits");
        status.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        status.setAlignment(Pos.CENTER);
        
        HBox parentStatus = new HBox();
        parentStatus.setMaxWidth(rightSide.getMaxWidth());
        parentStatus.setMaxHeight(rightSide.getMaxHeight() / 2);
        parentStatus.setPrefWidth(rightSide.getMaxWidth());
        parentStatus.setPrefHeight(rightSide.getMaxHeight() / 2);
        
        GuiStatusBit.setParent(parentStatus);
        
        GuiStatusBit Z = new GuiStatusBit("Z");
        GuiStatusBit C = new GuiStatusBit("C");
        GuiStatusBit N = new GuiStatusBit("N");
        GuiStatusBit V = new GuiStatusBit("V");
        
        Label stdout = new Label("Standard Output");
        stdout.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        stdout.setAlignment(Pos.CENTER);
        
        TextArea standardOutput = new TextArea();
        standardOutput.setMaxWidth(rightSide.getMaxWidth());
        standardOutput.setMaxHeight(rightSide.getMaxHeight());
        standardOutput.setPrefWidth(rightSide.getMaxWidth());
		standardOutput.setPrefHeight(rightSide.getMaxHeight());
		standardOutput.setEditable(false);
		
		Label stdin = new Label("Standard Input");
        stdin.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        stdin.setAlignment(Pos.CENTER);
        
        TextArea standardInput = new TextArea();
        standardInput.setMaxWidth(rightSide.getMaxWidth());
        standardInput.setMaxHeight(rightSide.getMaxHeight());
        standardInput.setPrefWidth(rightSide.getMaxWidth());
		standardInput.setPrefHeight(rightSide.getMaxHeight());
        
	
		rightSide.getChildren().addAll(status, parentStatus, stdout, standardOutput, stdin, standardInput);
		
	    halves.getChildren().addAll(leftSide, middleLeftSide, middleRightSide, rightSide);
		
		Scene scene = new Scene(halves);
		stage.setScene(scene);	
		stage.show();
	}
}
