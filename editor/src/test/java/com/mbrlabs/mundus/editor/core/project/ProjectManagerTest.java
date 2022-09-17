package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


public class ProjectManagerTest extends BaseCtxTest {
    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/mundusProject";

    @Autowired
    private ProjectManager projectManager;

    @Test
    public void testCreateProject() {
        var res = projectManager.createProject("testProject", PROJECT_PATH);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @SneakyThrows
    @Test
    public void testLoadDoesntExistProject() {
        var ref = new ProjectRef();
        ref.setName("missedProject");
        ref.setPath(PROJECT_PATH);

        var res = projectManager.loadProject(ref);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getCurrentScene().getEnvironment().getAmbientLight());
    }

    @Test
    public void testRenderScene() {
        var project = projectManager.createProject("ololo", PROJECT_PATH);

    }
}
