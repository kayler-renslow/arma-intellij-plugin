package utils;

import java.io.File;

public interface FileAction {

	/**
	 * Does an action based upon the given File given: file.
	 *
	 * @param file the file that requires action.
	 */
	void doAction(File file, boolean isFolder);


}
