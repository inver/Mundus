package com.mbrlabs.mundus.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import org.junit.Test;

public class ProjectStorageTest {

    private final ProjectStorage projectStorage = new ProjectStorage(new ObjectMapper());

    @Test
    public void saveCtxTest() {
        var project = new ProjectContext(0);

    }


}
