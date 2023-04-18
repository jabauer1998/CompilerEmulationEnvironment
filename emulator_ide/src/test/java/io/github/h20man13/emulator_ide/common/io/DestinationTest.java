package io.github.h20man13.emulator_ide.common.io;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

public class DestinationTest {

	@Test
	public void TestDestination(){
		Destination destination = new Destination(new StringWriter());
		String str = "Lets make an awesome output stream";
		destination.print(str);
		Writer writer = destination.getWriter();
		String result = writer.toString();
		destination.flush();
		destination.close();
		assertTrue(str.equals(result));
	}

}
