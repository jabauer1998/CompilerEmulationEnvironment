package edu.depauw.emulator_ide.gui;


import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.lang.StringBuilder;

public class GuiRegister {

	private static HashMap<Integer, GuiRegister> memFile = new HashMap<>();
	private static int                           registerSize;
	private static VBox                          parent;

	private HBox      container;
	private TextField data;
	private Label     registerName;

	public GuiRegister(String identifier, int number) {

		if (parent == null) {
			System.out.println("Error: the parent is a null object");
			System.exit(1);
		}

		container = new HBox();
		container.setMaxWidth(parent.getMaxWidth());
		container.setMaxHeight(parent.getMaxHeight());
		container.setPrefWidth(parent.getMaxWidth());
		container.setPrefHeight(parent.getMaxHeight());

		registerName = new Label(identifier);
		registerName.setMinWidth(container.getMaxWidth()/8);
		registerName.setMaxHeight(container.getMaxHeight());
		registerName.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));

		data = new TextField("");
		data.setMaxWidth(container.getMaxWidth());
		data.setMaxHeight(container.getMaxHeight());
		data.setPrefWidth(container.getMaxWidth());
		data.setPrefHeight(container.getMaxHeight());

		if (memFile.size()%2 == 0) {
			data.setStyle("-fx-border-color:black; -fx-background-color:#a3c2c2; -fx-text-color:black");
		} else {
			data.setStyle("-fx-border-color:black; -fx-background-color:white; -fx-text-color:black");
		}

		data.setAlignment(Pos.CENTER);
		data.setEditable(false);

		if (registerSize <= 0) {
			System.out.println("Error: Register size must be defined but found NULL");
			System.exit(1);
		}

		for (int i = 0; i < registerSize; i++) { data.appendText("0"); }

		if (memFile.containsKey(number)) {
			System.out.println("Error: the memory file allready contains Register " + identifier);
			System.exit(1);
		}

		container.getChildren().addAll(registerName, data);

		parent.getChildren().add(container);

		memFile.put(number, this);

	}

	public GuiRegister(int number) { this("R" + number, number); }

	private long getValue(){ return Long.parseLong(data.getText(), 2); }

	private void setValue(long newData){
		StringBuilder sb = new StringBuilder(Long.toBinaryString(newData));

		if (sb.length() < registerSize) {

			while(sb.length() < registerSize) { sb.insert(0, '0'); }

			data.setText(sb.toString());
		} else if (sb.length() > registerSize) {
			data.setText(sb.substring(sb.length() - registerSize));
		} else {
			data.setText(sb.toString());
		}

	}

	public static void setRegisterSize(int value){ registerSize = value; }

	public static void setParent(VBox node){ parent = node; }

	public static long getRegister(long register){ return memFile.get((int)register).getValue(); }

	public static void setRegister(long register, long value){ memFile.get((int)register).setValue(value); }

	private static String genZeros(int number){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < number; i++) { sb.append('0'); }

		return sb.toString();
	}

	public static void initialize(){

		for (GuiRegister reg : memFile.values()) {

			for (int i = 0; i < registerSize; i++) { reg.data.setText(genZeros(registerSize)); }

		}

	}
}
