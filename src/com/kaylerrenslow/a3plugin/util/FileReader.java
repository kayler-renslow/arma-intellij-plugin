package com.kaylerrenslow.a3plugin.util;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Reads files.
 *
 * @author Kayler
 * @since 12/31/2015
 */
public class FileReader {

	private FileReader() {
	}

	private static final FileReader INSTANCE = new FileReader();


	/**
	 * Reads an entire file from the build path and returns it as a String.<br>
	 *
	 * @param path build path to file
	 * @return String with all content inside
	 */
	public static String getText(String path) throws IllegalArgumentException {
		InputStream stm = INSTANCE.getClass().getResourceAsStream(path);
		if (stm == null) {
			throw new IllegalArgumentException("path '" + path + "' resource not found");
		}
		byte[] data;
		String s;
		try {
			BufferedInputStream bis = new BufferedInputStream(stm);
			data = new byte[bis.available()];
			bis.read(data, 0, data.length);
			s = new String(data);
			bis.close();

		} catch (Exception e) {
			e.printStackTrace(System.out);
			s = "" + INSTANCE.getClass() + ">> error occurred retrieving file " + path;
		}
		return s.replaceAll("\r\n", "\n"); //required or Intellij will flip out
	}

}
