package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.kaylerrenslow.armaplugin.ArmaAddonsProjectConfig;
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

	/**
	 * Sets the UI to represent the given config. If the config is null, it will clear every field.
	 * The provided config will also not be mutated within the form.
	 *
	 * @param config the config to use, or null to clear the UI
	 */
	public void setTo(@Nullable ArmaAddonsProjectConfig config) {
		treeAddonsRoots_rootNode.removeAllChildren();
		tfWithBrowseReferenceDirectory.setText("");
		if (config == null) {
			return;
		}
		tfWithBrowseReferenceDirectory.setText(config.getAddonsReferenceDirectory());

		int i = 0;
		for (String addonRoot : config.getAddonsRoots()) {
//			ArmaAddonRootTreeNode node = new ArmaAddonRootTreeNode(addonRoot);
//			treeAddonsRoots_rootNode.insert(node, i);
//			i++;
		}
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
