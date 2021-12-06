package edu.depauw.emulator_ide.gui;


import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GuiMemory {

	private static VBox        parent;
	private static GuiMemory[] mem;
	private static int         index  = 0;
	private static int         length = 0;
	private HBox               container;
	private Text               data;
	private Label              address;

	public GuiMemory(int address) {

		if (parent == null) {
			System.out.println("Error: the memory parent is a null object");
			System.exit(1);
		}

		if (length <= 0) {
			System.out.println("Error: you must set the desired length of memory per address(default is 8)");
			System.exit(1);
		}

		container = new HBox();
		container.setMaxWidth(parent.getMaxWidth());
		container.setMaxHeight(parent.getMaxHeight());
		container.setPrefWidth(parent.getMaxWidth());
		container.setPrefHeight(parent.getMaxHeight());

		this.address = new Label("0x" + Integer.toHexString(address));
		this.address.setMinWidth(container.getMaxWidth()/2);
		this.address.setMaxHeight(container.getMaxHeight());

		data = new Text(genZeros(length));
		data.maxWidth(container.getMaxWidth());
		data.maxHeight(container.getMaxHeight());
		data.prefWidth(container.getMaxWidth());
		data.prefHeight(container.getMaxHeight());
		data.setTextAlignment(TextAlignment.CENTER);

		container.getChildren().addAll(this.address, data);

		parent.getChildren().add(container);

		if (index >= mem.length) {
			System.out.println("Error: the index that was defined was not usefull");
			System.out.println();
		}

		mem[index] = this;
		index++;
	}

	private static String genZeros(int num){
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < num; i++) { str.append('0'); }

		return str.toString();
	}

	public static void setParent(VBox node){ parent = node; }

	public static void setMemSize(int size){ mem = new GuiMemory[size]; }

	public static void setMemLength(int size){ length = size; }

	public static void setMemory(long index, long value){

		if (index >= mem.length) {
			System.out.println("Error: the index is out of bounds");
			System.exit(1);
		} else {
			StringBuilder value2 = new StringBuilder(Long.toBinaryString(value));

			if (value2.length() < length) {

				while(value2.length() < length) { value2.insert(0, '0'); }

				mem[(int)index].data.setText(value2.toString());
			} else if (value2.length() > length) {
				mem[(int)index].data.setText(value2.substring(value2.length() - length));
			} else {
				mem[(int)index].data.setText(value2.toString());
			}

		}

	}

	public static Long getMemory(long index){

		if (index >= mem.length) {
			System.out.println("Error: the index is out of bounds");
			System.exit(1);
			return null;
		} else {
			return Long.parseLong(mem[(int)index].data.getText(), 2);
		}

	}

	public static void initialize(){ for (int i = 0; i < mem.length; i++) { mem[i].data.setText(genZeros(length)); } }

}
