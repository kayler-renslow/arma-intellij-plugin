package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used for saving application settings to file, with IntelliJ doing all the saving work.
 * @author Kayler
 * @since 10/25/2017
 */
@State(name = "ArmaPluginApplicationSettings", storages = @Storage("armaPluginUserSettings.xml"), reloadable = false)
public class ArmaPluginApplicationSettings implements PersistentStateComponent<ArmaPluginApplicationSettings.State> {
	private State myState = new State();

	@NotNull
	@Override
	public State getState() {
		return myState;
	}

	@Override
	public void loadState(State state) {
		this.myState = state;
	}

	@NotNull
	public static ArmaPluginApplicationSettings getInstance() {
		return ServiceManager.getService(ArmaPluginApplicationSettings.class);
	}

	//NOTE: DO NOT CHANGE ANY FIELD NAMES OR CLASS NAMES. State saving and loading is done with serialization with reflection.
	public static class State {
		@Nullable
		public String armaToolsDirectory;
	}
}
