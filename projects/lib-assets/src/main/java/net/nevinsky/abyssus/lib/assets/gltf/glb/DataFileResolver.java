package net.nevinsky.abyssus.lib.assets.gltf.glb;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFImageDto;

import java.nio.ByteBuffer;

public interface DataFileResolver {
    void load(FileHandle file);

    ByteBuffer getBuffer(int buffer);

    Pixmap load(GlTFDto root, GlTFImageDto glImage);
}
