package net.nevinsky.abyssus.lib.assets.gltf.glb;

import com.badlogic.gdx.utils.LittleEndianInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFBufferViewDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GlTFBinary {

    private final Map<Integer, ByteBuffer> bufferMap = new HashMap<>();

    public GlTFBinary(GlTFDto root, File file) throws FileNotFoundException {
        this(root, new FileInputStream(file));
    }

    public GlTFBinary(GlTFDto root, InputStream is) {
        try (var leis = new LittleEndianInputStream(is)) {
            loadInternal(root, leis);
        } catch (IOException e) {
            throw new GlTFException(e);
        }
    }

    private void loadInternal(GlTFDto root, LittleEndianInputStream stream) throws IOException {
        //1MB buffer
        var buffer = new byte[1024 * 1024];

        for (var bufferDto : root.getBuffers()) {
            var bufferData = ByteBuffer.allocate(bufferDto.getByteLength());
            bufferData.order(ByteOrder.LITTLE_ENDIAN);
            int bytesToRead = bufferDto.getByteLength();
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
        }
    }

    public float[] readBufferFloat(GlTFDto root, int accessorID) {
        var accessor = root.getAccessors().get(accessorID);
        var bufferView = root.getBufferViews().get(accessor.getBufferView());
        var bytes = bufferMap.get(bufferView.getBuffer());
        bytes.position(bufferView.getByteOffset() + accessor.getByteOffset());

        var data = new float[accessor.getSize() / 4];

        int nbFloatsPerVertex = accessor.getTypeSize();
        int nbBytesToSkip = 0;
        if (bufferView.getByteStride() != null) {
            nbBytesToSkip = bufferView.getByteStride() - nbFloatsPerVertex * 4;
        }
        if (nbBytesToSkip == 0) {
            bytes.asFloatBuffer().get(data);
            return data;
        }

        for (int i = 0; i < accessor.getCount(); i++) {
            for (int j = 0; j < nbFloatsPerVertex; j++) {
                data[i * nbFloatsPerVertex + j] = bytes.getFloat();
            }
            // skip remaining bytes
            bytes.position(bytes.position() + nbBytesToSkip);
        }

        return data;
    }

    public int[] readBufferUByte(GlTFDto root, int accessorID) {
        var accessor = root.getAccessors().get(accessorID);
        var bufferView = root.getBufferViews().get(accessor.getBufferView());
        var bytes = bufferMap.get(bufferView.getBuffer());
        bytes.position(bufferView.getByteOffset() + accessor.getByteOffset());

        var data = new int[accessor.getSize()];

        int nbBytesPerVertex = accessor.getTypeSize();
        int nbBytesToSkip = 0;
        if (bufferView.getByteStride() != null) {
            nbBytesToSkip = bufferView.getByteStride() - nbBytesPerVertex;
        }
        if (nbBytesToSkip == 0) {
            for (int i = 0; i < data.length; i++) {
                data[i] = bytes.get() & 0xFF;
            }
            return data;
        }

        for (int i = 0; i < accessor.getCount(); i++) {
            for (int j = 0; j < nbBytesPerVertex; j++) {
                data[i * nbBytesPerVertex + j] = bytes.get() & 0xFF;
            }
            // skip remaining bytes
            bytes.position(bytes.position() + nbBytesToSkip);
        }
        return data;
    }

    public int[] readBufferUShort(GlTFDto root, int accessorID) {
        var accessor = root.getAccessors().get(accessorID);
        var bufferView = root.getBufferViews().get(accessor.getBufferView());

        var bytes = bufferMap.get(bufferView.getBuffer());
        bytes.position(bufferView.getByteOffset() + accessor.getByteOffset());

        int[] data = new int[accessor.getSize() / 2];

        int nbShortsPerVertex = accessor.getTypeSize();
        int nbBytesToSkip = 0;
        if (bufferView.getByteStride() != null) {
            nbBytesToSkip = bufferView.getByteStride() - nbShortsPerVertex * 2;
        }
        if (nbBytesToSkip == 0) {
            ShortBuffer shorts = bytes.asShortBuffer();
            for (int i = 0; i < data.length; i++) {
                data[i] = shorts.get() & 0xFFFF;
            }
            return data;
        }
        for (int i = 0; i < accessor.getCount(); i++) {
            for (int j = 0; j < nbShortsPerVertex; j++) {
                data[i * nbShortsPerVertex + j] = bytes.getShort() & 0xFFFF;
            }
            // skip remaining bytes
            bytes.position(bytes.position() + nbBytesToSkip);
        }
        return data;
    }

    public float[] readBufferUShortAsFloat(GlTFDto root, int accessorID) {
        int[] intBuffer = readBufferUShort(root, accessorID);
        float[] floatBuffer = new float[intBuffer.length];
        for (int i = 0; i < intBuffer.length; i++) {
            floatBuffer[i] = intBuffer[i] / 65535f;
        }
        return floatBuffer;
    }

    public float[] readBufferUByteAsFloat(GlTFDto root, int accessorID) {
        int[] intBuffer = readBufferUByte(root, accessorID);
        float[] floatBuffer = new float[intBuffer.length];
        for (int i = 0; i < intBuffer.length; i++) {
            floatBuffer[i] = intBuffer[i] / 255f;
        }
        return floatBuffer;
    }

//    public FloatBuffer getBufferFloat(GlTFDto root, int accessorID) {
//        return getBufferFloat(root, glModel.accessors.get(accessorID));
//    }
//
//    public GLTFBufferView getBufferView(int bufferViewID) {
//        return glModel.bufferViews.get(bufferViewID);
//    }
//
//    public FloatBuffer getBufferFloat(GLTFAccessor glAccessor) {
//        return getBufferByte(glAccessor).asFloatBuffer();
//    }

    public FloatBuffer getBufferFloat(GlTFBufferViewDto bufferViewDto) {
        return getBufferByte(bufferViewDto).asFloatBuffer();
    }

    public IntBuffer getBufferInt(GlTFBufferViewDto bufferViewDto) {
        return getBufferByte(bufferViewDto).asIntBuffer();
    }

    public ShortBuffer getBufferShort(GlTFBufferViewDto bufferViewDto) {
        return getBufferByte(bufferViewDto).asShortBuffer();
    }

    public ByteBuffer getBufferByte(GlTFDto root, GlTFAccessorDto glAccessor) {
        var bufferView = root.getBufferViews().get(glAccessor.getBufferView());
        ByteBuffer bytes = bufferMap.get(bufferView.getBuffer());
        bytes.position(bufferView.getByteOffset() + glAccessor.getByteOffset());
        return bytes;
    }

    public ByteBuffer getBufferByte(GlTFBufferViewDto bufferView) {
        ByteBuffer bytes = bufferMap.get(bufferView.getBuffer());
        bytes.position(bufferView.getByteOffset());
        return bytes;
    }

    private static class ChunkHeader {
        private int length;
        private int type;
    }
}
