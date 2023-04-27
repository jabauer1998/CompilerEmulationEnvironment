package io.github.H20man13.emulator_ide.gui.gui_machine;

import java.util.Collection;
import java.util.HashMap;
import io.github.H20man13.emulator_ide._interface.Flags;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;

public class GuiFlags extends HBox implements Flags {
    private ScrollPane FlagPane;
    private HashMap<String, GuiFlag> flagMap;

    private double actualWidth;
    private double actualHeight;
    
    public GuiFlags(double Width, double Height){
        FlagPane = new ScrollPane();
        FlagPane.setContent(this);
        FlagPane.setMaxWidth(Width);
        FlagPane.setMaxHeight(Height);

        this.actualHeight = Height;
        this.actualWidth = Width;

        FlagPane.setVbarPolicy(ScrollBarPolicy.NEVER);

        flagMap = new HashMap<>();
    }

    public void AddGuiFlag(String Name){
        GuiFlag Flag = new GuiFlag(Name, actualWidth / 6, actualHeight);

        this.getChildren().addAll(Flag);

        flagMap.put(Flag.getName(), Flag);
    }

    public ScrollPane getScrollPane(){
        return FlagPane;
    }

    @Override
    public long getStatusValue(String statusName){
        GuiFlag Flag = flagMap.get(statusName);
        return Flag.isSet() ? 1 : 0;
    }

    @Override
    public void setStatusValue(String statusName, long statusValue){
        GuiFlag Flag = flagMap.get(statusName);
        Flag.Set(statusValue != 0);
    }

    
    public void clearStatusValues(){
        Collection<GuiFlag> values = flagMap.values();
        for(GuiFlag value : values){
            value.Set(false);
        }
    }
}
