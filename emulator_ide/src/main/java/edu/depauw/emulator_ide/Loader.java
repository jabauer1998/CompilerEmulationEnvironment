package edu.depauw.emulator_ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
import edu.depauw.emulator_ide.gui.gui_machine.GuiFlag;
import edu.depauw.emulator_ide.gui.gui_machine.GuiRegister;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Loader extends Application {

    private String configPath;
	private ErrorLog errLog;

	public Loader(){
		Destination errorOut = new Destination(System.err);
		errLog = new ErrorLog(errorOut);
		configPath = null;
		try{
			String resource = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();


			System.out.println("File Loader Printout");
			System.out.println("----------------------------------------");
			System.out.println("Examining files located inside the src -");
			System.out.println(resource.toString());

			if(!resource.endsWith(".jar")){
				//Find a Jar inside the current path
				System.out.println("Cant find a Jar...");
				System.out.println("Searching for a Jar at path -");
				System.out.println(resource.toString());
				File topFolder = new File(resource);
				for(File file : topFolder.listFiles()){
					if(file.getName().endsWith(".jar")){
						System.out.println("Found jar at path -");
						System.out.println(file.toString());
						resource = file.getAbsolutePath();
						break;
					}
				}
			}

            ZipInputStream zippedJarStream = new ZipInputStream(new FileInputStream(resource));

			
			System.out.println("----------------------------------------");

			while(true){
				ZipEntry zipEntry = zippedJarStream.getNextEntry();
				if(zipEntry == null){
					break;
				} else {
					String entryName = zipEntry.getName();
					System.out.println("Examining entry at path -");
					System.out.println(entryName);
					System.out.println("----------------------------------------");
					if(entryName.endsWith(".xml")){
						configPath = zipEntry.toString();
						break;
					}
				}
			}
				

		} catch(Exception exp){
			errLog.addItem(new ErrorItem("Error: Loading Resources via Reflection!!!"));
		}
	}

	public Loader(String configArgument){
		Destination errorOut = new Destination(System.err);
		errLog = new ErrorLog(errorOut);
		configPath = configArgument;
	}

	/**
	 * The following code builds the default ArmTDMI7 machine in java
	 * This is the default machine if no xml Config File is provided 
	 * @param stage
	*/
	private void buildDefaultMachine(Stage stage){
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());

		stage.setMaxWidth(bounds.getWidth());
		stage.setMaxHeight(bounds.getHeight());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());

		stage.setTitle("Emulator Development Environment");

		int NumberOfBytes = 1000;
		int NumberOfBytesInRow = 4;

		GuiEde EdeInstance = new GuiEde(NumberOfBytes, NumberOfBytesInRow, stage.getMaxWidth(), stage.getMaxHeight());

		EdeInstance.AddExeJob("Assemble", "ArmAssembler.exe -i TempInput -o TempOutput -e TempError", "TempInput", "TempOutput", "TempError");
		EdeInstance.AddVerilogJob("Execute", "Processor.v", "ErrorFile");

		int RegisterLength = 32;
		EdeInstance.AddRegister("CPSR", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R0", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R1", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R2", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R3", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R4", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R5", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R6", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R7", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R8", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R9", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R10", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R11", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R12", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R13", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R14", RegisterLength, GuiRegister.Format.BINARY);
		EdeInstance.AddRegister("R15", RegisterLength, GuiRegister.Format.BINARY);


		EdeInstance.AddFlag("C");
		EdeInstance.AddFlag("V");
		EdeInstance.AddFlag("N");
		EdeInstance.AddFlag("O");


		EdeInstance.AddIoSection("Errors", "StandardError");
		EdeInstance.AddIoSection("Io", "StandardInput", "StandardOutput");
		
		Scene scene = new Scene(EdeInstance);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	private void loadMachineFromConfig(Stage stage){
		String processorPath = null;

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

			InputStream configStream = this.getClass().getResourceAsStream(configPath);
			File configFile = new File(configPath);

			if(!configFile.exists()){
				System.err.println("Config File was not found with path -");
				System.err.println(configPath);
				return;
			}

			//Collect the Title Information from the File that is pointed to
			Document Doc = Builder.parse(configStream);
			
			NodeList ProcessorElem = Doc.getElementsByTagName("Processor");
			NodeList TitleElem = Doc.getElementsByTagName("Title");


			if(ProcessorElem == null || TitleElem.getLength() == 0){
				Element ProcessorTag = (Element)ProcessorElem.item(0);
				processorPath = ProcessorTag.getTextContent();
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
				NodeList ConfigNodes = Node.getElementsByTagName("Memory");
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
			NodeList JobNodes = Doc.getElementsByTagName("ExeJob");
			for(int i = 0; i < JobNodes.getLength(); i++){
				Element JobElem = (Element)JobNodes.item(i);
				String JobName = JobElem.getAttribute("Name");
				String Exec = JobElem.getAttribute("Exe");
				String Input = JobElem.getAttribute("Input");
				String Output = JobElem.getAttribute("Output");
				String Error = JobElem.getAttribute("Error");
				EdeInstance.AddExeJob(JobName, Exec, Input, Output, Error);
			}

			//The Final Job that is required is a Verilog Job that is utilized to run the Interpreter
			NodeList VerilogNodes = Doc.getElementsByTagName("VerilogJob");
			for(int i = 0; i < JobNodes.getLength(); i++){
				Element JobElem = (Element)VerilogNodes.item(i);
				String JobName = JobElem.getAttribute("Name");
				String Processor = JobElem.getAttribute("Proc");
				EdeInstance.AddVerilogJob(JobName, Processor, "ErrOut");
			}

			

			//After Jobs we will need to setup the Register File
			Element MachineElement = (Element)MachineElem.item(0);
			NodeList Registers = MachineElement.getElementsByTagName("Reg");
			for(int i = 0; i < Registers.getLength(); i++){
				Element RegisterElement = (Element)Registers.item(i);
				String Name = RegisterElement.getAttribute("Name");
				int Width = Integer.parseInt(RegisterElement.getAttribute("Width"));
				EdeInstance.AddRegister(Name, Width, GuiRegister.Format.BINARY);
			}

			//Finally we need to Add the Status Bits
			NodeList Flags = MachineElement.getElementsByTagName("Flag");
			for(int i = 0; i < Flags.getLength(); i++){
				Element FlagElement = (Element)Registers.item(i);
				String Name = FlagElement.getAttribute("Name");
				EdeInstance.AddFlag(Name);
			}

			Scene scene = new Scene(EdeInstance);
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();
		} catch(Exception exp){
			System.err.println("Error: Cannot Set Security Feature for the Parser");
		}
	}
    

    @Override
    public void start(Stage stage){

		if(configPath == null){
			buildDefaultMachine(stage);
		} else {
			loadMachineFromConfig(stage);
		}
	}
    
}
