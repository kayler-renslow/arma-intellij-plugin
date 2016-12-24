package com.kaylerrenslow.a3plugin.project;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 * @since 03/31/2016
 */
public final class ArmaProjectDataManager {
	private ArrayList<ArmaModuleData> moduleDataList = new ArrayList<>();

	private static final ArmaProjectDataManager INSTANCE = new ArmaProjectDataManager();

	private ArmaProjectDataManager() {
	}

	public static ArmaProjectDataManager getInstance() {
		return INSTANCE;
	}


	public ArmaModuleData getDataForModule(@NotNull Module module) {
		for (ArmaModuleData moduleData : moduleDataList) {
			if (moduleData.getModule() == module) {
				return moduleData;
			}
		}
		ArmaModuleData newData = new ArmaModuleData(module);
		moduleDataList.add(newData);
		return newData;
	}

}
