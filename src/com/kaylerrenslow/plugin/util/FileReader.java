package com.kaylerrenslow.plugin.util;

import java.io.BufferedInputStream;

/**
 * Created by Kayler on 12/31/2015.
 */
public class FileReader{

	public String getText(String path) {
		BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(path));
		int size = 0;
		int in;
		byte[] data;
		String s;
		try{
			while((in=bis.read()) != -1){
				size++;
			}
			bis.close();
			data = new byte[size];
			bis = new BufferedInputStream(getClass().getResourceAsStream(path));

			for(int i = 0; i < data.length; i++){
				data[i] = (byte)bis.read();
			}
			s = new String(data);

		}catch (Exception e){
			s = getClass() + ">> error occurred retrieving file " + path;
		}
		return s.replaceAll("\r\n","\n"); //required or Intellij will flip out
	}
}
