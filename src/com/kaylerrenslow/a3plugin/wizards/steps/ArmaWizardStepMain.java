package com.kaylerrenslow.a3plugin.wizards.steps;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.kaylerrenslow.a3plugin.wizards.steps.swing.ArmaModuleWizardStepMainComponent;

import javax.swing.*;

/**
 * @author Kayler
 * @since 03/27/2016
 */
public class ArmaWizardStepMain extends ArmaModuleWizardStep {

	public ArmaWizardStepMain(WizardContext context, Disposable parentDisposable) {
		super(context, parentDisposable);
	}

	@Override
	public JComponent getComponent() {
		return new ArmaModuleWizardStepMainComponent(this);
	}

	@Override
	public void updateDataModel() {

	}
}
