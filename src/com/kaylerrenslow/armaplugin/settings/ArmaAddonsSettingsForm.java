package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ResourceBundle;

/**
 * @author Kayler
 * @since 11/05/2017
 */
public class ArmaAddonsSettingsForm {
	private JPanel panelRoot;
	private TextFieldWithBrowseButton tfWithBrowseReferenceDirectory;
	private Tree treeAddonsRoots;
	private ResourceBundle bundle;
	private DefaultMutableTreeNode treeAddonsRoots_rootNode;

	private void createUIComponents() {
		bundle = ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.ProjectSettingsBundle");

		tfWithBrowseReferenceDirectory = new TextFieldWithBrowseButton(new JTextField(40));

		treeAddonsRoots_rootNode = new RootTreeNode();
		treeAddonsRoots = new Tree(treeAddonsRoots_rootNode);
		treeAddonsRoots.setCellRenderer(new MyColoredTreeCellRenderer());
		treeAddonsRoots.setHoldSize(true);
		treeAddonsRoots.addTreeSelectionListener(e -> {
			System.out.println("ArmaAddonsSettingsForm.createUIComponents e=" + e);
		});
	}

	@NotNull
	public JComponent getPanelRoot() {
		return panelRoot;
	}

	private class ArmaAddonTreeNode extends DefaultMutableTreeNode {

	}

	private class ArmaAddonRootTreeNode extends DefaultMutableTreeNode {

	}

	private class RootTreeNode extends DefaultMutableTreeNode {

		public RootTreeNode() {
			super(bundle.getString("addon-roots"), true);
		}
	}


	private abstract class TreeNodeValue {
		@Nullable
		public Icon getIcon() {
			return null;
		}
	}

	private class MyColoredTreeCellRenderer extends ColoredTreeCellRenderer {
		@Override
		public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (value instanceof TreeNodeValue) {
				TreeNodeValue treeNodeValue = ((TreeNodeValue) value);
				setIcon(treeNodeValue.getIcon());
			}
		}
	}
}
