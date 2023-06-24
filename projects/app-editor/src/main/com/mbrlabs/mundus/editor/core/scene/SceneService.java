package com.mbrlabs.mundus.editor.core.scene;

import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SceneService {
    public boolean deleteNode(HierarchyNode root, int entityId) {
        if (!CollectionUtils.isNotEmpty(root.getChildren())) {
            return false;
        }

        var arr = new ArrayList<>(root.getChildren());
        for (var n : arr) {
            if (n.getId() == entityId) {
                root.getChildren().addAll(n.getChildren());
                root.getChildren().remove(n);
                return true;
            } else {
                var found = deleteNode(n, entityId);
                if (found) {
                    return true;
                }
            }
        }

        return false;
    }
}
