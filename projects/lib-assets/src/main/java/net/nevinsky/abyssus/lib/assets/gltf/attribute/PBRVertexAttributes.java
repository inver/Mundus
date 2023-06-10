package net.nevinsky.abyssus.lib.assets.gltf.attribute;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PBRVertexAttributes {
    // based on VertexAttributes maximum (biNormal = 256)
    public static final class Usage {
        public static final int PositionTarget = 512;
        public static final int NormalTarget = 1024;
        public static final int TangentTarget = 2048;
    }
}
