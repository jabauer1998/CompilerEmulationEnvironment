package edu.depauw.emulator_ide.gui.gui_job;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class GuiJobs extends VBox {
    private ScrollPane JobsPane;
    
    public GuiJobs(){
        JobsPane = new ScrollPane();
        JobsPane.setContent(this);
    }

    public void AddJob(GuiJob Job){
        this.getChildren().add(Job);
    }
}
