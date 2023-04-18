package io.github.H20man13.emulator_ide.gui.gui_job;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public abstract class GuiJob extends VBox{
    private Button ExeButton;
    private TextArea InputSection;

    protected GuiJob(String ButtonText, double Width, double Height){
        ExeButton = new Button(ButtonText);
        ExeButton.setPrefWidth(Width);
        ExeButton.setOnMouseClicked(new EventHandler<Event>() {
            public void handle(Event Event){
                RunJob();
            }
        });
        

        InputSection = new TextArea();
        InputSection.setPrefWidth(Width);

        this.getChildren().addAll(ExeButton, InputSection);
    }

    public abstract void RunJob();

    public TextArea getInputSection(){
        return InputSection;
    }
}