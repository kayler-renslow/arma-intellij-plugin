package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.util.KVPair;
import com.kaylerrenslow.a3plugin.dialog.util.ActionListenerWrapper;
import com.kaylerrenslow.a3plugin.dialog.util.DialogActionResponder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Kayler on 12/30/2015.
 */
public class Dialog_PluginProperties extends DialogWrapper implements DialogActionResponder<ComboBox, Plugin.UserPropertiesKey>{
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

		String[] split;

		for(Plugin.UserPropertiesKey ppk : Plugin.UserPropertiesKey.values()){
			if(ppk.possibleVals == null || ppk.possibleVals.length == 0){
				continue;
			}
			spacer = new JLabel(" ");
			lblKey = new JLabel(ppk.keyName);

			panel.add(lblKey, cons);
			cons.gridy++;
			split = ppk.doc.split("\n");
			for(int j = 0; j < split.length || split.length == 0 && j == 0; j++){
				lblDoc = new JLabel((split.length != 0 ? split[j] : ppk.doc));
				lblDoc.setForeground(JBColor.GREEN);
				panel.add(lblDoc, cons);
				cons.gridy++;
			}

			cb = new ComboBox();
			cb.addItem(ppk.defaultValue);
			for(i = 0; i < ppk.possibleVals.length; i++){
				cb.addItem(ppk.possibleVals[i]);
			}

			cb.setSelectedItem(Plugin.pluginProps.getPluginProperty(ppk));
			cb.addActionListener(new ActionListenerWrapper<ComboBox, Plugin.UserPropertiesKey>(this, cb, ppk));

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
		KVPair<Plugin.UserPropertiesKey, String> pop;
		boolean madeChanges = changes.size() > 0;
		Iterator<KVPair> iter = changes.iterator();
		boolean good;
		while(iter.hasNext()){
			pop = changes.removeFirst();
			Plugin.pluginProps.overridePluginProps(pop.key, pop.value);

		}
		good = Plugin.pluginProps.savePluginPropsToFile();
		if(!good){
			DialogBuilder alert = new DialogBuilder();
			alert.addCloseButton();
			alert.setTitle("Error");
			alert.centerPanel(new JLabel("An error occurred when saving the updated properties to file. Try restarting Intellij."));
			alert.show();
			return;
		}
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
	public void actionPerformed(ComboBox cb, Plugin.UserPropertiesKey key) {
		changes.add(new KVPair<Plugin.UserPropertiesKey, String>(key, (String)cb.getSelectedItem()));
	}
}
