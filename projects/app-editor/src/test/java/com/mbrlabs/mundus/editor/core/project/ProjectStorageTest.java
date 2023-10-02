package com.mbrlabs.mundus.editor.core.project;


import com.artemis.World;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.config.AppEnvironment;
import com.mbrlabs.mundus.editor.config.MapperConfig;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectStorageTest {

    private static final String PROJECT_PATH = "/tmp/" + UUID.randomUUID() + "/ProjectStorageTest";

    private static final String HOME_PATH = "/tmp/" + UUID.randomUUID() + "_random_home";
    private final ObjectMapper mapper = new MapperConfig().mapper();
    private final AppEnvironment environment = new AppEnvironment() {
        @Override
        public String getHomeDir() {
            return HOME_PATH;
        }
    };

    private final ProjectStorage storage = new ProjectStorage(mapper, environment);

    @BeforeEach
    @SneakyThrows
    public void init() {
        storage.init();
    }

    @Test
    public void testSaveProject() {

        var project = new ProjectContext(new PerspectiveCamera());
        project.setPath(PROJECT_PATH);

        var testWorld = new World();
        var scene = new Scene(testWorld);
        scene.setName("ololo");
        project.setCurrentScene(scene);

        //create dirs for project if it doesn't exist
        new File(project.getPath() + "/").mkdirs();

        storage.saveProjectContext(project);

        var ref = new ProjectRef(PROJECT_PATH);

        var res = storage.loadProjectContext(ref);
        assertEquals(project.getPath(), res.getPath());
        assertEquals(project.getName(), res.getName());
        assertEquals("ololo", project.getActiveSceneName());
    }

    @Test
    public void testSerializeAndDeserializeRegistry() {
        var ref = new ProjectRef("alala");

        var registry = new Registry("/tmp/tmp");
        registry.setLastProject(ref);
        registry.getProjects().add(ref);

        storage.saveRegistry(registry);
        var input = storage.loadRegistry();
        assertEquals(1, input.getProjects().size());
        assertEquals(ref, input.getProjects().get(0));
        assertEquals(ref, input.getLastProject());
    }
}
