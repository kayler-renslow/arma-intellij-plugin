package com.kaylerrenslow.plugin.dialog;

import com.intellij.execution.ui.layout.Grid;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.dialog.util.ActionListenerWrapper;
import com.kaylerrenslow.plugin.dialog.util.DialogActionResponder;
import org.jetbrains.annotations.Nullable;
import util.KVPair;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Kayler on 12/30/2015.
 */
public class Dialog_PluginProperties extends DialogWrapper implements DialogActionResponder<ComboBox, Plugin.PluginPropertiesKey>{
	private static final String TITLE = "Configure Arma 3 Plugin Properties";

	private LinkedList<KVPair> changes = new LinkedList<>();

	public Dialog_PluginProperties(@Nullable Project project, boolean canBeParent) {
		super(project, canBeParent);
		init();
		setTitle(TITLE);
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		JPanel panel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();
		layout.setConstraints(panel, cons);
		panel.setLayout(layout);


		cons.gridx = 0;
		cons.gridy = 0;
		cons.anchor = GridBagConstraints.LINE_START;

		com.intellij.openapi.ui.ComboBox cb;
		int i;
		JLabel lblKey, lblDoc, spacer;

		for(Plugin.PluginPropertiesKey ppk : Plugin.PluginPropertiesKey.values()){
			if(ppk.possibleVals == null || ppk.possibleVals.length == 0){
				continue;
			}
			spacer = new JLabel(" ");
			lblKey = new JLabel(ppk.keyName);
			lblDoc = new JLabel(ppk.doc);
			lblDoc.setForeground(JBColor.GREEN);

			panel.add(lblKey, cons);
			cons.gridy++;
			panel.add(lblDoc, cons);
			cons.gridy++;

			cb = new ComboBox();
			cb.addItem(ppk.defaultValue);
			for(i = 0; i < ppk.possibleVals.length; i++){
				cb.addItem(ppk.possibleVals[i]);
			}

			System.out.println(getClass() + " " + Plugin.pluginProps.getPluginProperty(ppk));
			cb.setSelectedItem(Plugin.pluginProps.getPluginProperty(ppk));
			cb.addActionListener(new ActionListenerWrapper<ComboBox, Plugin.PluginPropertiesKey>(this, cb, ppk));

			panel.add(cb, cons);
			cons.gridy++;
			panel.add(spacer, cons);
			cons.gridx = 0;
			cons.gridy++;
		}
		return panel;
	}

	@Override
	protected void doOKAction() {
		super.doOKAction();
		KVPair<Plugin.PluginPropertiesKey, String> pop;
		boolean madeChanges = changes.size() > 0;
		Iterator<KVPair> iter = changes.iterator();
		while(iter.hasNext()){
			pop = changes.removeFirst();
			Plugin.pluginProps.overridePluginProps(pop.key, pop.value);
		}
		Plugin.pluginProps.savePluginPropsToFile();
		if(!madeChanges){
			return;
		}
		DialogBuilder alert = new DialogBuilder();
		alert.addCloseButton();
		alert.setTitle("Notification");
		alert.centerPanel(new JLabel("All changes will take effect when you restart Intellij."));
		alert.show();
	}

	@Override
	public void actionPerformed(ComboBox cb, Plugin.PluginPropertiesKey key) {
		changes.add(new KVPair<Plugin.PluginPropertiesKey, String>(key, (String)cb.getSelectedItem()));
	}
}
