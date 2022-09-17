package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OutlineTest extends BaseCtxTest {

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
