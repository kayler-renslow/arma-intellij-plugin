package com.kaylerrenslow.a3plugin.project;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.util.PluginUtil;

import java.util.ArrayList;

/**
 * Created by Kayler on 03/31/2016.
 */
public final class ArmaProjectDataManager {
	private ArrayList<ArmaModuleData> moduleDataList = new ArrayList<>();

	private static final ArmaProjectDataManager INSTANCE = new ArmaProjectDataManager();

	private ArmaProjectDataManager() {}

	public static ArmaProjectDataManager getInstance(){
		return INSTANCE;
	}


	public ArmaModuleData getDataForModule(Module module){
		for(ArmaModuleData moduleData : moduleDataList){
			if(moduleData.getModule() == module){
				return moduleData;
			}
		}
		ArmaModuleData newData = new ArmaModuleData(module);
		moduleDataList.add(newData);
		return newData;
	}

}
