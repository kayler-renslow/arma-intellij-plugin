package com.kaylerrenslow.a3plugin.util;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple class to specify a file path. For example: file\file1.txt or ../hello.txt
 *
 * @author Kayler
 * @since 05/01/2016.
 */
public class FilePath {
	private final String fileName;
	private FilePath child;

	public static final
	@RegExp
	String DEFAULT_DELIMETER = "[\\\\|/]";

	/**
	 * Create a new file path
	 *
	 * @param fileName root file name
	 * @param child    child file path (or null if none exists)
	 */
	public FilePath(String fileName, @Nullable FilePath child) {
		this.fileName = fileName;
		this.child = child;
	}

	/**
	 * Create a file path with the given root name and children names
	 *
	 * @param fileName       root file name
	 * @param childFileNames children names in order, or null if no children exist
	 * @return a new FilePath instance
	 */
	public static FilePath createFilePath(@NotNull String fileName, @Nullable String... childFileNames) {
		FilePath root = new FilePath(fileName, null);
		FilePath cursor = root;
		if (childFileNames != null) {
			for (String file : childFileNames) {
				cursor.child = new FilePath(file, null);
				cursor = cursor.child;
			}
		}
		return root;
	}

	/**
	 * Create a file path from an existing string.
	 *
	 * @param path                     the file path (e.g. "hello\text.txt")
	 * @param directoryDelimitersRegex delimiter to specify where file name ends. Can be '/' or '\' or anything else
	 * @return a new FilePath instance
	 */
	public static FilePath getFilePathFromString(@NotNull String path, @RegExp String directoryDelimitersRegex) {
		String[] tokens = path.split(directoryDelimitersRegex);

		String[] children = null;
		if (tokens.length > 1) {
			children = new String[tokens.length - 1];
			for (int i = 1; i < tokens.length; i++) {
				children[i - 1] = tokens[i];
			}
		}

		return createFilePath(tokens[0], children);
	}

	/**
	 * Print this file path. Example output: test/test.txt
	 *
	 * @param directoryDelimiter delimiter to separate file names (e.g. '/')
	 * @return String with path.
	 */
	public String getFullPath(char directoryDelimiter) {
		return this.fileName + (this.child != null ? directoryDelimiter + this.child.getFullPath(directoryDelimiter) : "");
	}

	@NotNull
	public String getFileName() {
		return fileName;
	}

	public boolean fileNameIsDotDot() {
		return this.fileName.equals("..");
	}

	@Nullable
	public FilePath getChild() {
		return child;
	}

	@Override
	public String toString() {
		return "FilePath{" + getFullPath('/') + "}";
	}
}
