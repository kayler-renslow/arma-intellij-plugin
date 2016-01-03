package com.kaylerrenslow.plugin.util;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kayler on 01/02/2016.
 */
public class TextFileListToList{

	/** Reads a *.list file and returns a list. Any line starting with # is ignored. Every new line is a new entry. Empty lines are ignored.
	 * @param f File to read
	 * @param list list to add the contents to
	 * @return list of the contents or null if a problem occurred
	 */
	public static List<String> getListFromFile(File f, List<String> list){
		Scanner scan;
		try{
			scan = new Scanner(f);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		while(scan.hasNextLine()){
			String line = scan.nextLine().trim();
			if(line.charAt(0) == '#'){
				continue;
			}
			if(line.length()==0){
				continue;
			}
			list.add(line);
		}
		scan.close();

		return list;
	}
}
