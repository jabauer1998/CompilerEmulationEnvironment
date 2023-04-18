package io.github.h20man13.emulator_ide.common.io;


import io.github.h20man13.emulator_ide.common.Position;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Source {

	private final Reader input;

	private int past;
	private int current;
	private int next;

	private int lineNumber;
	private int linePosition;

	public Source(InputStream inputStream) { this(new InputStreamReader(inputStream)); }

	public Source(Reader inputReader) {
		input = inputReader;
		lineNumber = 0;
		linePosition = 0;

		try {
		   next = input.read();
		} catch (Exception e) {
		   System.err.println("Error could not read incoming character and could not advance");
		}

		if (!hasNext()) {
		     current = -1;
		     past = -1;
		} else {
		    current = -1;
		    advance();
		}

	}

	public void advance(){

	    if (!atEOD() || hasNext()) {
			past = current;
			current = next;

			if (current == '\n') {
				lineNumber++;
			} else {
				linePosition++;
			}

			try {
				next = input.read();
			} catch (Exception e) {
				System.err.println("Error could not read incoming character and could not advance");
			}
	    }

	}

	public void advance(int times){ for (int i = 0; i < times; i++) { advance(); } }

	public char getPast(){ return (char)past; }

	public char getCurrent(){ return (char)current; }

	public char getNext(){ return (char)next; }

	public boolean hasNext(){ return next != -1; }

	public boolean atEOD(){ return current == -1; }

	public void close(){

		try {
			input.close();
		} catch (Exception e) {
			System.err.println("Error: could not close input stream/reader correctly");
		}

	}

	/**
	 * Returns the position of the source in the stream
	 * 
	 * @return A Position object or tuple containing two elements the line position and the
	 *         line number
	 */

	public Position getPosition(){ return new Position(lineNumber, linePosition); }
}
