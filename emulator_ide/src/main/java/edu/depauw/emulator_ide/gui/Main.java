package edu.depauw.emulator_ide.gui;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.ScrollPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.LinkedList;

import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.passes.Interpreter;
import edu.depauw.emulator_ide.verilog_compiler.passes.TypeChecker;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;

public class Main extends Application {

	public static void main(String[] args){ launch(args); }

	private static ByteArrayInputStream  byteStream;
	private static ByteArrayOutputStream byteOutputStream;

	public static InputStream getByteInputStream(){ return byteStream; }

	public static ByteArrayOutputStream getByteOutputStream(){ return byteOutputStream; }

	@Override
	public void start(Stage stage){
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());

		stage.setMaxWidth(bounds.getWidth());
		stage.setMaxHeight(bounds.getHeight());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());

		stage.setTitle("Emulator Development Environment");

		HBox halves = new HBox(); // Width
		halves.setMaxWidth(stage.getWidth());
		halves.setMaxHeight(stage.getHeight());

		VBox leftSide = new VBox(); // left side of horizontal alignment
		leftSide.setMaxWidth(halves.getMaxWidth()/3);
		leftSide.setMaxHeight(halves.getMaxHeight());
		leftSide.setPrefWidth(halves.getMaxWidth()/3);
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

		middleLeftSide.setMaxWidth(halves.getMaxWidth()/6);
		middleLeftSide.setMaxHeight(halves.getMaxHeight());
		middleLeftSide.setPrefWidth(halves.getMaxWidth()/6);
		middleLeftSide.setPrefHeight(halves.getMaxHeight());

		GuiRegister.setParent(middleLeftSide);
		GuiRegister.setRegisterSize(32);

		GuiRegister CSPR = new GuiRegister("CPSR", 42);
		GuiRegister R0 = new GuiRegister(0);
		GuiRegister R1 = new GuiRegister(1);
		GuiRegister R2 = new GuiRegister(2);
		GuiRegister R3 = new GuiRegister(3);
		GuiRegister R4 = new GuiRegister(4);
		GuiRegister R5 = new GuiRegister(5);
		GuiRegister R6 = new GuiRegister(6);
		GuiRegister R7 = new GuiRegister(7);
		GuiRegister R8 = new GuiRegister(8);
		GuiRegister R9 = new GuiRegister(9);
		GuiRegister R10 = new GuiRegister(10);
		GuiRegister R11 = new GuiRegister(11);
		GuiRegister R12 = new GuiRegister(12);
		GuiRegister R13 = new GuiRegister(13);
		GuiRegister R14 = new GuiRegister(14);
		GuiRegister R15 = new GuiRegister(15);

		VBox middleRightSide = new VBox();
		middleRightSide.setMaxWidth(halves.getMaxWidth()/6);
		middleRightSide.setMaxHeight(halves.getMaxHeight());
		middleRightSide.setPrefWidth(halves.getMaxWidth()/6);
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

		int MEMSIZE = 100;
		GuiMemory.setParent(memoryDump);
		GuiMemory.setMemSize(MEMSIZE);
		GuiMemory.setMemLength(8);

		for (int i = 0; i < MEMSIZE; i++) {
			new GuiMemory(i); // creates a byte of memory to display
		}

		scrollPane.setContent(memoryDump);

		Button reset = new Button("Reset Memory");
		reset.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
		reset.setAlignment(Pos.CENTER);
		middleRightSide.getChildren().addAll(memDumpTitle, scrollPane, reset);

		VBox rightSide = new VBox();
		rightSide.setMaxWidth(halves.getMaxWidth()/3);
		rightSide.setMaxHeight(halves.getMaxHeight());
		rightSide.setPrefWidth(halves.getMaxWidth()/3);
		rightSide.setPrefHeight(halves.getMaxHeight());

		Label status = new Label("Status Bits");
		status.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
		status.setAlignment(Pos.CENTER);

		HBox parentStatus = new HBox();
		parentStatus.setMaxWidth(rightSide.getMaxWidth());
		parentStatus.setMaxHeight(rightSide.getMaxHeight()/2);
		parentStatus.setPrefWidth(rightSide.getMaxWidth());
		parentStatus.setPrefHeight(rightSide.getMaxHeight()/2);

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

		execute.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e){
				// Initialize gui to zeros
				GuiRegister.initialize();
				GuiMemory.initialize();
				GuiStatusBit.initialize();
				// set up input for interpreter
				byteStream = new ByteArrayInputStream(binaryInput.getText().getBytes());

				if (binaryInput.getText().length() > 0) {
					byteOutputStream = new ByteArrayOutputStream();
					String path = "src/main/java/edu/depauw/emulator_ide/processor/ARM7TDMIS.v";
					// Tokenise the tokens
					Destination errorOut = new Destination(System.err);

					try {

						Source source = new Source(new FileReader(path));
						ErrorLog errorLog = new ErrorLog(errorOut);
						Lexer lex = new Lexer(source, errorLog);
						LinkedList<Token> tokens = lex.tokenize();
						// parse the tokens
						Parser parse = new Parser(tokens, errorLog);
						ModuleDeclaration moddec = parse.parseAST();
						// Type check the program
						TypeChecker typeChecker = new TypeChecker(errorLog);
						typeChecker.visit(moddec);

						Interpreter interpreter = new Interpreter(errorLog); // interpret the program and run the binary
						interpreter.visit(moddec);

						standardOutput.setText(byteOutputStream.toString()); // set the output to the output on the stream
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}
		});

		assemble.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e){ System.out.println("Assemble button does not work yet"); }
		});

		reset.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e){
				// Initialize gui to zeros
				GuiRegister.initialize();
				GuiMemory.initialize();
				GuiStatusBit.initialize();
			}
		});

		Scene scene = new Scene(halves);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}
}
