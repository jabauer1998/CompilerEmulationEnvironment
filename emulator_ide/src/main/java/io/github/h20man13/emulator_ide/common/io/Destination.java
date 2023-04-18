package io.github.h20man13.emulator_ide.common.io;


import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;

public class Destination {

	private final Writer output;

	public Destination(OutputStream outputStream) { this(new OutputStreamWriter(outputStream)); }

	public Destination(Writer outputWriter) { output = outputWriter; }

	public void close(){

		try {
			output.close();
		} catch (Exception e) {
			System.err.println("Error: Stream could not close correctly");
		}

	}

	public void flush(){

		try {
			output.flush();
		} catch (Exception e) {
			System.err.println("Error: Stream could not flush correctly");
		}

	}

	public void print(String toPrint){
		try {
			output.write(toPrint);
		} catch (Exception e) {
			System.err.println("Error: Stream could not print string correctly");
		}

	}

	public void println(String toPrint){ print(toPrint + '\n'); }

	public Writer getWriter(){ return output; }
}
