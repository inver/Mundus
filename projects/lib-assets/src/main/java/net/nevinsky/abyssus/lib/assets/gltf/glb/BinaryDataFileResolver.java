package net.nevinsky.abyssus.lib.assets.gltf.glb;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.LittleEndianInputStream;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFImageDto;
import net.nevinsky.abyssus.lib.assets.gltf.texture.PixmapBinaryLoaderHack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BinaryDataFileResolver implements DataFileResolver {
    private final Map<Integer, ByteBuffer> bufferMap = new HashMap<>();

    @Override
    public void load(FileHandle file) {
        load(file.read());
    }

    public void load(byte[] bytes) {
        load(new ByteArrayInputStream(bytes));
    }

    public void load(InputStream stream) {
        load(new LittleEndianInputStream(stream));
    }

    public void load(LittleEndianInputStream stream) {
        try {
            loadInternal(stream);
        } catch (IOException e) {
            throw new GlTFException(e);
        }
    }

    private void loadInternal(LittleEndianInputStream stream) throws IOException {
        long magic = stream.readInt(); // & 0xFFFFFFFFL;
        if (magic != 0x46546C67) {
            throw new GlTFException("bad magic");
        }
        int version = stream.readInt();
        if (version != 2) {
            throw new GlTFException("bad version");
        }
        long length = stream.readInt();// & 0xFFFFFFFFL;

        byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
//        String jsonData = null;
        for (int i = 12; i < length; ) {
            int chunkLen = stream.readInt();
            int chunkType = stream.readInt();
            i += 8;            // chunkLen % 4;
            if (chunkType == 0x4E4F534A) {
                byte[] data = new byte[(int) chunkLen];
                stream.readFully(data, 0, chunkLen);
//                jsonData = new String(data);
            } else if (chunkType == 0x004E4942) {
                ByteBuffer bufferData = ByteBuffer.allocate(chunkLen);
                bufferData.order(ByteOrder.LITTLE_ENDIAN);
                int bytesToRead = chunkLen;
                int bytesRead;
                while (bytesToRead > 0
                        && (bytesRead = stream.read(buffer, 0, Math.min(buffer.length, bytesToRead))) != -1) {
                    bufferData.put(buffer, 0, bytesRead);
                    bytesToRead -= bytesRead;
                }
                if (bytesToRead > 0) {
                    throw new GlTFException("premature end of file");
                }
                bufferData.flip();
                bufferMap.put(bufferMap.size(), bufferData);
            } else {
                log.debug("skip buffer type " + chunkType);
//                Gdx.app.log(GLTFLoaderBase.TAG, "skip buffer type " + chunkType);
                if (chunkLen > 0) {
                    stream.skip(chunkLen);
                }
            }
            i += chunkLen;
        }

//        glModel = new Json().fromJson(GLTF.class, jsonData);
    }

    @Override
    public ByteBuffer getBuffer(int buffer) {
        return bufferMap.get(buffer);
    }

    @Override
    public Pixmap load(GlTFDto root, GlTFImageDto glImage) {
        if (glImage.getBufferView() != null) {
            var bufferView = root.getBufferViews().get(glImage.getBufferView());
            var buffer = bufferMap.get(bufferView.getBuffer());
            buffer.position(bufferView.getByteOffset());
            byte[] data = new byte[bufferView.getByteLength()];
            buffer.get(data);
            return PixmapBinaryLoaderHack.load(data, 0, data.length);
        } else {
            throw new GlTFException("GLB image should have bufferView");
        }
    }
}