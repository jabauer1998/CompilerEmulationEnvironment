package edu.depauw.emulator_ide.verilog_compiler.data_structure;

import java.io.File;
import java.util.HashMap;

    /**
    * The Context class is where all of the metadata is contained throughout all of the passes
    * This object should be passed through each state of the compiler.
    * This object acts as a singelton onject because no more then one context will ever be needed.
    * @author Jacob Bauer
    */

public class Context {

    //Structures and methods below to keep track of MetaData
    public HashMap<String, File> programFiles;

    private Context(){
        programFiles = new HashMap<>();
    }

    //The context object to return

    private static Context myContext = new Context();

    //The Method to retrive the private context instance

    public static Context getContext(){
        return myContext;
    }

}
