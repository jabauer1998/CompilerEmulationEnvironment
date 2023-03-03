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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
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
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister.Format;

public class Main extends Application {
	private static String ConfigPath = null;
	private static String ProcessorPath = null;
	public static void main(String[] args){
		Destination errorOut = new Destination(System.err);

		HashMap<String, String> config = parseArgs(args);

		ErrorLog Log = new ErrorLog(errorOut);

		if(!config.containsKey("config")){
			Log.addItem(new ErrorItem("Error Config Argument was not found!!"));
		} else {
			ConfigPath = config.get("config");
		}

		if(!config.containsKey("processor")){
			Log.addItem(new ErrorItem("Error Processor Argument was not found!!"));
		} else {
			ProcessorPath = config.get("processor");
		}

		if(config.containsKey("config") && config.containsKey("processor")){
			launch();
		}
	}

	private static HashMap<String, String> parseArgs(String[] args){
		HashMap<String, String> config = new HashMap<>();
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("--cfg") || args[i].equals("--config") || args[i].equals("-c")){
				i++;
				config.put("config", args[i]);
			} else if(args[i].equals("--processor") || args[i].equals("-p") || args[i].equals("--proc")){
				i++;
				config.put("processor", args[i]);
			}
		}

		return config;
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

		DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();

		try{
			dBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder Builder = dBuilderFactory.newDocumentBuilder();

			FileReader Reader = new FileReader(ConfigPath);
			File ConfigFile = new File(ConfigPath);

			if(!ConfigFile.exists()){
				System.err.println("Config File was not found with path -");
				System.err.println(ConfigPath);
				return;
			}

			//Collect the Title Information from the File that is pointed to
			Document Doc = Builder.parse(ConfigFile);
			NodeList TitleElem = Doc.getElementsByTagName("Title");

			if(TitleElem == null || TitleElem.getLength() == 0){
				stage.setTitle("Emulator Development Environment");
			} else {
				stage.setTitle(TitleElem.item(0).getTextContent());
			}

			//Collect the information utilized for Memory Initialization
			//These are located under the Machine/Config tag

			int NumberOfBytes = 1000;
			int NumberOfBytesInRow = 4;

			NodeList MachineElem = Doc.getElementsByTagName("Machine");
			if(MachineElem != null && MachineElem.getLength() != 0){
				Element Node = (Element)MachineElem.item(0);
				NodeList ConfigNodes = Node.getElementsByTagName("Config");
				if(ConfigNodes != null && ConfigNodes.getLength() != 0){
					Element ConfigNode = (Element)ConfigNodes.item(0);
					NodeList NumberOfBytesList = ConfigNode.getElementsByTagName("NumBytes");
					NodeList BytesInRowList = ConfigNode.getElementsByTagName("BytesInRow");
					NumberOfBytes = Integer.parseInt(NumberOfBytesList.item(0).getTextContent());
					NumberOfBytesInRow = Integer.parseInt(BytesInRowList.item(0).getTextContent());
				}
			}

			GuiEde EdeInstance = new GuiEde(NumberOfBytes, NumberOfBytesInRow, stage.getMaxWidth(), stage.getMaxHeight());
			EdeInstance.setMaxWidth(stage.getMaxWidth());
			EdeInstance.setMaxHeight(stage.getMaxHeight());
			EdeInstance.setPrefWidth(stage.getMaxWidth());
			EdeInstance.setPrefHeight(stage.getMaxHeight());

			//Now we need to Build the Jobs Portion of the Ede
			//We will go into each of the Jobs and generate the Jobs
			NodeList JobNodes = Doc.getElementsByTagName("Job");
			for(int i = 0; i < JobNodes.getLength(); i++){
				Element JobElem = (Element)JobNodes.item(i);
				String JobName = JobElem.getAttribute("Name");
				EdeInstance.AddJob(new GuiJob(JobName));
			}

			//After Jobs we will need to setup the Register File
			Element MachineElement = (Element)MachineElem.item(0);
			NodeList Registers = MachineElement.getElementsByTagName("Reg");
			for(int i = 0; i < Registers.getLength(); i++){
				Element RegisterElement = (Element)Registers.item(i);
				String Name = RegisterElement.getAttribute("Name");
				int Width = Integer.parseInt(RegisterElement.getAttribute("Width"));
				EdeInstance.AddRegister(new GuiRegister(Name, Width, GuiRegister.Format.BINARY));
			}

			//Finally we need to Add the Status Bits
			NodeList Flags = MachineElement.getElementsByTagName("Flag");
			for(int i = 0; i < Flags.getLength(); i++){
				Element FlagElement = (Element)Registers.item(i);
				String Name = FlagElement.getAttribute("Name");
				EdeInstance.AddFlag(new GuiFlag(Name));
			}

			Scene scene = new Scene(EdeInstance);
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();
		} catch(Exception exp){
			System.err.println("Error: Cannot Set Security Feature for the Parser");
		}
	}
}
