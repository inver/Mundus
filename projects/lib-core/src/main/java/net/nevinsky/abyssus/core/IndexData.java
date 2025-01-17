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

package net.nevinsky.abyssus.core;

import com.badlogic.gdx.utils.Disposable;

import java.nio.IntBuffer;

/**
 * An IndexData instance holds index data. Can be either a plain int buffer or an OpenGL buffer object.
 *
 * @author mzechner
 */
public interface IndexData extends Disposable {
    /**
     * @return the number of indices currently stored in this buffer
     */
    int getNumIndices();

    /**
     * @return the maximum number of indices this IndexBufferObject can store.
     */
    int getNumMaxIndices();

    /**
     * <p>
     * Sets the indices of this IndexBufferObject, discarding the old indices. The count must equal the number of
     * indices to be copied to this IndexBufferObject.
     * </p>
     *
     * <p>
     * This can be called in between calls to {@link #bind()} and {@link #unbind()}. The index data will be updated
     * instantly.
     * </p>
     *
     * @param indices the index data
     * @param offset  the offset to start copying the data from
     * @param count   the number of ints to copy
     */
    void setIndices(int[] indices, int offset, int count);

    /**
     * Copies the specified indices to the indices of this IndexBufferObject, discarding the old indices. Copying start
     * at the current {@link IntBuffer#position()} of the specified buffer and copied the
     * {@link IntBuffer#remaining()} amount of indices. This can be called in between calls to {@link #bind()} and
     * {@link #unbind()}. The index data will be updated instantly.
     *
     * @param indices the index data to copy
     */
    void setIndices(IntBuffer indices);

    /**
     * Update (a portion of) the indices.
     *
     * @param targetOffset offset in indices buffer
     * @param indices      the index data
     * @param offset       the offset to start copying the data from
     * @param count        the number of ints to copy
     */
    void updateIndices(int targetOffset, int[] indices, int offset, int count);

    /**
     * <p>
     * Returns the underlying IntBuffer. If you modify the buffer contents they wil be uploaded on the call to
     * {@link #bind()}. If you need immediate uploading use {@link #setIndices(int[], int, int)}.
     * </p>
     *
     * @return the underlying int buffer.
     */
    IntBuffer getBuffer();

    /**
     * Binds this IndexBufferObject for rendering with glDrawElements.
     */
    void bind();

    /**
     * Unbinds this IndexBufferObject.
     */
    void unbind();

    /**
     * Invalidates the IndexBufferObject so a new OpenGL buffer handle is created. Use this in case of a context loss.
     */
    void invalidate();

    /**
     * Disposes this IndexDatat and all its associated OpenGL resources.
     */
    void dispose();
}
