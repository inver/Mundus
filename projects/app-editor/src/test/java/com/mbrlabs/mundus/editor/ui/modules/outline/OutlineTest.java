package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.events.EventBus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutlineTest extends BaseCtxTest {

    @Autowired
    private Outline outline;
    @Autowired
    private EventBus eventBus;

    @Test
    public void testDeselect() {
        outline.onEntitySelected(-1);
        //NPE not raised
        assertEquals(1, 1);
    }
}
