package io.github.h20man13.emulator_ide.common.debug;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

import io.github.h20man13.emulator_ide.common.io.Destination;
import io.github.h20man13.emulator_ide.common.debug.item.ErrorItem;
import io.github.h20man13.emulator_ide.common.Position;

import java.io.StringWriter;

public class InfoLogTest {

	@Test
	public void TestInfoLog(){
		Destination destination = new Destination(new StringWriter());
		Position position = new Position(0, 0);
		ErrorLog log = new ErrorLog(destination);

		for (int i = 0; i < 30; i++) { log.addItem(new ErrorItem("" + i, position)); }

		log.printLogNoFlush();
		assertTrue(!destination.getWriter().toString().equals(""));
		destination.flush();
		destination.close();
	}
}
