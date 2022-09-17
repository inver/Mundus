package com.mbrlabs.mundus.editor.core.project;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

public class ProjectStorageTest {

    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/ProjectStorageTest";

    private final ProjectStorage storage = new ProjectStorage(new ObjectMapper());

    @Test
    public void testSaveProject() {
        var project = new ProjectContext(111);
        project.path = PROJECT_PATH;
        project.name = "alala";

        var scene = new Scene();
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
}
