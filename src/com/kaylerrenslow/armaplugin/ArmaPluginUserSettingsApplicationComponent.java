package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Kayler
 * @since 10/25/2017
 */
@State(name = "ArmaPluginUserSettings", storages = @Storage("armaPluginUserSettings.xml"), reloadable = false)
public class ArmaPluginUserSettingsApplicationComponent implements ApplicationComponent, PersistentStateComponent<ArmaPluginUserSettingsApplicationComponent.State> {
	private State currentState;

	@NotNull
	@Override
	public String getComponentName() {
		return "ArmaPluginUserSettingsApplicationComponent";
	}

	@Nullable
	@Override
	public State getState() {
		return currentState;
	}

	@Override
	public void loadState(State state) {
		System.out.println("ArmaPluginPersistentStateComponent.loadState ");
		if (state == null) {
			System.err.println("WARNING: ArmaPluginPersistentStateComponent.loadState: state was null");
			return;
		}
		this.currentState = state;
		if (state.armaToolsDirectory != null) {
			ArmaPluginUserData.getInstance().setArmaToolsDir(new File(state.armaToolsDirectory));
		}
	}


	//NOTE: DO NOT CHANGE ANY FIELD NAMES OR CLASS NAMES. State saving and loading is done with serialization with reflection.
	public static class State {
		public String armaToolsDirectory;
	}
}
