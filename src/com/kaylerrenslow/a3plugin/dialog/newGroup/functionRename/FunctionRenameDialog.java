package com.kaylerrenslow.a3plugin.dialog.newGroup.functionRename;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.newGroup.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.util.DialogUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;

import java.awt.*;

/**
 * @author Kayler
 * Creates a synchronous dialog that gets information from the user for renaming a config function
 * Created on 05/08/2016.
 */
public class FunctionRenameDialog {
	private final Dialog_ConfigFunctionRename dialog;
	private final HeaderConfigFunction renameFunction;


	private FunctionRenameDialog(Component contextComponent, Module module, HeaderConfigFunction renameFunction) {
		this.dialog = new Dialog_ConfigFunctionRename(renameFunction, module);
		this.renameFunction = renameFunction;
		dialog.pack();

		//center window
		dialog.setLocationRelativeTo(DialogUtil.getHighestAncestor(contextComponent));
		dialog.setTitle(Plugin.resources.getString("lang.sqf.refactoring.dialog.functions.title"));
		dialog.setVisible(true);
	}

	/** Creates and shows a new function renaming dialog
	 * @param contextComponent the component that invoked the operation
	 * @param module module
	 * @param renameFunction function to rename
	 * @return new function renaming dialog instance that is shown
	 */
	public static FunctionRenameDialog showNewInstance(Component contextComponent, Module module, HeaderConfigFunction renameFunction){
		return new FunctionRenameDialog(contextComponent, module, renameFunction);
	}

	public boolean getRenameRootTagValue() {
		return dialog.getRenameRootEle();
	}

	public SQFConfigFunctionInformationHolder getNewFunctionDefinition() {
		return new SQFConfigFunctionInformationHolder(dialog.getTagName(), dialog.getFunctionName(), this.renameFunction.getContainingDirectoryPath(), dialog.getFunctionFileName(), this.dialog.module, null);
	}

}
