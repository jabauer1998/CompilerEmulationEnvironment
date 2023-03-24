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
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.Node;

public class Main extends Loader {

	public static void main(String[] args){
		//Scan the current manifest. to find an XML Config File

		if(args.length <= 0){
			Loader load = new Loader();
			load.launch();
		} else  {
			String ConfigArgument = parseConfigArgument(args);
			if(ConfigArgument == null){
				Loader load = new Loader();
				load.launch();
			} else {
				Loader load = new Loader(ConfigArgument);
				load.launch();
			}
		}
	}

	public static String parseConfigArgument(String[] Arguments){
		for(int i = 0; i < Arguments.length; i++){
			String Argument = Arguments[i];
			if(Argument.equals("--config")){
				i++;
				return Arguments[i];
			}
		}

		return null;
	}

}
