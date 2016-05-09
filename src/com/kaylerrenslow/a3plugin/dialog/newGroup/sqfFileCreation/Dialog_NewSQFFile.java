package com.kaylerrenslow.a3plugin.dialog.newGroup.sqfFileCreation;

import javax.swing.*;
import java.awt.event.*;

public class Dialog_NewSQFFile extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField tfFileName;
	private JLabel lblFolder;

	Dialog_NewSQFFile(String newFileDirectory) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.lblFolder.setText(newFileDirectory + "/");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		initializeListeners();
	}

	private void initializeListeners() {
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
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	String getFileName(){
		return this.tfFileName.getText();
	}

}
