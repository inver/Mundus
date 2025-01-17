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

package net.nevinsky.abyssus.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FlushablePool;
import net.nevinsky.abyssus.core.mesh.MeshPartBuilder;

/**
 * This class allows to reduce the static allocation needed for shape builders. It contains all the objects used
 * internally by shape builders.
 *
 * @author realitix, xoppa
 */
public class BaseShapeBuilder {
    /* Color */
    protected static final Color tmpColor0 = new Color();
    protected static final Color tmpColor1 = new Color();
    protected static final Color tmpColor2 = new Color();
    protected static final Color tmpColor3 = new Color();
    protected static final Color tmpColor4 = new Color();

    /* Vector3 */
    protected static final Vector3 tmpV0 = new Vector3();
    protected static final Vector3 tmpV1 = new Vector3();
    protected static final Vector3 tmpV2 = new Vector3();
    protected static final Vector3 tmpV3 = new Vector3();
    protected static final Vector3 tmpV4 = new Vector3();
    protected static final Vector3 tmpV5 = new Vector3();
    protected static final Vector3 tmpV6 = new Vector3();
    protected static final Vector3 tmpV7 = new Vector3();

    /* VertexInfo */
    protected static final MeshPartBuilder.VertexInfo vertTmp0 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp3 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp4 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp5 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp6 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp7 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp8 = new MeshPartBuilder.VertexInfo();

    /* Matrix4 */
    protected static final Matrix4 matTmp1 = new Matrix4();

    private static final FlushablePool<Vector3> vectorPool = new FlushablePool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };

    private static final FlushablePool<Matrix4> matrices4Pool = new FlushablePool<Matrix4>() {
        @Override
        protected Matrix4 newObject() {
            return new Matrix4();
        }
    };

    /**
     * Obtain a temporary {@link Vector3} object, must be free'd using {@link #freeAll()}.
     */
    protected static Vector3 obtainV3() {
        return vectorPool.obtain();
    }

    /**
     * Obtain a temporary {@link Matrix4} object, must be free'd using {@link #freeAll()}.
     */
    protected static Matrix4 obtainM4() {
        final Matrix4 result = matrices4Pool.obtain();
        return result;
    }

    /**
     * Free all objects obtained using one of the `obtainXX` methods.
     */
    protected static void freeAll() {
        vectorPool.flush();
        matrices4Pool.flush();
    }
}
