package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.mbrlabs.mundus.commons.core.ecs.component.ParentComponent;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DragAndDropControllerTest extends BaseCtxTest {
    @Autowired
    private Outline outline;
    @Autowired
    private EditorCtx ctx;

    @Test
    public void testDoDrop() {
        clickMenuItem(outline.getRcmAddGroup());
        clickMenuItem(outline.getRcmAddGroup());
        clickMenuItem(outline.getRcmAddGroup());

        var entities = ctx.getCurrentWorld().getAspectSubscriptionManager().get(Aspect.all()).getEntities();

        var tree = outline.getTree();

        var payload = new DragAndDrop.Payload();
        payload.setObject(tree.findNode(entities.get(0)));

        outline.dragAndDrop.doDrop(payload, tree.findNode(entities.get(1)));

        var parentMapper = ctx.getCurrentWorld().getMapper(ParentComponent.class);
        Assertions.assertEquals(entities.get(1), parentMapper.get(entities.get(0)).getParentEntityId());
        Assertions.assertEquals(-1, parentMapper.get(entities.get(1)).getParentEntityId());
        Assertions.assertEquals(-1, parentMapper.get(entities.get(2)).getParentEntityId());
    }

    @Test
    public void testDoDragStart() {
        Assertions.assertNull(outline.dragAndDrop.doDragStart(null));

        var node = new IdNode(0, "", IdNode.Type.NONE);
        var payload = outline.dragAndDrop.doDragStart(node);
        Assertions.assertEquals(node, payload.getObject());
    }
}
