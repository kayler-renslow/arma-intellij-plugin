package com.kaylerrenslow.a3plugin.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kayler
 * @since 04/29/2016
 */
public class InputStreamReader {
	private final InputStream is;
	private int readInt;
	private char readChar;

	public InputStreamReader(InputStream is) {
		this.is = is;
	}

	/**
	 * return true if stream is empty, false if there is more to be read
	 */
	public boolean finished() {
		return this.readInt < 0;
	}

	public void closeStream() throws IOException {
		this.is.close();
	}

	/**
	 * reads the next int from the stream and returns it. This value is saved and can be retrieved again from getInt() or getChar()
	 */
	public int read() throws IOException {
		this.readInt = this.is.read();
		this.readChar = (char) this.readInt;
		return this.readInt;
	}

	/**
	 * reads the next int from the stream and returns it casted as a char. This value is saved and can be retrieved again from getInt() or getChar()
	 */
	public char readWithCast() throws IOException {
		read();
		return this.readChar;
	}

	/**
	 * returns the last read int from the stream
	 */
	public int getInt() {
		return this.readInt;
	}

	/**
	 * returns the last read int from the stream and is casted into a char
	 */
	public char getChar() {
		return this.readChar;
	}


	/**
	 * return true if the last char == '\n' or char == '\r'
	 */
	public boolean lastReadIsNewline() {
		return this.readChar == '\n' || this.readChar == '\r';
	}
}
