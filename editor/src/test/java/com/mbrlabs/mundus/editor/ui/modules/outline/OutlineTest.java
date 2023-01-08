package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
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
        var event = new EntitySelectedEvent(-1);
        outline.onEntitySelected(event);
        //NPE not raised
        Assert.assertEquals(1, 1);
    }
}
