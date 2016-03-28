package com.kaylerrenslow.a3plugin.wizards;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.a3plugin.wizards.steps.ArmaWizardStepMain;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 01/01/2016.
 */
public class ArmaModuleBuilder extends ModuleBuilder implements SourcePathsBuilder{

	private Project project;
	private List<Pair<String, String>> sourcePaths;


	@Override
	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
		setProject(rootModel.getProject());
		ContentEntry contentEntry = doAddContentEntry(rootModel);
		if(contentEntry != null){
			final List<Pair<String, String>> sourcePaths = getSourcePaths();

			if (sourcePaths != null) {
				for (final Pair<String, String> sourcePath : sourcePaths) {
					String first = sourcePath.first;
					new File(first).mkdirs();
					final VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first));
					if (sourceRoot != null) {
						contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
		return new ArmaWizardStepMain(context, parentDisposable);
	}

	private void setProject(Project p){
		this.project = p;
	}

	@Override
	public ModuleType getModuleType() {
		return ArmaModuleType.getInstance();
	}

	@Override
	public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {
		return this.sourcePaths;
	}

	@Override
	public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
		if(sourcePaths != null){
			this.sourcePaths = new ArrayList<>(sourcePaths);
		}

	}

	@Override
	public void addSourcePath(Pair<String, String> sourcePathInfo) {
		if(this.sourcePaths == null){
			this.sourcePaths = new ArrayList<>();
		}
		this.sourcePaths.add(sourcePathInfo);
	}
}
