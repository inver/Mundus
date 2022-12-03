/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tibor Zsuro
 * @version 21-08-2021
 */
public class GameObjectDto {

    private int id;
    private String name;
    private boolean active;

    private float[] transform = new float[10];

    private List<String> tags;
    private List<GameObjectDto> children;
    private List<ComponentDto> components = new ArrayList<>();
    private ModelComponentDto modelComponent;
    private TerrainComponentDto terrainComponent;

    public GameObjectDto() {
        children = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float[] getTransform() {
        return transform;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<GameObjectDto> getChildren() {
        return children;
    }

    public void setChildren(List<GameObjectDto> children) {
        this.children = children;
    }

    public ModelComponentDto getModelComponent() {
        return modelComponent;
    }

    public void setModelComponent(ModelComponentDto modelComponent) {
        this.modelComponent = modelComponent;
    }

    public TerrainComponentDto getTerrainComponent() {
        return terrainComponent;
    }

    public void setTerrainComponent(TerrainComponentDto terrainComponent) {
        this.terrainComponent = terrainComponent;
    }

    public List<ComponentDto> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentDto> components) {
        this.components = components;
    }
}
