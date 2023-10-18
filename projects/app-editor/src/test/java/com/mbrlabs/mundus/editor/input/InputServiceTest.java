package com.mbrlabs.mundus.editor.input;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.tools.SelectionTool;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;

public class InputServiceTest extends BaseCtxTest {

    @Autowired
    private InputService inputService;
    @Autowired
    private ToolManager toolManager;

    private final SelectionTool mockTool = mock(SelectionTool.class);
    private final SelectionTool mockTool2 = mock(SelectionTool.class);

    @Test
    void testActivateTool() {
        inputService.activateTool(mockTool);
        Assertions.assertEquals(mockTool, inputService.getProcessors().get(1));
        Assertions.assertEquals(toolManager, inputService.getProcessors().get(2));
        Assertions.assertEquals(3, inputService.getProcessors().size);

        inputService.activateTool(mockTool2);
        Assertions.assertEquals(mockTool2, inputService.getProcessors().get(1));
        Assertions.assertEquals(3, inputService.getProcessors().size);
    }

    @Test
    void testDeactivateTool() {
        inputService.activateTool(mockTool);
        inputService.activateTool(mockTool2);
        Assertions.assertEquals(3, inputService.getProcessors().size);

        inputService.deactivateTool();
        Assertions.assertEquals(toolManager, inputService.getProcessors().get(1));
        Assertions.assertEquals(2, inputService.getProcessors().size);
    }

    @Test
    void testActivateDefaultTool() {
        inputService.activateDefaultTool();
        Assertions.assertEquals(toolManager.getTranslateTool(), inputService.getProcessors().get(1));
        Assertions.assertEquals(toolManager, inputService.getProcessors().get(2));
        Assertions.assertEquals(3, inputService.getProcessors().size);
    }
}
