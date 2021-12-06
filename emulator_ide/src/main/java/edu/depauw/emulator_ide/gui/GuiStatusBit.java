package edu.depauw.emulator_ide.gui;


import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GuiStatusBit {

	private static HashMap<String, GuiStatusBit> statusBits = new HashMap<>();
	private static HBox                          parent;
	private VBox                                 container;
	private TextField                            data;
	private Label                                bitName;

	public GuiStatusBit(String name) {

		if (parent == null) {
			System.out.println("Error: the print line statement doesn not work");
			System.exit(1);
		}

		container = new VBox();
		container.setMaxHeight(parent.getMaxHeight());
		container.setMaxWidth(parent.getMaxWidth());
		container.setPrefHeight(parent.getMaxHeight());
		container.setPrefWidth(parent.getMaxWidth());

		bitName = new Label(name);
		bitName.setAlignment(Pos.CENTER);
		bitName.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));

		data = new TextField("0");
		data.setEditable(false);
		data.setAlignment(Pos.CENTER);

		if (statusBits.size()%2 == 0) {
			data.setStyle("-fx-border-color:black; -fx-background-color:#a3c2c2; -fx-text-color:black");
		} else {
			data.setStyle("-fx-border-color:black; -fx-background-color:white; -fx-text-color:black");
		}

		container.getChildren().addAll(bitName, data);

		if (statusBits.containsKey(name)) {
			System.out.print("Key allready exists");
			System.exit(1);
		}

		parent.getChildren().add(container);
		statusBits.put(name, this);

	}

	public static void setParent(HBox par){ parent = par; }

	public static void setStatus(String bit, long value){

		if (!statusBits.containsKey(bit)) {
			System.out.println("Error: program exited sooner than expected");
			System.exit(1);
		} else {
			statusBits.get(bit).data.setText(value != 0 ? "1" : "0");
		}

	}

	public static long getStatus(String bit){

		if (!statusBits.containsKey(bit)) {
			System.out.println("Error: program exited sooner than expected");
			System.exit(1);
			return -1;
		} else {
			return Long.parseLong(statusBits.get(bit).data.getText(), 2);
		}

	}

	public static void initialize(){ for (GuiStatusBit stat : statusBits.values()) { stat.data.setText("0"); } }
}
