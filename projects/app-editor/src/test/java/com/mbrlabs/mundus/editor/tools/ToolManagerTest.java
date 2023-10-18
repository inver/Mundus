package com.mbrlabs.mundus.editor.tools;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ToolManagerTest extends BaseCtxTest {

    @Autowired
    private ToolManager manager;
    private final Tool nonSelectionTool = mock(Tool.class);
    private final SelectionTool mockTool = spy(mock(SelectionTool.class));

    @Test
    void testActivateTool() {
        manager.activateTool(manager.getTerrainBrushes().get(0));
        Assertions.assertEquals(manager.getTerrainBrushes().get(0), manager.getActiveTool());
    }

    @Test
    void testActivateToolWithKeepSelection() {
        manager.activateTool(nonSelectionTool);
        manager.activateTool(mockTool);
        manager.activateTool(mockTool);
        Assertions.assertEquals(mockTool, manager.getActiveTool());
        verify(mockTool, times(1)).entitySelected(anyInt());
    }

    @Test
    void testDeactivateTool() {
        manager.activateTool(mockTool);
        manager.deactivateTool();
        verify(mockTool, times(1)).onDisabled();
        Assertions.assertNull(manager.getActiveTool());

        manager.deactivateTool();
        verify(mockTool, times(1)).onDisabled();
    }

    @Test
    void testRender() {
        manager.activateTool(mockTool);
        manager.render(null, null, null, 0f);
        verify(mockTool, times(1)).render(any(), any(), any(), eq(0f));

        manager.deactivateTool();
        manager.render(null, null, null, 0f);
        verify(mockTool, times(1)).render(any(), any(), any(), eq(0f));
    }

    @Test
    void testAct() {
        manager.activateTool(mockTool);
        manager.act();
        verify(mockTool, times(1)).act();

        manager.deactivateTool();
        manager.act();
        verify(mockTool, times(1)).act();
    }
}