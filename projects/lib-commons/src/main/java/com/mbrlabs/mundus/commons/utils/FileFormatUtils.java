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

package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.files.FileHandle;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FileFormatUtils {

    public static final String FORMAT_3D_G3DB = "g3db";
    public static final String FORMAT_3D_GLTF = "gltf";
    public static final String FORMAT_3D_AC3D = "ac";
    public static final String FORMAT_3D_WAVEFONT = "obj";

    public static final String FORMAT_3D_G3DJ = "g3dj";
    public static final String FORMAT_3D_COLLADA = "dae";
    public static final String FORMAT_3D_FBX = "fbx";

    public static final String FORMAT_IMG_PNG = "png";
    public static final String FORMAT_IMG_JPG = "jpg";
    public static final String FORMAT_IMG_JPEG = "jpeg";
    public static final String FORMAT_IMG_TGA = "tga";


    public static String getFileExtension(FileHandle file) {
        var name = file.name();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static boolean isFBX(FileHandle file) {
        return getFileExtension(file).equals(FORMAT_3D_FBX);
    }

    public static boolean isCollada(FileHandle file) {
        return getFileExtension(file).equals(FORMAT_3D_COLLADA);
    }

    public static boolean isG3DB(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_G3DB);
    }

    public static boolean isG3DB(FileHandle file) {
        return isG3DB(file.name());
    }

    public static boolean isGLTF(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_GLTF);
    }

    public static boolean isGLTF(FileHandle file) {
        return isGLTF(file.name());
    }

    public static boolean isAC3D(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_AC3D);
    }

    public static boolean isAC3D(FileHandle file) {
        return isAC3D(file.name());
    }

    public static boolean isOBJ(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_WAVEFONT);
    }

    public static boolean isOBJ(FileHandle file) {
        return isOBJ(file.name());
    }
}
