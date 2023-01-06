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

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.Renderable;
import com.mbrlabs.mundus.commons.scene3d.traversal.DepthFirstIterator;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class GameObject extends SimpleNode<GameObject> implements Iterable<GameObject>, Renderable {

    public static final String DEFAULT_NAME = "GameObject";

    public String name;
    private boolean active;
    private final List<String> tags = new ArrayList<>();
    private final List<Component> components = new ArrayList<>();

    public GameObject() {
        this(-1);
    }

    public GameObject(int id) {
        this(DEFAULT_NAME, id);
    }

    /**
     * @param name game object name; can be null
     * @param id   game object id
     */
    public GameObject(String name, int id) {
        super(id);
        this.name = (name == null) ? DEFAULT_NAME : name;
        this.active = true;
    }

    /**
     * Make copy with existing gameObject and new id
     *
     * @param gameObject game object for clone
     */
    public GameObject(GameObject gameObject, int id) {
        super(gameObject, id);

        // set name _copy
        this.name = gameObject.name + "_copy";
        this.active = gameObject.active;

        if (gameObject.tags.size() > 0) {
            this.tags.addAll(gameObject.tags);
        }

        // copy components
        for (Component c : gameObject.components) {
            this.components.add(c.clone(this));
        }
        setParent(gameObject.parent);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        if (active) {
            for (Component component : this.components) {
                component.render(batch, environment, shaders, delta);
            }

            if (getChildren() != null) {
                for (GameObject node : getChildren()) {
                    node.render(batch, environment, shaders, delta);
                }
            }
        }
    }

    /**
     * Calls the update() method for each component in this and all child nodes.
     *
     * @param delta time since last update
     */
    public void update(float delta) {
        if (active) {
            for (Component component : this.components) {
                component.update(delta);
            }

            if (getChildren() != null) {
                for (GameObject node : getChildren()) {
                    node.update(delta);
                }
            }
        }
    }

    /**
     * Returns the tags
     *
     * @return tags or null if none available
     */
    public List<String> getTags() {
        return this.tags;
    }

    /**
     * Adds a tag.
     *
     * @param tag tag to add
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Finds all component by a given type.
     *
     * @param out             output array
     * @param type            component type
     * @param includeChildren search in node children of this game object as well?
     */
    public void findComponentsByType(List<Component> out, Component.Type type, boolean includeChildren) {
        if (includeChildren) {
            for (GameObject go : this) {
                for (Component c : go.components) {
                    if (c.getType() == type) {
                        out.add(c);
                    }
                }
            }
        } else {
            for (Component c : components) {
                if (c.getType() == type) {
                    out.add(c);
                }
            }
        }
    }

    /**
     * Finds one component by type.
     *
     * @param type component type
     * @return component if found or null
     */
    public Component findComponentByType(Component.Type type) {
        for (Component c : components) {
            if (c.getType() == type) return c;
        }

        return null;
    }

    /**
     * Returns all components of this go.
     *
     * @return components
     */
    public List<Component> getComponents() {
        return this.components;
    }

    /**
     * Removes a component.
     *
     * @param component component to remove
     */
    public void removeComponent(Component component) {
        components.remove(component);
    }

    /**
     * Adds a component.
     *
     * @param component component to add
     * @throws InvalidComponentException
     */
    public void addComponent(Component component) {
        isComponentAddable(component);
        components.add(component);
    }

    /**
     * @param component
     * @throws InvalidComponentException
     */
    public void isComponentAddable(Component component) {
        // check for component of the same type
        for (Component c : components) {
            if (c.getType() == component.getType()) {
                throw new InvalidComponentException(
                        "One Game object can't have more then 1 component of type " + c.getType());
            }
        }
    }

    @Override
    public Iterator<GameObject> iterator() {
        return new DepthFirstIterator(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GameObject that = (GameObject) o;

        return new EqualsBuilder().append(active, that.active).append(name, that.name).append(tags, that.tags).append(components, that.components).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(active).append(tags).append(components).toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
