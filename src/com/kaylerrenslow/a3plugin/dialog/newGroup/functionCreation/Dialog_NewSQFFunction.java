package com.kaylerrenslow.a3plugin.dialog.newGroup.functionCreation;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Dialog_NewSQFFunction extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;

	private JComboBox<String> cbKnownFunctionLocations;
	private JComboBox<String> cbKnownTagNames;
	private JLabel lblError;

	private JTextField tfFunctionName;
	private JTextField tfFunctionTagName;
	private JTextField tfFunctionLocation;

	final Module module;


	private ArrayList<HeaderConfigFunction> functions;

	Dialog_NewSQFFunction(@NotNull Module module, @Nullable String functionPath) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		if (functionPath != null) {
			this.tfFunctionLocation.setText(functionPath);
		}

		this.module = module;

		initializeChoiceboxes();
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

	private void initializeChoiceboxes() {
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
				if (functionLocations[i].equals(function.getContainingDirectoryPath())) { //don't add the file path more than once
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
		if (tagName.length() == 0) {
			error(this.tfFunctionTagName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.tag_empty"));
			return;
		}
		if (functionName.length() == 0) {
			error(this.tfFunctionName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_name_empty"));
			return;
		}
		if (location.length() == 0) {
			error(this.tfFunctionLocation, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_location_empty"));
			return;
		}
		for (HeaderConfigFunction function : functions) {
			if (tagName.equals(function.getTagName()) && functionName.equals(function.getFunctionClassName())) {
				error(this.tfFunctionName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_name_duplicate"));
				return;
			}
		}
		dispose();
	}

	String getTagName(){
		return this.tfFunctionTagName.getText();
	}

	String getFunctionName(){
		return this.tfFunctionName.getText();
	}

	String getFunctionLocation(){
		return this.tfFunctionLocation.getText();
	}

	private void error(JComponent component, String message) {
		component.requestFocus();
		lblError.setText(message);
	}

	private void onCancel() {
		dispose();
	}


}
