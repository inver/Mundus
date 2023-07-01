package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.artemis.Aspect;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.config.ui.TestOutline;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

    @Disabled
    @Test
    public void testDeleteGroup() {
//        outline.getRcmDelete().getClickListener()
    }

    @Disabled
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
}
