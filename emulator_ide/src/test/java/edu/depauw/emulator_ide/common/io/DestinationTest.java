package  edu.depauw.emulator_ide.common.io;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

public class DestinationTest{

    
    @Test
    public void TestDestination(){
	String input = "Lets make an awesome input stream";
	Destination destination = new Destination(new StringWriter());
	int index = 0;
	String str = "Cool what is this bro";
	destination.print(str);
	Writer writer = destination.getWriter();
	assertTrue(str.equals(writer.toString()));
    }

    
}


