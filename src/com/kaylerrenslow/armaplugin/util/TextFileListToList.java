package com.kaylerrenslow.armaplugin.util;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Kayler
 * @since 01/02/2016
 */
public class TextFileListToList {


	/**
	 * Reads a *.list file and returns a list. Any line starting with # is ignored. Every new line is a new entry. Empty lines are ignored.
	 * <p>Sample .list file: <br>
	 * <pre>
	 *     #comment that is ignored
	 *     line that will be added
	 *     this line will be added including comment #comment
	 * </pre>
	 * </p>
	 *
	 * @param is   InputStream that is linked to the list file
	 * @return list of the contents or null if a problem occurred
	 */
	@NotNull
	public static List<String> getListFromStream(@NotNull InputStream is) {
		Scanner scanner = new Scanner(is);
		ArrayList<String> list = new ArrayList<>();
		while (scanner.hasNextLine()) {
			String l = scanner.nextLine();
			if (l.startsWith("#")) {
				continue;
			}
			list.add(l);
		}
		scanner.close();

		//trim off any unnecessary stuff inside the list by adding all elements into new list
		return new ArrayList<>(list);
	}

}