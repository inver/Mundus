package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.core.ecs.component.ParentComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;

public class OutlineDragAndDropController extends DragAndDrop {
    private final VisTree<IdNode, Integer> tree;
    private final EditorCtx ctx;
    private final EventBus eventBus;

    public OutlineDragAndDropController(EditorCtx ctx, EventBus eventBus, VisTree<IdNode, Integer> tree) {
        this.tree = tree;
        this.ctx = ctx;
        this.eventBus = eventBus;

        addSource(new DragAndDrop.Source(tree) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                var node = tree.getNodeAt(y);
                return doDragStart(node);
            }
        });
        addTarget(new Target(tree) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                var node = tree.getNodeAt(y);
                doDrag(node);
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                var node = tree.getNodeAt(y);
                doDrop(payload, node);
            }
        });
    }

    Payload doDragStart(IdNode node) {
        if (node == null) {
            return null;
        }

        var res = new Payload();
        res.setObject(node);
        return res;
    }

    private void doDrag(IdNode overNode) {
        if (overNode == null && tree.getSelection().isEmpty()) {
            return;
        }
        if (overNode != null && !tree.getSelection().contains(overNode)) {
            tree.getSelection().set(overNode);
        }
    }

    void doDrop(Payload payload, IdNode newParentNode) {
        var draggedNode = (IdNode) payload.getObject();
        if (draggedNode == null || newParentNode == null || newParentNode.getValue() < 0) {
            return;
        }


        var parentMapper = ctx.getCurrentWorld().getMapper(ParentComponent.class);
        if (newParentNode.getValue() == parentMapper.get(draggedNode.getValue()).getParentEntityId()) {
            return;
        }

        var draggedParentComponent = parentMapper.get(draggedNode.getValue());
        draggedParentComponent.setParentEntityId(newParentNode.getValue());
        eventBus.post(new SceneGraphChangedEvent());
        eventBus.post(new EntitySelectedEvent(draggedNode.getValue()));
    }
}
