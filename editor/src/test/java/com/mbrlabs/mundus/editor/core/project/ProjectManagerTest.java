package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ProjectManagerTest extends BaseCtxTest {
    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/mundusProject";
    @Autowired
    private ProjectManager projectManager;

    @Test
    public void testCreateProject() {
        var res = projectManager.createProject(PROJECT_PATH);
        assertNotNull(res);
        assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @SneakyThrows
    @Test
    public void testLoadDoesNotExistProject() {
        var ref = new ProjectRef(PROJECT_PATH);

        var res = projectManager.loadProject(ref);
        assertNotNull(res);
        assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @SneakyThrows
    @Test
    public void loadProject() {
        var path = "src/test/resources/testProject";
        var target = "/tmp/" + UUID.randomUUID() + "/" + path;
        FileUtils.copyDirectory(new File(path), new File(target));

        var ref = new ProjectRef(target);

        var project = projectManager.loadProject(ref);
        assertNotNull(project);
        assertEquals(ref.getPath(), project.getPath());
        assertEquals(1, project.getProjectAssets().size());
    }
}
