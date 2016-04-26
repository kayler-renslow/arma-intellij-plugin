package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Dialog_NewSQFFunction extends JDialog {
	private final Module module;
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField tfFunctionName;
	private JComboBox<String> cbKnownTagNames;
	private JComboBox<String> cbKnownFunctionLocations;
	private SimpleGuiAction<SQFConfigFunctionInformationHolder> okAction;
	private JTextField tfFunctionTagName;
	private JTextField tfFunctionLocation;
	private JLabel lblError;

	private ArrayList<HeaderConfigFunction> functions;

	public Dialog_NewSQFFunction(AnActionEvent creationEvent) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.module = creationEvent.getData(DataKeys.MODULE);
		initializeThings();

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

	private void initializeThings() {
		try {
			functions = HeaderPsiUtil.getAllConfigFunctionsFromDescriptionExt(this.module);
		} catch (GenericConfigException e) {
			functions = new ArrayList<>();
			return;
		}
		cbKnownTagNames.addItem("");
		cbKnownFunctionLocations.addItem("");
		String lastTag = ".";
		String[] functionLocations = new String[functions.size()];
		for (HeaderConfigFunction function : functions) {
			for (int i = 0; i < functionLocations.length; i++) { //this loop must come before tag check
				if (functionLocations[i] == null) {
					functionLocations[i] = function.getContainingDirectoryPath();
					cbKnownFunctionLocations.addItem(function.getContainingDirectoryPath());
					break;
				}
				if (functionLocations[i].equals(function.getContainingDirectoryPath())) {
					break;
				}
			}

			if (function.getTagName().equals(lastTag)) {
				continue;
			}
			lastTag = function.getTagName();
			cbKnownTagNames.addItem(function.getTagName());
		}
		cbKnownFunctionLocations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tfFunctionLocation.setText(cbKnownFunctionLocations.getSelectedItem().toString());
				buttonOK.requestFocus();
			}
		});
		cbKnownTagNames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tfFunctionTagName.setText(cbKnownTagNames.getSelectedItem().toString());
				tfFunctionName.requestFocus();
			}
		});
	}

	private void onOK() {
		String tagName = this.tfFunctionTagName.getText().trim();
		String functionName = this.tfFunctionName.getText().trim();
		String location = this.tfFunctionLocation.getText().trim();
		if(tagName.length() == 0){
			error(this.tfFunctionTagName, "Function tag can't be empty.");
			return;
		}
		if(functionName.length() == 0){
			error(this.tfFunctionName, "Function name can't be empty.");
			return;
		}
		if(location.length() == 0){
			error(this.tfFunctionLocation, "Function location can't be empty.");
			return;
		}
		for(HeaderConfigFunction function : functions){
			if(tagName.equals(function.getTagName()) && functionName.equals(function.getFunctionClassName())){
				error(this.tfFunctionName, "Function name must be unique.");
				return;
			}
		}
		okAction.actionPerformed(new SQFConfigFunctionInformationHolder(this.tfFunctionTagName.getText(), this.tfFunctionName.getText(), this.tfFunctionLocation.getText(), this.module, null));
		dispose();
	}

	private void error(JComponent component, String message){
		component.requestFocus();
		lblError.setText(message);
	}

	private void onCancel() {
		dispose();
	}

	public static void showNewInstance(AnActionEvent actionEvent, SimpleGuiAction<SQFConfigFunctionInformationHolder> okAction) {
		Dialog_NewSQFFunction dialog = new Dialog_NewSQFFunction(actionEvent);
		dialog.pack();
		Component component = actionEvent.getData(DataKeys.CONTEXT_COMPONENT);
		while (component.getParent() != null) {
			component = component.getParent();
		}
		dialog.setLocationRelativeTo(component);
		dialog.okAction = okAction;
		dialog.setTitle(Plugin.resources.getString("plugin.dialog.new_sqf_function.title"));
		dialog.setVisible(true);
	}

}
