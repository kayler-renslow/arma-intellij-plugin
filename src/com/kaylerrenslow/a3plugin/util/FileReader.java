package com.kaylerrenslow.a3plugin.util;

import java.io.BufferedInputStream;

/**
 * @author Kayler
 * Reads files.<br>
 * Created on 12/31/2015.
 */
public class FileReader{

	private FileReader(){}

	private static final FileReader INSTANCE = new FileReader();


	/**Reads an entire file from the build path and returns it as a String.<br>
	 * @param path build path to file
	 * @return String with all content inside
	 */
	public static String getText(String path) {
		BufferedInputStream bis = new BufferedInputStream(INSTANCE.getClass().getResourceAsStream(path));
		int size = 0;
		byte[] data;
		String s;
		try{
			while(bis.read() != -1){
				size++;
			}
			bis.close();
			data = new byte[size];
			bis = new BufferedInputStream(INSTANCE.getClass().getResourceAsStream(path));

			for(int i = 0; i < data.length; i++){
				data[i] = (byte)bis.read();
			}
			s = new String(data);

		}catch (Exception e){
			s = "" + INSTANCE.getClass() + ">> error occurred retrieving file " + path;
		}
		return s.replaceAll("\r\n","\n"); //required or Intellij will flip out
	}

}
