package com.kaylerrenslow.a3plugin.components;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Kayler
 * @since 12/23/2016
 */
public class FileTemplateProjectComponent implements ProjectComponent {
    private Project project;

    public FileTemplateProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {
        FileTemplate fileTemplate = FileTemplateManager.getInstance(project).findInternalTemplate("SQF File");
        fileTemplate.setExtension("sqf");

        try {
            fileTemplate.setText(FileUtil.loadTextAndClose(new InputStreamReader(getClass().getResourceAsStream("/com/kaylerrenslow/a3plugin/fileTemplates/SQF File.ft"))));
        } catch (IOException e) {

        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ArmaPluginFileTemplateProjectComponent";
    }
}
