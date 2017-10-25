package com.kaylerrenslow.armaplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * @author Kayler
 * @since 09/27/2017
 */
public class SetArmaToolsDirAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);


		new ArmaToolsConfigDialog(anActionEvent.getProject()).show();
		//todo finish this method

		//todo have popup that says whether or not the directory is valid
	}

	private static class ArmaToolsConfigDialog extends DialogWrapper {

		private final ResourceBundle bundle = ArmaPlugin.getPluginBundle();
		private final JLabel lblCurrentDirPath = new JLabel();

		protected ArmaToolsConfigDialog(@Nullable Project project) {
			super(project);
			setTitle(bundle.getString("Dialog.ArmaToolsConfig.title"));
		}

		@Nullable
		@Override
		protected JComponent createCenterPanel() {
			JPanel root = new JPanel();

			JLabel lblAbout = new JLabel(bundle.getString("Dialog.ArmaToolsConfig.about_label"));
			lblAbout.setFont(lblAbout.getFont().deriveFont(Font.PLAIN, 15));
			root.add(lblAbout);

			{ //view and set the current arma tools directory
				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
				panel.add(new JLabel(bundle.getString("Dialog.ArmaToolsConfig.current-directory")));
				panel.add(lblCurrentDirPath);

				updateCurrentDirPathLabel();

				root.add(panel);
			}


			return root;
		}

		private void updateCurrentDirPathLabel() {
			File armaTools = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
			lblCurrentDirPath.setText(armaTools == null ?
					bundle.getString("Dialog.ArmaToolsConfig.not-set") : armaTools.getAbsolutePath()
			);
		}
	}
}
