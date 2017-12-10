package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFileTextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * A {@link HeaderFileTextProvider} implementation that accepts {@link VirtualFile} instances
 *
 * @author Kayler
 * @since 12/09/2017
 */
public class VirtualFileHeaderFileTextProvider implements HeaderFileTextProvider {

	@NotNull
	private final VirtualFile virtualFile;
	@NotNull
	private final Project project;

	public VirtualFileHeaderFileTextProvider(@NotNull VirtualFile virtualFile, @NotNull Project project) {
		this.virtualFile = virtualFile;
		this.project = project;
	}

	@Override
	@NotNull
	public Scanner newTextScanner() throws IOException {
		PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
		if (file == null) {
			throw new FileNotFoundException("File " + virtualFile + " couldn't be found");
		}
		return new Scanner(file.getText());
	}

	@Override
	@NotNull
	public String getFileName() {
		return virtualFile.getName();
	}

	@Override
	@NotNull
	public String getFilePath() {
		return virtualFile.getPath();
	}

	@Override
	public long getFileLength() {
		return virtualFile.getLength();
	}

	@Override
	@Nullable
	public HeaderFileTextProvider resolvePath(@NotNull String path) {
		VirtualFile resolvedFile;
		path = path.replaceAll("\\\\", "/");
		if (path.startsWith("/")) {
			Module module = ModuleUtil.findModuleForFile(this.virtualFile, project);
			if (module == null) {
				return null;
			}
			path = path.substring(1); //remove \
			VirtualFile rootDirectory;
			{
				VirtualFile imlFile = module.getModuleFile();
				if (imlFile == null) {
					return null;
				}
				rootDirectory = imlFile.getParent();
				if (rootDirectory == null) {
					return null;
				}
			}

			resolvedFile = rootDirectory.findFileByRelativePath(path);
		} else {
			VirtualFile virtualFile = this.virtualFile.getParent();
			if (virtualFile == null) {
				return null;
			}
			resolvedFile = virtualFile.findFileByRelativePath(path);
		}
		if (resolvedFile == null) {
			return null;
		}
		return new VirtualFileHeaderFileTextProvider(resolvedFile, project);
	}
}
