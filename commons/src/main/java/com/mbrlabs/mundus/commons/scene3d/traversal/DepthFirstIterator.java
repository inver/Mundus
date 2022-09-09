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

package com.mbrlabs.mundus.commons.scene3d.traversal;

import com.mbrlabs.mundus.commons.scene3d.Node;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author Marcus Brummer
 * @version 20-01-2016
 */
public class DepthFirstIterator<T extends Node<T>> implements Iterator<Node<T>> {

    private final Stack<Node<T>> stack = new Stack<>();

    public DepthFirstIterator(Node<T> root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Node<T> next() {
        var top = stack.pop();
        if (top.getChildren() != null) {
            for (var child : top.getChildren()) {
                stack.push(child);
            }
        }

        return top;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
