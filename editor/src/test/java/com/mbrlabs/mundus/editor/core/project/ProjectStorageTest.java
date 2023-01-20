package com.mbrlabs.mundus.editor.core.project;


import com.artemis.World;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;

public class ProjectStorageTest extends BaseCtxTest {

    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/ProjectStorageTest";

    @Autowired
    private ProjectStorage storage;

    @Test
    public void testSaveProject() {

        var project = new ProjectContext(111, new PerspectiveCamera());
        project.path = PROJECT_PATH;
        project.name = "alala";

        var testWorld = new World();
        var scene = new Scene(testWorld);
        scene.setName("ololo");
        project.setCurrentScene(scene);

        //create dirs for project if it doesn't exist
        new File(project.path + "/").mkdirs();

        storage.saveProjectContext(project);

        var ref = new ProjectRef();
        ref.setPath(PROJECT_PATH);

        var res = storage.loadProjectContext(ref);
        Assert.assertEquals(project.path, res.path);
        Assert.assertEquals(project.name, res.name);
        Assert.assertEquals("ololo", project.getActiveSceneName());
    }

    @Test
    public void testSerializeAndDeserializeRegistry() {
        var ref = new ProjectRef();
        ref.setPath("alala");
        ref.setName("ololo");

        var registry = new Registry("/tmp/tmp");
        registry.setLastProject(ref);
        registry.getProjects().add(ref);
        registry.getSettings().setFbxConvBinary("binary");

        storage.saveRegistry(registry);
        var input = storage.loadRegistry();
        Assert.assertEquals(1, input.getProjects().size());
        Assert.assertEquals(ref, input.getProjects().get(0));
        Assert.assertEquals(ref, input.getLastProject());
    }
}
