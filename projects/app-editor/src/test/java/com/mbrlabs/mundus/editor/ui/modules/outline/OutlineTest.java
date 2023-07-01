package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.config.ui.TestOutline;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutlineTest extends BaseCtxTest {

    @Autowired
    private Outline outline;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AppUi appUi;
    @Autowired
    private EditorCtx ctx;

    @Test
    public void testDeselect() {
        outline.onEntitySelected(-1);
        //NPE not raised
        assertEquals(1, 1);
    }

    @Test
    public void testClickDeleteWithoutSelect() {
        clickMenuItem(outline.getRcmDelete());

        Assertions.assertEquals(1, ((TestOutline) outline).getGetSelectedItemCount().get());
    }

    @Test
    public void testDeleteGroup() {
//        outline.getRcmDelete().getClickListener()
    }

    @Test
    public void testDeleteComplexObject() {

    }

    @Test
    public void testAddGroup() {
        var entitiesCount = ctx.getCurrentWorld().getAspectSubscriptionManager().get(Aspect.all()).getEntities().size();

        clickMenuItem(outline.getRcmAddGroup());

        var entitiesWithGroup = ctx.getCurrentWorld().getAspectSubscriptionManager().get(Aspect.all());
        Assertions.assertEquals(entitiesCount + 1, entitiesWithGroup.getEntities().size());
    }

    private void clickMenuItem(MenuItem item) {
        item.notify(createEvent(item, InputEvent.Type.touchDown), false);
        item.notify(createEvent(item, InputEvent.Type.touchUp), false);
    }

    private Event createEvent(Actor target, InputEvent.Type type) {
        var event = new InputEvent();
        event.setStage(appUi);
        event.setType(type);
        event.setTarget(target);
        return event;
    }
}
