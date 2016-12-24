package com.kaylerrenslow.a3plugin.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
	 *     </pre>
	 * </p>
	 *
	 * @param is   InputStream that is linked to the list file
	 * @param list list to add the contents to
	 * @return list of the contents or null if a problem occurred
	 */
	public static List<String> getListFromStream(@NotNull InputStream is, @NotNull List<String> list) {
		try {
			InputStreamReader reader = new InputStreamReader(is);
			String current = "";
			char c;
			while (!reader.finished()) {
				c = reader.readWithCast();
				if (c != '#') {
					if (!reader.lastReadIsNewline()) {
						current += c;
					}
					while (!reader.finished()) { // read non-commented line
						c = reader.readWithCast();
						if (reader.lastReadIsNewline()) {
							list.add(current);
							current = "";
							break;
						}
						current += c;
					}
				}

			}
			reader.closeStream();
		} catch (IOException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Couldn't read stream. Reason: " + e.getMessage());
		}

		return list;
	}

}
