package com.mbrlabs.mundus.editor.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

@ExtendWith(GdxTestRunner.class)
//@Import({
//        BeanConfig.class
//})
@ContextConfiguration(classes = {
        TestConfig.class
})
public abstract class BaseCtxTest {

    @Autowired
    private AppEnvironment appEnvironment;

    @BeforeEach
    public void init() {
        new File(appEnvironment.getHomeDir()).mkdirs();
    }
}
