package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.lang.header.ConfigClassNotDefinedException;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunctionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/08/2017
 */
public class ArmaPluginModuleData {
	private List<HeaderFile> configHeaderFiles = Collections.emptyList();
	private boolean reparseConfigHeaderFiles = true;
	private final Module module;
	private List<HeaderConfigFunction> functions = null;
	private XmlFile stringTableXmlFile;

	public ArmaPluginModuleData(@NotNull Module module) {
		this.module = module;
	}

	boolean shouldReparseConfigHeaderFiles() {
		return reparseConfigHeaderFiles;
	}

	void setReparseConfigHeaderFiles(boolean reparseConfigHeaderFiles) {
		this.reparseConfigHeaderFiles = reparseConfigHeaderFiles;
		if (reparseConfigHeaderFiles) {
			this.functions = null;
		}
	}

	/**
	 * Sets {@link #getConfigHeaderFiles()} and also loads all {@link #getAllConfigFunctions()} instances
	 *
	 * @param configHeaderFiles a read-only list of config header files
	 */
	void setConfigHeaderFiles(@NotNull List<HeaderFile> configHeaderFiles) {
		this.configHeaderFiles = configHeaderFiles;
		ArrayList<HeaderConfigFunction> functions = new ArrayList<>();
		for (HeaderFile file : configHeaderFiles) {
			try {
				functions.addAll(HeaderConfigFunctionUtil.getAllConfigFunctionsFromRootConfig(HeaderConfigFunctionUtil.getCfgFunctions(file)));
			} catch (ConfigClassNotDefinedException e) {
				e.printStackTrace();
			}
		}
		this.functions = Collections.unmodifiableList(functions);
	}

	/**
	 * Get a list of preprocessed and parsed config files. If the list is empty, there is no description.ext (for missions)
	 * file or config.cpp files (for addons). If the list is non-empty, the list will be a singleton with the description.ext
	 * file, or it will contain multiple, at least 1, config.cpp files (for addons).
	 *
	 * @return a read only list of config files
	 */
	@NotNull
	public List<HeaderFile> getConfigHeaderFiles() {
		return configHeaderFiles;
	}

	/**
	 * @return the Module for which this data is relevant for
	 */
	@NotNull
	public Module getModule() {
		return module;
	}

	/**
	 * @return a read-only list of all config functions.
	 */
	@Nullable
	public List<HeaderConfigFunction> getAllConfigFunctions() {
		return functions;
	}

	@Nullable
	public XmlFile getStringTableXmlFile() {
		return stringTableXmlFile;
	}

	public void setStringTableXmlFile(@Nullable XmlFile stringTableXmlFile) {
		this.stringTableXmlFile = stringTableXmlFile;
	}
}
