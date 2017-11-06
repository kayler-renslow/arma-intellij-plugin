package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * Used for saving project settings to file, with IntelliJ doing all the saving work.
 *
 * @author Kayler
 * @since 11/05/2017
 */
@State(name = "ArmaPluginProjectSettings", storages = @Storage("armaPluginUserSettings.xml"), reloadable = false)
public class ArmaPluginProjectSettings implements PersistentStateComponent<ArmaPluginProjectSettings.State> {
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
	public static ArmaPluginProjectSettings getInstance() {
		return ServiceManager.getService(ArmaPluginProjectSettings.class);
	}

	//NOTE: DO NOT CHANGE ANY FIELD NAMES OR CLASS NAMES. State saving and loading is done with serialization with reflection.
	public static class State {

	}
}
