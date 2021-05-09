package edu.depauw.emulator_ide.verilog_compiler.data_structure;

import edu.depauw.emulator_ide.verilog_compiler.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

import java.lang.Long;
import java.lang.Integer;

/**The vectorcall class is used to par.E a call to an array cell
 * @author Jacob Bauer
 */

public class Pattern{

    private final String pattern;

    /** The Range is a data structure to verify case statement numbers
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    
    public Pattern(String pattern){
	this.pattern = pattern;
    }

    public boolean match(long value){
	for(int i = 0; i < pattern.length(); i++){
	    if(pattern.charAt(i) != 'x' && pattern.charAt(i) != 'z' && Integer.parseInt("" + pattern.charAt(i)) != ((value >> (pattern.length() - i - 1)) & 1)){
		return false;
	    }
	}
	return true;
    }
}
