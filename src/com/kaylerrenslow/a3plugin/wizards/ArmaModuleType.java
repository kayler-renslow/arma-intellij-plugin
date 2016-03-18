package com.kaylerrenslow.a3plugin.wizards;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Kayler on 01/01/2016.
 */
public class ArmaModuleType extends ModuleType<ArmaModuleBuilder>{
	public static final String ID = "arma.moduleType";
	private static final String NAME = Plugin.resources.getString("plugin.module.arma.name");
	private static final String DESCRIPTION = Plugin.resources.getString("plugin.module.arma.description");

	public ArmaModuleType() {
		super(ID);
	}

	public static ArmaModuleType getInstance(){
		return (ArmaModuleType) ModuleTypeManager.getInstance().findByID(ID);
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
		return DESCRIPTION;
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
