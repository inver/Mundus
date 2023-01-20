package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import org.apache.commons.lang3.NotImplementedException;

public class OutlineDragAndDrop extends DragAndDrop {
    private final VisTree<IdNode, Integer> treeWidget;
    private final DropListener listener;

    public OutlineDragAndDrop(VisTree<IdNode, Integer> treeWidget, DropListener listener) {
        this.treeWidget = treeWidget;
        this.listener = listener;

        initSource();
        initTarget();
    }

    private void initSource() {
        addSource(new DragAndDrop.Source(treeWidget) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                var node = treeWidget.getNodeAt(y);
                if (node == null) {
                    return null;
                }

                var res = new Payload();
                res.setObject(node);
                return res;
            }
        });
    }

    private void initTarget() {
        addTarget(new Target(treeWidget) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                var overNode = treeWidget.getNodeAt(y);
                if (overNode == null && treeWidget.getSelection().isEmpty()) {
                    return true;
                }
                if (overNode != null && !treeWidget.getSelection().contains(overNode)) {
                    treeWidget.getSelection().set(overNode);
                }
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                var newParent = treeWidget.getNodeAt(y);
                var node = (IdNode) payload.getObject();
                if (node == null) {
                    return;
                }
                var draggedGO = node.getValue();

                // check if a go is dragged in one of its children or itself
                if (newParent != null) {
                    var parentGO = newParent.getValue();
                    throw new NotImplementedException();
//                    if (parentGO.isChildOf(draggedGO)) {
//                        return;
//                    }
                }
                throw new NotImplementedException();
//                var oldParent = draggedGO.getParent();
//                // remove child from old parent
//                draggedGO.remove();
//
//                var draggedPos = new Vector3();
//                // add to new parent
//                if (newParent == null) {
//                    // recalculate position for root layer
//                    var newPos = draggedPos;
//                    draggedGO.getPosition(draggedPos);
//                    newPos = calculatePosition(oldParent, draggedPos);
//
//                    listener.movedToRoot(draggedGO);
//                    draggedGO.setLocalPosition(newPos.x, newPos.y, newPos.z);
//                } else {
//                    throw new NotImplementedException();
////                    draggedGO.getPosition(draggedPos);
////                    // recalculate position
////                    var parentPos = new Vector3();
//                    // World coorinates
////                    var parentGO = newParent.getValue();
////                    parentGO.getPosition(parentPos);
////                    draggedPos = calculatePosition(oldParent, draggedPos);
////
////                    // Local in releation to new parent
////                    var newPos = draggedPos.sub(parentPos);
////                    // add
////                    parentGO.addChild(draggedGO);
////                    draggedGO.setLocalPosition(newPos.x, newPos.y, newPos.z);
//                }

//                listener.updateTree();
            }

            private Vector3 calculatePosition(GameObject oldParent, Vector3 newPosition) {
                if (oldParent != null) {
                    // calculate oldParentPos + draggedPos
                    var oldParentPos = new Vector3();
                    oldParent.getPosition(oldParentPos);
                    return oldParentPos.add(newPosition);
                }
                return newPosition;
            }
        });
    }

    public interface DropListener {
        void movedToRoot(GameObject obj);

        void updateTree();
    }
}
