package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Kayler
 * @since 12/07/2017
 */
public class LaunchArmaDialogCreatorAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		//todo this doesn't work
//		try {
//			IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("com.kaylerrenslow.plugin.armaplugin.id"));
//			if(plugin == null){
//				throw new IllegalStateException("couldn't find Arma IntelliJ Plugin despite running it's code! :O");
//			}
//			String adcJarDir = plugin.getPath() + "/lib/";
//			Process process = Runtime.getRuntime().exec("java -jar adc.jar", new String[]{}, new File(adcJarDir));
//			process.waitFor();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
