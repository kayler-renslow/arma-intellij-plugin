package com.kaylerrenslow.armaplugin.util;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Kayler
 * @since 09/07/2017
 */
public class FileResourceContentExtractor {
	@NotNull
	public static String extract(@NotNull String resourcePath) {
		InputStream stm = FileResourceContentExtractor.class.getResourceAsStream(resourcePath);
		if (stm == null) {
			throw new IllegalArgumentException("path '" + resourcePath + "' not found");
		}
		StringBuilder sb = new StringBuilder();
		Scanner s = new Scanner(stm);
		while (s.hasNextLine()) {
			sb.append(s.nextLine());
			sb.append('\n');
		}
		return sb.toString();
	}
}
