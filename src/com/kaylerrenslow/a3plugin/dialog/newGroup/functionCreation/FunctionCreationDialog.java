package com.kaylerrenslow.a3plugin.dialog.newGroup.functionCreation;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.newGroup.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.util.DialogUtil;

import java.awt.*;

/**
 * Creates a synchronous dialog for creating a new SQF config function
 *
 * @author Kayler
 * @since 05/08/2016
 */
public class FunctionCreationDialog {

	private final Dialog_NewSQFFunction dialog;

	private FunctionCreationDialog(Component contextComponent, Module module, String functionDirectoryPath) {
		this.dialog = new Dialog_NewSQFFunction(module, functionDirectoryPath);
		dialog.pack();

		//center window
		dialog.setLocationRelativeTo(DialogUtil.getHighestAncestor(contextComponent));

		dialog.setTitle(Plugin.resources.getString("plugin.dialog.new_sqf_function.title"));
		dialog.setVisible(true);
	}


	public static FunctionCreationDialog showNewInstance(Component contextComponent, Module module, String functionDirectoryPath) {
		return new FunctionCreationDialog(contextComponent, module, functionDirectoryPath);
	}

	/**
	 * Returns true if the dialog was properly closed, false otherwise
	 */
	public boolean dialogFinished() {
		return !this.dialog.cancelled();
	}

	public SQFConfigFunctionInformationHolder getNewFunctionDefinition() {
		if (!dialogFinished()) {
			throw new IllegalStateException("Shouldn't access this when the dialog improperly closed");
		}
		return new SQFConfigFunctionInformationHolder(dialog.getTagName(), dialog.getFunctionName(), dialog.getFunctionLocation(), "", this.dialog.module, null);
	}
}
