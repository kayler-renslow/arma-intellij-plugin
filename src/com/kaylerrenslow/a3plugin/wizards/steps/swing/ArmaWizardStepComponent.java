package com.kaylerrenslow.a3plugin.wizards.steps.swing;

import com.kaylerrenslow.a3plugin.wizards.steps.ArmaModuleWizardStep;

import javax.swing.*;

/**
 * @author Kayler
 * @since 03/27/2016
 */
public abstract class ArmaWizardStepComponent extends JPanel {
	protected final ArmaModuleWizardStep wizardStep;

	public ArmaWizardStepComponent(ArmaModuleWizardStep wizardStep) {
		this.wizardStep = wizardStep;
		initialize();
	}

	abstract void initialize();
}
