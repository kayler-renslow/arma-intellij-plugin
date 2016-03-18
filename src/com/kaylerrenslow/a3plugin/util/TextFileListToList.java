package com.kaylerrenslow.a3plugin.util;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kayler on 01/02/2016.
 */
public class TextFileListToList{

	/** Reads a *.list file and returns a list. Any line starting with # is ignored. Every new line is a new entry. Empty lines are ignored.
	 * <p>Sample .list file: <br>
	 *     <pre>
	 *     #comment that is ignored
	 *     line that will be added
	 *     this line will be added include comment #comment
	 *     </pre>
	 *     </p>
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

	/** Adds file names from a directory into a given list. <b>This will not enter/visit directories inside the given directory.</b>
	 * @param directory directory with all file names
	 * @param list list to add the file names to.
	 * @param addDirectories true if the directories inside 'directory' should be added to the list
	 * @return the same list passed in the arguments
	 */
	public static List<String> appendFileNamesToList(File directory, List<String> list, boolean addDirectories){
		if(!directory.isDirectory()){
			throw new IllegalArgumentException("directory argument must be a directory");
		}
		File[] files = directory.listFiles();
		for(int i = 0; i < files.length; i++){
			if(!files[i].isDirectory() || addDirectories){
				list.add(files[i].getName());
			}
		}

		return list;
	}
}
