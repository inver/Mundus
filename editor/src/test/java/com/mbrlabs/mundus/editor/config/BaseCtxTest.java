package com.mbrlabs.mundus.editor.config;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

@RunWith(GdxTestRunner.class)
@ContextConfiguration(classes = {
        TestConfig.class
})
public abstract class BaseCtxTest {

    @Autowired
    private AppEnvironment appEnvironment;

    @Before
    public void init() {
        new File(appEnvironment.getHomeDir()).mkdirs();
    }
}
