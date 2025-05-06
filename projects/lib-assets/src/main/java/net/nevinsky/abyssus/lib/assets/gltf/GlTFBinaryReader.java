package net.nevinsky.abyssus.lib.assets.gltf;

import com.badlogic.gdx.utils.LittleEndianInputStream;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.glb.GlTFBinary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GlTFBinaryReader {

    public GlTFBinary readFile(GlTFDto root, File file) throws FileNotFoundException {
        return readFile(root, new FileInputStream(file));
    }

    public GlTFBinary readFile(GlTFDto root, InputStream is) {
        var res = new GlTFBinary();

        try (var leis = new LittleEndianInputStream(is)) {
            loadInternal(root, res, leis);
        } catch (IOException e) {
            throw new GlTFException(e);
        }

        return res;
    }

    private void loadInternal(GlTFDto root, GlTFBinary binary, LittleEndianInputStream stream) throws IOException {
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
            binary.addBuffer(bufferData);
        }
    }
}
