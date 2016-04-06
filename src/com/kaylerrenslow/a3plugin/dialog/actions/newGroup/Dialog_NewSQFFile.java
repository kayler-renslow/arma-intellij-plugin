package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;

import javax.swing.*;
import java.awt.event.*;

public class Dialog_NewSQFFile extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField tfFileName;
	private SimpleGuiAction<String> okAction;

	private Dialog_NewSQFFile() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onOK() {
		okAction.actionPerformed(this.tfFileName.getText());
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	public static void showNewInstance(SimpleGuiAction<String> okAction) {
		Dialog_NewSQFFile dialog = new Dialog_NewSQFFile();
		dialog.pack();
		dialog.okAction = okAction;
		dialog.setTitle(Plugin.resources.getString("plugin.dialog.new_sqf_file.title"));
		dialog.setVisible(true);
	}
}
