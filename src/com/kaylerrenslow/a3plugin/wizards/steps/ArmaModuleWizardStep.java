package com.kaylerrenslow.a3plugin.wizards.steps;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;

/**
 * Created by Kayler on 03/27/2016.
 */
public abstract class ArmaModuleWizardStep extends ModuleWizardStep{
	protected WizardContext context;
	protected Disposable parentDisposable;

	public ArmaModuleWizardStep(WizardContext context, Disposable parentDisposable) {
		this.context = context;
		this.parentDisposable = parentDisposable;
	}
}
