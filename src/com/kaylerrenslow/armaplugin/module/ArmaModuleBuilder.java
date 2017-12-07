package com.kaylerrenslow.armaplugin.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 01/01/2016
 */
public class ArmaModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {

	private List<Pair<String, String>> sourcePaths;

	@Override
	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
		ContentEntry contentEntry = doAddContentEntry(rootModel);
		if (contentEntry != null) {
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
		if (sourcePaths != null) {
			this.sourcePaths = new ArrayList<>(sourcePaths);
		}
	}

	@Override
	public void addSourcePath(Pair<String, String> sourcePathInfo) {
		if (this.sourcePaths == null) {
			this.sourcePaths = new ArrayList<>();
		}
		this.sourcePaths.add(sourcePathInfo);
	}

	private static abstract class ArmaModuleWizardStep extends ModuleWizardStep {
		protected WizardContext context;
		protected Disposable parentDisposable;

		public ArmaModuleWizardStep(WizardContext context, Disposable parentDisposable) {
			this.context = context;
			this.parentDisposable = parentDisposable;
		}
	}

	private static class ArmaWizardStepMain extends ArmaModuleWizardStep {

		public ArmaWizardStepMain(WizardContext context, Disposable parentDisposable) {
			super(context, parentDisposable);
		}

		@Override
		public JComponent getComponent() {
			JPanel root = new JPanel();

			root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
			JLabel subHeader = new JLabel(ArmaPlugin.getPluginBundle().getString("arma-module.description"));

//			root.add(new JLabel(ArmaPluginIcons.ARMA_LOGO));
			root.add(subHeader);

			return root;
		}

		@Override
		public void updateDataModel() {

		}
	}
}