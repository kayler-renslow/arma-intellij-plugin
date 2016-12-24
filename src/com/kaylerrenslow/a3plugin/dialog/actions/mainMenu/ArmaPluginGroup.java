package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.util.PluginUtil;

/**
 * @author Kayler
 * @since 04/30/2016
 */
public class ArmaPluginGroup extends DefaultActionGroup {
	@Override
	public void update(AnActionEvent e) {
		boolean isArmaPlugin = PluginUtil.moduleIsArmaType(DataKeys.MODULE.getData(e.getDataContext()));
		boolean showAllTime = Plugin.pluginProps.propertyIsTrue(Plugin.UserPropertiesKey.SHOW_MM_ARMA_PLUGIN);
		e.getPresentation().setVisible(isArmaPlugin || showAllTime);
	}
}
