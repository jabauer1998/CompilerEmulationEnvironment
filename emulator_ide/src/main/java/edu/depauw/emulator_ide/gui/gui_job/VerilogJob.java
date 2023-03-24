package edu.depauw.emulator_ide.gui.gui_job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.io.Destination;
import edu.depauw.emulator_ide.gui.GuiEde;
import edu.depauw.emulator_ide.verilog_interpreter.interpreter.EdeInterpreter;

public class VerilogJob extends GuiJob{
    GuiEde EdeInstance;

    public VerilogJob(String JobName, double Width, double Height, GuiEde Ede, String ErrorPane){
        super(JobName, Width, Height);
        EdeInstance = Ede;
    }

    @Override
    public void RunJob(){ // TODO Auto-generated method stub
        CopyDataToOutputFile();
        Destination Dest = new Destination(System.err);
        ErrorLog errLog = new ErrorLog(Dest);
        EdeInterpreter Interpreter = new EdeInterpreter(errLog, EdeInstance);
        Interpreter.interpretFile("InterpretTemp");
    }

    public void CopyDataToOutputFile(){
        File File = new File("InterpretTemp");
        if(File.exists()){
            File.delete();
        }

        try {
            File.createNewFile();
            FileWriter Writer = new FileWriter(File);
            Writer.write(this.getInputSection().getText());
            Writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
