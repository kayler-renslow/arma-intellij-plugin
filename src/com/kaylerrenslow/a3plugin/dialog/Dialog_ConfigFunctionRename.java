package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.dialog.util.DialogUtil;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Dialog_ConfigFunctionRename extends JDialog {
	private JPanel contentPane;
	private JTextField tfFunctionTagName;
	private JTextField tfFunctionName;
	private JComboBox<String> cbKnownTagNames;
	private JLabel lblError;
	private JButton btnPreview;
	private JButton btnRefactor;
	private JButton btnCancel;
	private JLabel lblRenameDesc;
	private SimpleGuiAction<Pair<SQFConfigFunctionInformationHolder,SQFConfigFunctionInformationHolder>> refactorAction;
	private Module module;
	private ArrayList<HeaderConfigFunction> functions;
	private SQFConfigFunctionInformationHolder oldData;

	public Dialog_ConfigFunctionRename(String functionName, Module module) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(btnRefactor);
		this.module = module;
		initializeComponents(functionName, module);
		initializeListeners();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void initializeComponents(String functionName, Module module) {
		ArrayList<HeaderConfigFunction> functions;
		try {
			functions = HeaderPsiUtil.getAllConfigFunctionsFromDescriptionExt(module);
		} catch (GenericConfigException e) {
			functions = new ArrayList<>();
		}
		this.functions = functions;
		cbKnownTagNames.addItem("");
		String lastTag = ".";
		for (HeaderConfigFunction function : functions) {
			if (function.getTagName().equals(lastTag)) {
				continue;
			}
			lastTag = function.getTagName();
			cbKnownTagNames.addItem(function.getTagName());
		}

		String desc = String.format(Plugin.resources.getString("lang.sqf.refactoring.dialog.functions.desc"), functionName);
		this.lblRenameDesc.setText(desc);

		SQFStatic.SQFFunctionTagAndName tagAndName = SQFStatic.getFunctionTagAndName(functionName);
		this.tfFunctionTagName.setText(tagAndName.tagName);
		this.tfFunctionName.setText(tagAndName.functionClassName);

		this.oldData = new SQFConfigFunctionInformationHolder(this.tfFunctionTagName.getText(), this.tfFunctionName.getText(), "", this.module, null);
	}

	private void initializeListeners() {
		btnRefactor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

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
		if (!validEntries()) {
			return;
		}
		SQFConfigFunctionInformationHolder neww = new SQFConfigFunctionInformationHolder(this.tfFunctionTagName.getText(), this.tfFunctionName.getText(), "", this.module, null);
		Pair<SQFConfigFunctionInformationHolder,SQFConfigFunctionInformationHolder> data = Pair.create(oldData, neww);
		this.refactorAction.actionPerformed(data);
		dispose();
	}

	private boolean validEntries() {
		String tagName = this.tfFunctionTagName.getText().trim();
		String functionName = this.tfFunctionName.getText().trim();
		if (tagName.length() == 0) {
			error(this.tfFunctionTagName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.tag_empty"));
			return false;
		}
		if (functionName.length() == 0) {
			error(this.tfFunctionName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_name_empty"));
			return false;
		}
		if(!functionName.equals(this.oldData.functionClassName)){
			for (HeaderConfigFunction function : functions) {
				if (tagName.equals(function.getTagName()) && functionName.equals(function.getFunctionClassName())) {
					error(this.tfFunctionName, Plugin.resources.getString("lang.sqf.menu.new.sqf_file.function_name_duplicate"));
					return false;
				}
			}
		}
		return true;
	}

	private void error(JComponent component, String message) {
		component.requestFocus();
		lblError.setText(message);
	}

	private void onCancel() {
		dispose();
	}

	public static void showNewInstance(Component contextComponent, Module module, String renameFunction, SimpleGuiAction<Pair<SQFConfigFunctionInformationHolder,SQFConfigFunctionInformationHolder>> action) {
		Dialog_ConfigFunctionRename dialog = new Dialog_ConfigFunctionRename(renameFunction, module);
		dialog.pack();

		//center window
		dialog.setLocationRelativeTo(DialogUtil.getHighestAncestor(contextComponent));
		dialog.setTitle(Plugin.resources.getString("lang.sqf.refactoring.dialog.functions.title"));
		dialog.refactorAction = action;
		dialog.setVisible(true);
	}
}
