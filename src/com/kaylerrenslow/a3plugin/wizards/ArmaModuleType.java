package com.kaylerrenslow.a3plugin.wizards;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Kayler
 * @since 01/01/2016
 */
public class ArmaModuleType extends ModuleType<ArmaModuleBuilder> {
	public static final String ID = "arma.moduleType";
	private static final String NAME = Plugin.resources.getString("plugin.module.name");

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
		return NAME;
	}

	@NotNull
	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Icon getBigIcon() {
		return PluginIcons.ICON_ARMA3_FILE;
	}

	@Override
	public Icon getNodeIcon(@Deprecated boolean isOpened) {
		return PluginIcons.ICON_ARMA3_FILE;
	}
}
