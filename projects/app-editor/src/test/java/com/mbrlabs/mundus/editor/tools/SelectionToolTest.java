package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Input;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectionToolTest {

    private SelectionTool selectionTool;
    private EditorCtx editorCtx;
    private EntityPicker entityPicker;
    private CommandHistory commandHistory;
    private EventBus eventBus;

    @BeforeEach
    public void setup() {
        editorCtx = mock(EditorCtx.class);

        var currentProjectMock = mock(ProjectContext.class);
        when(editorCtx.getCurrent()).thenReturn(currentProjectMock);
        when(currentProjectMock.getCurrentScene()).thenReturn(mock(Scene.class));

        entityPicker = mock(EntityPicker.class);
        commandHistory = mock(CommandHistory.class);
        eventBus = mock(EventBus.class);
        selectionTool = new SelectionTool(editorCtx, "shaderKey", entityPicker, commandHistory, eventBus);
    }

    @Test
    public void testEntitySelectedWhenDifferentEntityIdSelectedThenEntityIdUpdated() {
        selectionTool.entitySelected(1);
        verify(editorCtx, times(1)).selectEntity(1);
    }

    @Test
    public void testEntitySelectedWhenSameEntityIdSelectedThenEntityIdRemainsSame() {
        when(editorCtx.getSelectedEntityId()).thenReturn(1);
        selectionTool.entitySelected(1);
        verify(editorCtx, times(1)).selectEntity(eq(1));
    }

    @Test
    public void testEntitySelectedWhenNegativeEntityIdSelectedThenEntityIdUpdatedToNegativeOne() {
        selectionTool.entitySelected(-1);
        verify(editorCtx, times(1)).selectEntity(-1);
    }

    @Test
    public void testGetIconThenReturnSymbolIconPointer() {
        assertEquals(SymbolIcon.POINTER, selectionTool.getIcon());
    }

    @Test
    public void testTouchDownThenUpdatePressedXAndPressedY() {
        selectionTool.touchDown(100, 200, 0, Input.Buttons.LEFT);
        assertTrue(selectionTool.touchUp(100, 200, 0, Input.Buttons.LEFT));
    }

    @Test
    public void testTouchUpWhenButtonLeftAndScreenXAndYAreSameAsPressedXAndPressedYThenCallEntitySelectedOnce() {
        when(entityPicker.pick(any(), anyInt(), anyInt())).thenReturn(1);
        selectionTool.touchDown(100, 200, 0, Input.Buttons.LEFT);
        selectionTool.touchUp(100, 200, 0, Input.Buttons.LEFT);
        verify(eventBus, times(1)).post(any(EntitySelectedEvent.class));
    }

    @Test
    public void testTouchUpWhenButtonNotLeftOrScreenXAndYAreDifferentFromPressedXAndYThenDoNotCallEntitySelected() {
        selectionTool.touchDown(100, 200, 0, Input.Buttons.LEFT);
        selectionTool.touchUp(101, 200, 0, Input.Buttons.LEFT);
        verify(eventBus, times(0)).post(any(EntitySelectedEvent.class));
    }

    @Test
    public void testDisposeThenNoExceptionThrown() {
        selectionTool.dispose();
    }

    @Test
    public void testOnActivatedThenNoExceptionThrown() {
        selectionTool.onActivated();
    }

    @Test
    public void testOnDisabledThenSelectedEntityIdUpdatedToNegativeOne() {
        selectionTool.onDisabled();
        verify(editorCtx, times(1)).selectEntity(-1);
    }
}