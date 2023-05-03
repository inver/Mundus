package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;


public class ProjectManagerTest extends BaseCtxTest {
    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/mundusProject";
    @Autowired
    private ProjectManager projectManager;

    @Test
    public void testCreateProject() {
        var res = projectManager.createProject(PROJECT_PATH);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @SneakyThrows
    @Test
    public void testLoadDoesNotExistProject() {
        var ref = new ProjectRef(PROJECT_PATH);

        var res = projectManager.loadProject(ref);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @SneakyThrows
    @Test
    public void loadProject() {
        var path = "src/test/resources/testProject";
        var target = "/tmp/" + UUID.randomUUID() + "/" + path;
        FileUtils.copyDirectory(new File(path), new File(target));

        var ref = new ProjectRef(target);

        var project = projectManager.loadProject(ref);
        Assert.assertNotNull(project);
        Assert.assertEquals(ref.getPath(), project.getPath());
        Assert.assertEquals(1, project.getProjectAssets().size());
    }
}
