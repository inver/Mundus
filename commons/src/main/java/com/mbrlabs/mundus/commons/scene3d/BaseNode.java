/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.scene3d;

import com.mbrlabs.mundus.commons.scene3d.traversal.DepthFirstIterator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-06-2016
 */
public abstract class BaseNode<T extends BaseNode<T>> implements Node<T> {

    @Getter
    protected final int id;

    protected List<T> children;
    protected T parent;

    public BaseNode(int id) {
        this.id = id;
    }

    @Override
    public void initChildrenArray() {
        this.children = new ArrayList<>();
    }

    @Override
    public void addChild(T child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParent((T) this);
    }

    @Override
    public boolean isChildOf(T other) {
        var iterator = new DepthFirstIterator<T>(other);
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<T> getChildren() {
        return this.children;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    @Override
    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public void remove() {
        if (parent != null) {
            parent.getChildren().remove((T) this);
            this.parent = null;
        }
    }

}
