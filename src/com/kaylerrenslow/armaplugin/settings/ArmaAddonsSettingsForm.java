package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Kayler
 * @since 11/05/2017
 */
public class ArmaAddonsSettingsForm {
	private JPanel panelRoot;
	private TextFieldWithBrowseButton tfWithBrowseReferenceDirectory;
	private JBSplitter splitterAddonsRoots;

	private void createUIComponents() {
		tfWithBrowseReferenceDirectory = new TextFieldWithBrowseButton(new JTextField(40));

		{
			splitterAddonsRoots = new JBSplitter(false);
			Tree tree = new Tree();
			splitterAddonsRoots.setFirstComponent(tree);
			splitterAddonsRoots.setSecondComponent(new JBList<>());
		}

	}

	@NotNull
	public JComponent getPanelRoot() {
		return panelRoot;
	}
}
