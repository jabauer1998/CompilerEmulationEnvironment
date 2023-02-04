package edu.depauw.emulator_ide.gui.gui_machine;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class GuiRegisterFile extends VBox {
    public ScrollPane Pane;
    public VBox InnerVBox;
    
    public GuiRegisterFile(){
        Pane = new ScrollPane();
        InnerVBox = new VBox();
        super.getChildren().addAll(Pane);
        Pane.setContent(InnerVBox);
    }

    public void AddGuiRegister(GuiRegister Register){
        InnerVBox.getChildren().add(Register);
    }
}
