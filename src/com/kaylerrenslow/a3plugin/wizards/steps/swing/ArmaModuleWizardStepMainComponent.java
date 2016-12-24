package com.kaylerrenslow.a3plugin.wizards.steps.swing;

import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.wizards.steps.ArmaModuleWizardStep;

import javax.swing.*;

/**
 * Wizard step that is the first one seen when clicking the Arma mission module in the new module picker
 *
 * @author Kayler
 * @since 03/27/2016
 */
public class ArmaModuleWizardStepMainComponent extends ArmaWizardStepComponent {

	public ArmaModuleWizardStepMainComponent(ArmaModuleWizardStep wizardStep) {
		super(wizardStep);
	}

	@Override
	void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel subHeader = new JLabel(Plugin.resources.getString("plugin.module.description"));

		this.add(new JLabel(PluginIcons.ARMA_LOGO));
		this.add(subHeader);
	}

}
