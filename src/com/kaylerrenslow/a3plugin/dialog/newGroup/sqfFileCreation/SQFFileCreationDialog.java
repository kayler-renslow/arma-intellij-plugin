package com.kaylerrenslow.a3plugin.dialog.newGroup.sqfFileCreation;

import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.util.DialogUtil;

import java.awt.*;

/**
 * @author Kayler
 *         Creates a synchronous dialog for making new sqf files
 *         Created on 05/08/2016.
 */
public class SQFFileCreationDialog {
	private final Dialog_NewSQFFile dialog;

	private SQFFileCreationDialog(Component contextComponent, VirtualFile newFileDirectory) {
		if (!newFileDirectory.isDirectory()) {
			newFileDirectory = newFileDirectory.getParent();
		}

		this.dialog = new Dialog_NewSQFFile(newFileDirectory.getName());
		dialog.pack();
		dialog.setLocationRelativeTo(DialogUtil.getHighestAncestor(contextComponent));
		dialog.setTitle(Plugin.resources.getString("plugin.dialog.new_sqf_file.title"));
		dialog.setVisible(true);
	}

	public static SQFFileCreationDialog showNewInstance(Component contextComponent, VirtualFile newFileDirectory) {
		return new SQFFileCreationDialog(contextComponent, newFileDirectory);
	}

	public String getFileName() {
		return this.dialog.getFileName();
	}
}
