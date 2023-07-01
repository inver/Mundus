package com.mbrlabs.mundus.editor.config;

import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

@ExtendWith(GdxTestRunner.class)
@ContextConfiguration(classes = {
        TestConfig.class
})
public abstract class BaseCtxTest {

    @Autowired
    private AppEnvironment appEnvironment;
    @Autowired
    private ProjectManager projectManager;

    @BeforeEach
    public void init() {
        new File(appEnvironment.getHomeDir()).mkdirs();
        var defPrj = projectManager.createDefaultProject();
        if (defPrj != null) {
            projectManager.changeProject(defPrj);
        }
    }
}
