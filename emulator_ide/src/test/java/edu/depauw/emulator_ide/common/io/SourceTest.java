package edu.depauw.emulator_ide.common.io;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;

public class SourceTest {

	@Test
	public void TestSource(){
		String input = "Lets make an awesome input stream";
		Source source = new Source(new StringReader(input));
		int index = 0;

		while(!source.atEOD()) { // while source is not at the end of data processed loop through the input
			assertTrue(source.getCurrent() == input.charAt(index));

			if (source.hasNext()) { assertTrue(source.getNext() == input.charAt(index + 1)); }

			source.advance();
			assertTrue(source.getPast() == input.charAt(index));
			index++;
		}

	}

}
