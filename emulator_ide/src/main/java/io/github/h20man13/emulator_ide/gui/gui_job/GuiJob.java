package io.github.H20man13.emulator_ide.gui.gui_job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fxmisc.richtext.InlineCssTextArea;
import io.github.H20man13.emulator_ide.common.Search;
import io.github.H20man13.emulator_ide.common.Search.SearchDirection;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class GuiJob extends VBox{
    private Button ExeButton;
    private Region InputSection;
    private HashSet<String> keywords;

    public enum TextAreaType{
        DEFAULT,
        KEYWORD
    }

    protected GuiJob(String ButtonText, TextAreaType type, double Width, double Height, String... keywordArr){
        ExeButton = new Button(ButtonText);
        ExeButton.setPrefWidth(Width);
        ExeButton.setOnMouseClicked(new EventHandler<Event>() {
            public void handle(Event Event){
                RunJob();
            }
        });

        keywords = new HashSet<String>();

        for(String keyword : keywordArr){
            this.keywords.add(keyword);
        }

        if(type == TextAreaType.KEYWORD){
            InputSection = new InlineCssTextArea();
            InputSection.setPrefWidth(Width);
            InputSection.setPrefHeight(Height);
            InputSection.setStyle("-fx-fill: black;");
        
            InputSection.setOnKeyTyped(new EventHandler<Event>(){
                @Override
                public void handle(Event arg0){
                    InlineCssTextArea myInputSection = (InlineCssTextArea)InputSection;
                    int cursorPosition = myInputSection.getCaretPosition();
                    String text = myInputSection.getText();
                    //Otherwise it is a whitespace and we need to change the color word before and after the whitespace
                    int findEndPositionLeft = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.LEFT);
                    int findBeginPositionLeft = Search.findNextWhiteSpace(findEndPositionLeft, text, SearchDirection.LEFT);

                    int findBeginPositionRight = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.RIGHT);
                    int findEndPositionRight = Search.findNextWhiteSpace(findBeginPositionRight, text, SearchDirection.RIGHT);

                    String leftSubString = text.substring(findBeginPositionLeft, findEndPositionLeft);
                    if(keywords.contains(leftSubString)){
                        //Highlight the Keyword
                        myInputSection.setStyle(findBeginPositionLeft, findEndPositionLeft, "-fx-fill: blue;");
                    } else {
                        myInputSection.setStyle(findBeginPositionLeft, findEndPositionLeft, "-fx-fill: black;");
                    }

                    String rightSubString = text.substring(findBeginPositionRight, findEndPositionRight + 1);
                    if(keywords.contains(rightSubString)){
                        myInputSection.setStyle(findBeginPositionRight, findEndPositionRight + 1, "-fx-fill: blue;");
                    } else {
                        myInputSection.setStyle(findBeginPositionRight, findEndPositionRight + 1, "-fx-fill: black;");
                    }
                };
            });
        } else {
            InputSection = new TextArea();
            InputSection.setPrefWidth(Width);
            InputSection.setPrefHeight(Height);
        }
        

        this.getChildren().addAll(ExeButton, InputSection);
    }

    public abstract void RunJob();

    public Region getInputSection(){
        return InputSection;
    }
}