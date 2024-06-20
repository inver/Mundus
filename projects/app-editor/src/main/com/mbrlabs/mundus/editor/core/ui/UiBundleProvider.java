package com.mbrlabs.mundus.editor.core.ui;

import com.mbrlabs.mundus.editor.config.AppEnvironment;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import lombok.RequiredArgsConstructor;

import java.nio.file.Paths;

@RequiredArgsConstructor
public class UiBundleProvider {

    private AppEnvironment appEnvironment;

    public UiBundle loadBundle(String name) {
        var pluginsDir = appEnvironment.getPluginsDir();

        Paths.get(pluginsDir).forEach(path -> {
            if (path.toFile().isDirectory()) {
                return;
            }


        });
        return null;
    }
}
