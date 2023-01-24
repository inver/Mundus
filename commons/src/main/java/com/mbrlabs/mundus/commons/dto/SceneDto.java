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

import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
public class SceneDto {
    private long id;
    private String name;
    private FogDTO fog;
    private String skyboxName;
    private BaseLightDto ambientLight;

    @Getter
    @Setter
    private Object ecs;

    @Getter
    @Setter
    private HierarchyNode rootNode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//
//    public float getCamPosX() {
//        return camPosX;
//    }
//
//    public void setCamPosX(float camPosX) {
//        this.camPosX = camPosX;
//    }
//
//    public float getCamPosY() {
//        return camPosY;
//    }
//
//    public void setCamPosY(float camPosY) {
//        this.camPosY = camPosY;
//    }
//
//    public float getCamPosZ() {
//        return camPosZ;
//    }
//
//    public void setCamPosZ(float camPosZ) {
//        this.camPosZ = camPosZ;
//    }
//
//    public float getCamDirX() {
//        return camDirX;
//    }
//
//    public void setCamDirX(float camDirX) {
//        this.camDirX = camDirX;
//    }
//
//    public float getCamDirY() {
//        return camDirY;
//    }
//
//    public void setCamDirY(float camDirY) {
//        this.camDirY = camDirY;
//    }
//
//    public float getCamDirZ() {
//        return camDirZ;
//    }
//
//    public void setCamDirZ(float camDirZ) {
//        this.camDirZ = camDirZ;
//    }

    public FogDTO getFog() {
        return fog;
    }

    public void setFog(FogDTO fog) {
        this.fog = fog;
    }

    public String getSkyboxName() {
        return skyboxName;
    }

    public void setSkyboxName(String skyboxName) {
        this.skyboxName = skyboxName;
    }

    public BaseLightDto getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLightDto ambientLight) {
        this.ambientLight = ambientLight;
    }

}
