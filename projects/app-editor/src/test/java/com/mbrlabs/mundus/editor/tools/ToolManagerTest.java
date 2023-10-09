package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.InputManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ToolManagerTest {

    private static ToolManager manager;
    private static InputManager inputManager;

    @BeforeAll
    public static void init() {
        var ctx = mock(EditorCtx.class);
        Gdx.input = mock(Input.class);
        Gdx.files = mock(Files.class);

        inputManager = new InputManager();
        manager = new ToolManager(ctx, null, mock(EventBus.class), inputManager, null, null,
                null, null, mock(CommandHistory.class), null, null);
    }

    @Test
    public void testActivateTool() {
        manager.activateTool(manager.scaleTool);
        Assertions.assertTrue(inputManager.getProcessors().size > 0);
    }
}