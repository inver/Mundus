package com.mbrlabs.mundus.editor.utils;

import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PickerColorEncoder {

    public static int decode(int rgba8888Code) {
        if ((rgba8888Code & 0x000000FF) != 255) {
            return -1;
        }

        int id = (rgba8888Code & 0xFF000000) >>> 24;
        id += ((rgba8888Code & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888Code & 0x0000FF00) >>> 8) * 256 * 256;

        return id;
    }

    /**
     * Encodes a game object id to a GameObjectIdAttribute with rgb channels.
     *
     * @param go game object, who's id must be encoded
     * @return the game object id, encoded as rgb values
     */
    public static PickerIDAttribute encodeRaypickColorId(int entityId) {
        PickerIDAttribute goIDa = new PickerIDAttribute();
        encodeRaypickColorId(entityId, goIDa);
        return goIDa;
    }

    /**
     * Encodes a id to a GameObjectIdAttribute with rgb channels.
     *
     * @param id  id
     * @param out encoded id as attribute
     */
    public static void encodeRaypickColorId(int id, PickerIDAttribute out) {
        out.r = id & 0x000000FF;
        out.g = (id & 0x0000FF00) >>> 8;
        out.b = (id & 0x00FF0000) >>> 16;
        out.a = 255;
    }
}
