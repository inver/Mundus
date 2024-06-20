/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.nevinsky.abyssus.core.model;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Returned by a {@link ModelLoader}, contains meshes, materials, nodes and animations. OpenGL resources like textures
 * or vertex buffer objects are not stored. Instead, a ModelData instance needs to be converted to a Model first.
 *
 * @author badlogic
 */
//todo remove this class and converts to Model
public class ModelData {
    public String id;
    public final int[] version = new int[2];
    public final List<ModelMesh> meshes = new ArrayList<>();
    public final List<ModelMaterial> materials = new ArrayList<>();
    public final List<ModelNode> nodes = new ArrayList<>();
    public final List<ModelAnimation> animations = new ArrayList<>();

    public void addMesh(ModelMesh mesh) {
        for (ModelMesh other : meshes) {
            if (other.id.equals(mesh.id)) {
                throw new GdxRuntimeException("Mesh with id '" + other.id + "' already in model");
            }
        }
        meshes.add(mesh);
    }
}
