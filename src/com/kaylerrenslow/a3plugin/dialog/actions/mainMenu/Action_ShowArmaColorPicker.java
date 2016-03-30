package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.dialog.ColorPickerScene;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;

/**
 * Created by Kayler on 03/29/2016.
 */
public class Action_ShowArmaColorPicker extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent ae) {
		JFrame f = new JFrame("Color Picker");
		JFXPanel p = new JFXPanel();
		Scene scene = new ColorPickerScene();
		p.setScene(scene);
		f.setVisible(true);
		f.add(p);
		f.setSize((int)scene.getWidth(), (int)scene.getHeight());
	}
}
