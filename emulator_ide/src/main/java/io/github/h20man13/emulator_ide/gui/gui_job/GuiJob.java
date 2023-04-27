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
import javafx.scene.layout.VBox;

public abstract class GuiJob extends VBox{
    private Button ExeButton;
    private InlineCssTextArea InputSection;
    private HashSet<String> keywords;

    protected GuiJob(String ButtonText, double Width, double Height, String[] keywordArr){
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

        InputSection = new InlineCssTextArea();
        InputSection.setPrefWidth(Width);
        InputSection.setStyle("-fx-fill: black;");
        InputSection.setOnKeyTyped(new EventHandler<Event>(){
            @Override
            public void handle(Event arg0){
                int cursorPosition = InputSection.getCaretPosition();
                String text = InputSection.getText();
                char charAtPosition = text.charAt(cursorPosition - 1);
                
                if(!Character.isWhitespace(charAtPosition)){
                    //If the character added is not a whitespace then we just need 
                    //to highlight or unhighlight the word that was typed
                    int findBeginPosition = Search.findNextWhiteSpace(cursorPosition, text, SearchDirection.LEFT);
                    int findEndPosition = Search.findNextWhiteSpace(cursorPosition, text, SearchDirection.RIGHT);

                    String textSubString = text.substring(findBeginPosition, findEndPosition);
                    if(keywords.contains(textSubString)){
                        //Highlight the Keyword
                        InputSection.setStyle(findBeginPosition, findEndPosition, "-fx-fill: blue;");
                    } else {
                        InputSection.setStyle(findBeginPosition, findEndPosition, "-fx-fill: black;");
                    }
                } else {
                    //Otherwise it is a whitespace and we need to change the color word before and after the whitespace
                    int findEndPositionLeft = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.LEFT);
                    int findBeginPositionLeft = Search.findNextWhiteSpace(findEndPositionLeft, text, SearchDirection.LEFT);

                    int findBeginPositionRight = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.RIGHT);
                    int findEndPositionRight = Search.findNextWhiteSpace(findBeginPositionRight, text, SearchDirection.RIGHT);

                    String leftSubString = text.substring(findBeginPositionLeft, findEndPositionLeft);
                    if(keywords.contains(leftSubString)){
                        //Highlight the Keyword
                        InputSection.setStyle(findBeginPositionLeft, findEndPositionLeft, "-fx-fill: blue;");
                    } else {
                        InputSection.setStyle(findBeginPositionLeft, findEndPositionLeft, "-fx-fill: black;");
                    }

                    String rightSubString = text.substring(findBeginPositionRight, findEndPositionRight + 1);
                    if(keywords.contains(rightSubString)){
                        InputSection.setStyle(findBeginPositionRight, findEndPositionRight, "-fx-fill: blue;");
                    } else {
                        InputSection.setStyle(findBeginPositionRight, findEndPositionRight, "-fx-fill: black;");
                    }
                }
            };
        });

        this.getChildren().addAll(ExeButton, InputSection);
    }

    public abstract void RunJob();

    public InlineCssTextArea getInputSection(){
        return InputSection;
    }
}