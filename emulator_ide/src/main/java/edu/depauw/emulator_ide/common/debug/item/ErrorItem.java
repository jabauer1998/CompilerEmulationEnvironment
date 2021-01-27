package edu.depauw.emulator_ide.common.debug.item;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;

    
import java.lang.String;
import java.lang.StringBuilder;
    
public class ErrorItem implements InfoItem{
    
    private final Position position;
    private final String message;

    public ErrorItem(String message, Position position){
	this.message = message;
	this.position = position;
    }
	
    public String toString(){
	StringBuilder result = new StringBuilder("\u001b[31;1mError\u001b[37m:\u001b[0m ");
	result.append(message);
	result.append(" at [");
	result.append(position.toString());
	result.append(']');
	return result.toString();
    }
}
