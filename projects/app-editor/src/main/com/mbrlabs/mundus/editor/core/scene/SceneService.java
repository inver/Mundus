package com.mbrlabs.mundus.editor.core.scene;

import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.function.Consumer;

@Service
public class SceneService {

    public HierarchyNode find(HierarchyNode root, int entityId) {
        if (root.getId() == entityId) {
            return root;
        }

        if (CollectionUtils.isEmpty(root.getChildren())) {
            return null;
        }

        for (var n : root.getChildren()) {
            var found = find(n, entityId);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public void traverseNode(HierarchyNode node, Consumer<HierarchyNode> consumer) {
        consumer.accept(node);

        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (var n : node.getChildren()) {
                traverseNode(n, consumer);
            }
        }
    }

    public boolean deleteNode(HierarchyNode root, int entityId, boolean copyChildrenToParent) {
        if (!CollectionUtils.isNotEmpty(root.getChildren())) {
            return false;
        }

        var arr = new ArrayList<>(root.getChildren());
        for (var n : arr) {
            if (n.getId() == entityId) {
                if (copyChildrenToParent) {
                    root.getChildren().addAll(n.getChildren());
                }
                root.getChildren().remove(n);
                return true;
            } else {
                var found = deleteNode(n, entityId, copyChildrenToParent);
                if (found) {
                    return true;
                }
            }
        }

        return false;
    }
}
