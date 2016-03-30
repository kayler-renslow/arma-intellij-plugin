package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.ColorPickerScene;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;

/**
 * @author Kayler
 * This action is invoked when the 'Arma Color Picker' menu option under Arma Plugin is clicked
 * Created on 03/29/2016.
 */
public class Action_ShowArmaColorPicker extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent ae) {
		JFrame f = new JFrame(Plugin.resources.getString("plugin.color_picker.title"));
		JFXPanel p = new JFXPanel();
		Scene scene = new ColorPickerScene();
		p.setScene(scene);
		f.setVisible(true);
		f.add(p);
		f.toFront();
		f.setSize((int)scene.getWidth(), (int)scene.getHeight());
	}
}
