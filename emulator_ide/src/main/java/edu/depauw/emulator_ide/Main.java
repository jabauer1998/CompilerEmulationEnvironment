package edu.depauw.emulator_ide;


import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.gui.GuiEde;
import edu.depauw.emulator_ide.gui.gui_job.GuiJob;
import edu.depauw.emulator_ide.gui.gui_machine.GuiFlag;
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.circuit_elem.nodes.Node;

public class Main extends Application {
	private String ConfigPath;
	private ErrorLog errLog;

	private Main(){
		Destination errorOut = new Destination(System.err);
		errLog = new ErrorLog(errorOut);
		ConfigPath = null;
		try{
			Enumeration<URL> resources = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			loop: while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				try {
			  		Manifest manifest = new Manifest(resource.openStream());
			  		// check that this is your manifest and do what you need or get the next one
					Attributes attributes = manifest.getMainAttributes();
					String cfgPath = attributes.getValue("Config");
					if(cfgPath != null && cfgPath.contains("xml")){
						ConfigPath = cfgPath;
						break loop;
					}
				} catch (IOException E) {
					errLog.addItem(new ErrorItem("Cannot Open URL " + resource.toString()));
				}
			}
		} catch(Exception exp){
			errLog.addItem(new ErrorItem("Error Loading Resources via Reflection!!!"));
		}
	}

	public static void main(String[] args){
		//Scan the current manifest. to find an XML Config File

		Main Loader = new Main();
		Loader.launch();
	}

	
	public void start(Stage stage){

		if(ConfigPath == null){
			errLog.addItem(new ErrorItem("Error: No .xml config file found in the manifest"));
			errLog.printLog();
			return;
		}

		String ProcessorPath = null;

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
			
			NodeList ProcessorElem = Doc.getElementsByTagName("Processor");
			NodeList TitleElem = Doc.getElementsByTagName("Title");


			if(ProcessorElem == null || TitleElem.getLength() == 0){
				Element ProcessorTag = (Element)ProcessorElem.item(0);
				ProcessorPath = ProcessorTag.getTextContent();
			} else {
				System.err.println("Error: The error output wasnt printed to the correct location");
			}

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
