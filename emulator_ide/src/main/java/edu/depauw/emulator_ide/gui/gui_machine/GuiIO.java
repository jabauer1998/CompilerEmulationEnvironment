package edu.depauw.emulator_ide.gui.gui_machine;

import java.util.HashMap;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class GuiIO extends VBox{
    private TabPane TabPane;

    private double actualWidth;
    private double actualHeight;

    HashMap<String, TextArea> IoPaneMap;
    
    public GuiIO(double Width, double Height){
        TabPane = new TabPane();
        TabPane.setMaxWidth(Width);
        TabPane.setMaxHeight(Height);

        IoPaneMap = new HashMap<>();
        this.actualWidth = Width;
        this.actualHeight = Height;
    }

    public void AddIoSection(String TabTitle, String... PaneTitles){
        VBox IoPanes = new VBox();
        for(String PaneTitle : PaneTitles){
            VBox IoPaneWithLabel = new VBox();
            Label Name = new Label(PaneTitle);
            TextArea Area = new TextArea();
            Area.setPrefWidth(actualWidth);
            Area.setPrefHeight(actualHeight/PaneTitles.length);

            IoPaneMap.put(PaneTitle, Area);
            IoPaneWithLabel.getChildren().addAll(Name, Area);
            IoPanes.getChildren().addAll(IoPaneWithLabel);
        }

        ScrollPane SPane = new ScrollPane();
        SPane.setContent(IoPanes);

        Tab PaneTab = new Tab(TabTitle, SPane);
        PaneTab.setClosable(false);
        TabPane.getTabs().add(PaneTab);
    }

    public TabPane getTabPane(){
        return TabPane;
    }
}
