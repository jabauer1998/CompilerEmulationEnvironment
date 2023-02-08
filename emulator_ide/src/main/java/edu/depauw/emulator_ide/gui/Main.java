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
import java.util.List;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.common.io.Source;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.Interpreter;
import edu.depauw.emulator_ide.verilog_compiler.parser.Lexer;
import edu.depauw.emulator_ide.verilog_compiler.parser.Parser;
import edu.depauw.emulator_ide.verilog_compiler.parser.Token;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.ModuleDeclaration;
import edu.depauw.emulator_ide.verilog_compiler.parser.pre_processor.Preprocessor;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.type_checker.TypeChecker;
import edu.depauw.emulator_ide.gui.gui_job.GuiJob;
import edu.depauw.emulator_ide.gui.gui_machine.GuiFlag;
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister;

public class Main extends Application {

	public static void main(String[] args){ 
		launch(args); 
	}

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

		GuiEde EdeInstance = new GuiEde(1000, 1);

		EdeInstance.AddJob(new GuiJob("Assemble"));
		EdeInstance.AddJob(new GuiJob("Execute"));

		int RegisterWidthInBytes = 4;

		EdeInstance.AddRegister(new GuiRegister("CPSR", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R0", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R1", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R2", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R3", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R4", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R5", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R6", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R7", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R8", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R9", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R10", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R11", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R12", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R13", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R14", RegisterWidthInBytes, GuiRegister.Format.BINARY));
		EdeInstance.AddRegister(new GuiRegister("R15", RegisterWidthInBytes, GuiRegister.Format.BINARY));

		EdeInstance.AddFlag(new GuiFlag("Z"));
		EdeInstance.AddFlag(new GuiFlag("C"));
		EdeInstance.AddFlag(new GuiFlag("N"));
		EdeInstance.AddFlag(new GuiFlag("V"));

		Scene scene = new Scene(EdeInstance);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}
}
