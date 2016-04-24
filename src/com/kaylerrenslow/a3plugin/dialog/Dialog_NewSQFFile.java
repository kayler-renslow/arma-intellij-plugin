package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class Dialog_NewSQFFile extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField tfFileName;
	private JLabel lblFolder;
	private SimpleGuiAction<Pair<String, VirtualFile>> okAction;
	private VirtualFile newFileDirectory;

	private Dialog_NewSQFFile(AnActionEvent event) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.newFileDirectory = event.getData(DataKeys.VIRTUAL_FILE);
		System.out.println("Dialog_NewSQFFile.Dialog_NewSQFFile " + this.newFileDirectory);
		if(!this.newFileDirectory.isDirectory()){
			this.newFileDirectory = this.newFileDirectory.getParent();
		}
		this.lblFolder.setText(this.newFileDirectory.getCanonicalPath());

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
//		setLocationRelativeTo(PlatformDataKeys.FILE_EDITOR.getData(event.getDataContext()).getComponent()); //TODO null pointer
	}

	private void onOK() {
		okAction.actionPerformed(Pair.create(this.tfFileName.getText(), this.newFileDirectory));
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	public static void showNewInstance(AnActionEvent actionEvent, SimpleGuiAction<Pair<String, VirtualFile>> okAction) {
		Dialog_NewSQFFile dialog = new Dialog_NewSQFFile(actionEvent);
		dialog.pack();
		dialog.okAction = okAction;
		dialog.setTitle(Plugin.resources.getString("plugin.dialog.new_sqf_file.title"));
		dialog.setVisible(true);
	}
}
