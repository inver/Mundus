package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.mbrlabs.mundus.editor.config.GdxTestRunner;
import com.mbrlabs.mundus.editor.config.TestConfig;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(GdxTestRunner.class)
@ContextConfiguration(classes = {
        TestConfig.class
})
public class OutlineTest {

    @Autowired
    private Outline outline;
    @Autowired
    private EventBus eventBus;

    @Test
    public void testDeselect() {
        var event = new GameObjectSelectedEvent(null);
        outline.onGameObjectSelected(event);
        //NPE not raised
        Assert.assertEquals(1, 1);
    }
}
