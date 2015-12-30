package com.kaylerrenslow.plugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.dialog.util.ActionListenerWrapper;
import com.kaylerrenslow.plugin.dialog.util.DialogActionResponder;
import org.jetbrains.annotations.Nullable;
import util.KVPair;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Kayler on 12/30/2015.
 */
public class Dialog_About extends DialogWrapper{
	private static final String TITLE = "About the Arma 3 Plugin";
	private static final String TEXT = "";

	public Dialog_About(@Nullable Project project, boolean canBeParent) {
		super(project, canBeParent);
		init();
		setTitle(TITLE);
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		JPanel panel = new JPanel();

		return panel;
	}

}
