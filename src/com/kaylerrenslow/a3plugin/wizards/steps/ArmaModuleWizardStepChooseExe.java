package com.kaylerrenslow.a3plugin.wizards.steps;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;

import javax.swing.*;

/**
 * @author Kayler
 * @since 03/27/2016
 */
public class ArmaModuleWizardStepChooseExe extends ArmaModuleWizardStep {
	public ArmaModuleWizardStepChooseExe(WizardContext context, Disposable parentDisposable) {
		super(context, parentDisposable);
	}

	@Override
	public JComponent getComponent() {
		return null;
	}

	@Override
	public void updateDataModel() {

	}
}
