package edu.depauw.emulator_ide.assembler;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.depauw.emulator_ide.assembler.Parser;
import edu.depauw.emulator_ide.assembler.Token;
import edu.depauw.emulator_ide.common.debug.InfoLog;

public class ParserListChecker {
	@Test
	public void seeLists() {
		System.out.println("Made it here test running ");
		Parser.printList("BX list", Parser.bxList);
	}
}
