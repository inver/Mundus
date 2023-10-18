package com.mbrlabs.mundus.editor.tools;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.input.InputService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ToolManagerTest extends BaseCtxTest {

    @Autowired
    private ToolManager manager;
    @Autowired
    private InputService inputService;


//    @BeforeAll
//    public void init() {
//        super.init();
//        inputManager = new InputManager();
//        manager = new ToolManager(ctx, null, mock(EventBus.class), inputManager, null, null,
//                null, null, mock(CommandHistory.class), null, null);
//    }

    @Test
    public void testActivateTool() {
        inputService.activateTool(manager.scaleTool);
        Assertions.assertTrue(inputService.getProcessors().size > 0);
    }
}