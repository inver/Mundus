package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.editor.config.ui.TestOutline;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.outline.Outline;
import com.mbrlabs.mundus.editor.ui.modules.outline.OutlineTest;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

@ExtendWith(GdxTestRunner.class)
@ContextConfiguration(classes = {
        MapperConfig.class,
        CommonConfig.class,
        UiConfig.class,
        TestConfig.class
})
public abstract class BaseCtxTest {

    @Autowired
    private AppEnvironment appEnvironment;
    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private AppUi appUi;
    @Autowired
    private EditorCtx ctx;
    @Autowired
    private Outline outline;

    @BeforeEach
    public void init() {
        new File(appEnvironment.getHomeDir()).mkdirs();
        var defPrj = projectManager.createDefaultProject();
        if (defPrj != null) {
            projectManager.changeProject(defPrj);
        }
        ((TestOutline) outline).getGetSelectedItemCount().set(0);
    }

    @AfterEach
    @SneakyThrows
    public void clearUp() {
        FileUtils.forceDelete(new File(appEnvironment.getHomeDir()));
    }

    protected Event createEvent(Actor target, InputEvent.Type type) {
        var event = new InputEvent();
        event.setStage(appUi);
        event.setType(type);
        event.setTarget(target);
        return event;
    }

    protected void clickMenuItem(MenuItem item) {
        item.notify(createEvent(item, InputEvent.Type.touchDown), false);
        item.notify(createEvent(item, InputEvent.Type.touchUp), false);
    }
}
