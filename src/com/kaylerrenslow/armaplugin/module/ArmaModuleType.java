package com.kaylerrenslow.armaplugin.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * @author Kayler
 * @since 12/06/2017
 */
public class ArmaModuleType extends ModuleType<ArmaModuleBuilder> {
	@NotNull
	public static final String ID = "arma.moduleType";

	public ArmaModuleType() {
		super(ID);
	}

	public static ArmaModuleType getInstance() {
		return (ArmaModuleType) ModuleTypeManager.getInstance().findByID(ID);
	}

	@NotNull
	@Override
	public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ArmaModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
		ModuleWizardStep[] steps = {};
		return steps;
	}

	@NotNull
	@Override
	public ArmaModuleBuilder createModuleBuilder() {
		return new ArmaModuleBuilder();
	}

	@NotNull
	@Override
	public String getName() {
		return "ArmA Addon/Mission Module";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Icon getIcon() {
		return ArmaPluginIcons.ICON_ARMA3_FILE;
	}

	@Override
	public Icon getNodeIcon(@Deprecated boolean isOpened) {
		return ArmaPluginIcons.ICON_ARMA3_FILE;
	}
}
