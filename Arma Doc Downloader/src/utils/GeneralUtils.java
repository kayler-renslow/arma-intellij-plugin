package utils;

import java.io.File;

public class GeneralUtils {

	/**
	 * @param startPoint where to start searching files
	 * @param fileAction FileAction used for when a file is targeted. Can be null.
	 */
	public static void searchFiles(File startPoint, FileAction fileAction) {
		File[] files = startPoint.listFiles();
		if (files == null) {
			fileAction.doAction(startPoint, false);
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fileAction.doAction(files[i], true);
				searchFiles(files[i], fileAction);
			} else if (files[i].isFile()) {
				fileAction.doAction(files[i], false);
			}
		}
	}

	public static void searchFiles(String startPoint, FileAction fileAction) {
		searchFiles(new File(startPoint), fileAction);
	}

	public static String getFileExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			extension = fileName.substring(i + 1);
		}
		if (extension == "") {
			return "file";
		}
		return extension;
	}

}
