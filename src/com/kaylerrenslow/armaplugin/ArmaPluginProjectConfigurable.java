package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * @author Kayler
 * @since 09/28/2017
 */
public class ArmaPluginProjectConfigurable implements Configurable {
	private final ResourceBundle bundle = ArmaPlugin.getPluginBundle();
	private final JLabel lblCurrentDirPath = new JLabel();

	@Nls
	@Override
	public String getDisplayName() {
		return "Arma Plugin";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		return new ArmaPluginSettingsForm().getPanelRoot();
//		JPanel root = new JPanel();
//		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
//
//		JLabel lblAbout = new JLabel(bundle.getString("Dialog.ArmaToolsConfig.about_label"));
//		lblAbout.setFont(lblAbout.getFont().deriveFont(Font.PLAIN, 15));
//		root.add(lblAbout);
//		lblAbout.setHorizontalAlignment(SwingConstants.LEFT);
//
//		{ //view and set the current arma tools directory
//			JPanel panel = new JPanel();
//			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
//			panel.add(new JLabel(bundle.getString("Dialog.ArmaToolsConfig.current-directory")));
//			panel.add(lblCurrentDirPath);
//
//			updateCurrentDirPathLabel();
//
//			root.add(panel);
//		}
//
//		return root;
	}

	private void updateCurrentDirPathLabel() {
		File armaTools = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
		lblCurrentDirPath.setText(armaTools == null ?
				bundle.getString("Dialog.ArmaToolsConfig.not-set") : armaTools.getAbsolutePath()
		);
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void apply() throws ConfigurationException {

	}
}
