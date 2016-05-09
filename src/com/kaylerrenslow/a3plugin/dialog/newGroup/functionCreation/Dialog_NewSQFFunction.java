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
	private JLabel lblError;

	private JComboBox<String> cbTagName;
	private JTextField tfFunctionName;
	private JComboBox<String> cbFunctionLocation;

	final Module module;

	private ArrayList<HeaderConfigFunction> functions;

	private boolean cancelled;

	Dialog_NewSQFFunction(@NotNull Module module, @Nullable String functionPath) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		if (functionPath != null) {
			this.cbFunctionLocation.setSelectedItem(functionPath);
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
		cbTagName.addItem("");
		cbFunctionLocation.addItem("");
		String lastTag = ".";
		String[] functionLocations = new String[functions.size()];
		for (HeaderConfigFunction function : functions) {
			for (int i = 0; i < functionLocations.length; i++) { //this loop must come before tag check
				if (functionLocations[i] == null) {
					functionLocations[i] = function.getContainingDirectoryPath();
					cbFunctionLocation.addItem(function.getContainingDirectoryPath());
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
			cbTagName.addItem(function.getTagName());
		}
	}

	private void onOK() {
		String tagName = getTagName();
		String functionName = getFunctionName();
		String location = getFunctionLocation();
		if (tagName.length() == 0) {
			error(this.cbTagName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.tag_empty"));
			return;
		}
		if (functionName.length() == 0) {
			error(this.tfFunctionName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_name_empty"));
			return;
		}
		if (location.length() == 0) {
			error(this.cbFunctionLocation, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_location_empty"));
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
		return this.cbTagName.getSelectedItem().toString().trim();
	}

	String getFunctionName(){
		return this.tfFunctionName.getText().trim();
	}

	String getFunctionLocation(){
		return this.cbFunctionLocation.getSelectedItem().toString().trim();
	}

	private void error(JComponent component, String message) {
		component.requestFocus();
		lblError.setText(message);
	}

	private void onCancel() {
		dispose();
		this.cancelled = true;
	}


	boolean cancelled() {
		return this.cancelled;
	}
}
