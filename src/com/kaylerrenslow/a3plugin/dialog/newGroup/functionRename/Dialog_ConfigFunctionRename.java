package com.kaylerrenslow.a3plugin.dialog.newGroup.functionRename;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.newGroup.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Dialog_ConfigFunctionRename extends JDialog {
	private JPanel contentPane;
	private JComboBox<String> cbKnownTagNames;
	private JLabel lblError;
	private JButton btnPreview;
	private JButton btnRefactor;
	private JButton btnCancel;
	private JLabel lblRenameDesc;

	private JTextField tfFunctionTagName;
	private JTextField tfFunctionName;
	private JTextField tfNewFileName;
	private JCheckBox cbRenameRootTagEle;

	final Module module;

	private ArrayList<HeaderConfigFunction> functions;
	private SQFConfigFunctionInformationHolder oldData;

	Dialog_ConfigFunctionRename(HeaderConfigFunction functionToRename, Module module) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(btnRefactor);
		this.module = module;

		initializeComponents(functionToRename, module);
		initializeListeners();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void initializeComponents(HeaderConfigFunction functionToRename, Module module) {
		String tip_f = Plugin.resources.getString("lang.sqf.refactoring.dialog.functions.rename_root_tag.tooltip");
		this.cbRenameRootTagEle.setToolTipText(String.format(tip_f, functionToRename.getTagName()));

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

		String desc = String.format(Plugin.resources.getString("lang.sqf.refactoring.dialog.functions.desc"), functionToRename.getCallableName());
		this.lblRenameDesc.setText(desc);

		this.tfFunctionTagName.setText(functionToRename.getTagName());
		this.tfFunctionName.setText(functionToRename.getFunctionClassName());
		if (functionToRename.appendFn()) {
			this.tfNewFileName.setEditable(false);
		}
		this.tfNewFileName.setText(functionToRename.getFunctionFileName());

		this.oldData = new SQFConfigFunctionInformationHolder(this.tfFunctionTagName.getText(), this.tfFunctionName.getText(), "", this.tfNewFileName.getText(), this.module, null);

		this.btnPreview.setVisible(false);
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
		tfFunctionName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				updateText();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				updateText();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateText();
			}

			private void updateText() {
				if (!tfNewFileName.isEditable()) {
					tfNewFileName.setText(HeaderConfigFunction.getFunctionFileName(true, tfFunctionName.getText(), ".sqf"));
				}
			}
		});
	}

	private void onOK() {
		if (!validEntries()) {
			return;
		}
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
		if (!functionName.equals(this.oldData.functionClassName)) {
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

	boolean getRenameRootEle() {
		return this.cbRenameRootTagEle.isSelected();
	}

	String getTagName() {
		return this.tfFunctionTagName.getText();
	}

	String getFunctionName() {
		return this.tfFunctionName.getText();
	}

	String getFunctionFileName() {
		return this.tfNewFileName.getText();
	}
}
