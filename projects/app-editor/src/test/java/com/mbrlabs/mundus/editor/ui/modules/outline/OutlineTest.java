package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.verification.Only;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutlineTest extends BaseCtxTest {

    @Autowired
    private Outline outline;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AppUi appUi;

    @Test
    public void testDeselect() {
        outline.onEntitySelected(-1);
        //NPE not raised
        assertEquals(1, 1);
    }

    @Test
    public void testClickDeleteWithoutSelect() {
        outline.getRcmDelete().notify(createEvent(outline.getRcmDelete(), InputEvent.Type.touchDown), false);
        outline.getRcmDelete().notify(createEvent(outline.getRcmDelete(), InputEvent.Type.touchUp), false);
        Mockito.verify(outline, Mockito.times(30)).getSelectedEntityId();
    }

    @Test
    public void testDeleteGroup() {
//        outline.getRcmDelete().getClickListener()
    }

    @Test
    public void testDeleteComplexObject() {

    }

    private Event createEvent(Actor target, InputEvent.Type type) {
        var event = new InputEvent();
        event.setStage(appUi);
        event.setType(type);
        event.setTarget(target);
        return event;
    }
}
