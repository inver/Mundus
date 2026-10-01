package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AITexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves texture paths reported by Assimp to files. File textures keep their relative sub folders; embedded
 * textures (paths like {@code *0}) are written once to the embedded output directory (cache by texture address).
 * Without an output directory embedded textures are skipped.
 */
@Slf4j
final class TextureProcessor {

    private final AIScene scene;
    private final String modelDir;
    private final FileHandle embeddedDir;
    private final Map<Long, String> extracted = new HashMap<>();

    TextureProcessor(AIScene scene, String modelDir, FileHandle embeddedDir) {
        this.scene = scene;
        this.modelDir = modelDir;
        this.embeddedDir = embeddedDir;
    }

    /** @return the file name for the {@code ModelTexture}, or {@code null} if the texture can't be used */
    String resolve(String assimpPath) {
        var relative = normalizePath(assimpPath);
        if (relative.isEmpty()) {
            return null;
        }
        var embedded = findEmbedded(assimpPath);
        if (embedded != null) {
            return extract(embedded);
        }
        if (relative.startsWith("*")) {
            log.warn("Embedded texture {} not found in scene", assimpPath);
            return null;
        }
        return modelDir.isEmpty() ? relative : modelDir + "/" + relative;
    }

    /** Embedded textures are referenced either by index ({@code *N}) or by their original file name. */
    private AITexture findEmbedded(String assimpPath) {
        int count = scene.mNumTextures();
        if (count == 0) {
            return null;
        }
        var textures = scene.mTextures();
        if (assimpPath.startsWith("*")) {
            try {
                int index = Integer.parseInt(assimpPath.substring(1));
                return index >= 0 && index < count ? AITexture.create(textures.get(index)) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        var wanted = fileName(assimpPath);
        for (int i = 0; i < count; i++) {
            var texture = AITexture.create(textures.get(i));
            var name = texture.mFilename().dataString();
            if (!name.isEmpty() && fileName(name).equals(wanted)) {
                return texture;
            }
        }
        return null;
    }

    private static String fileName(String path) {
        var p = normalizePath(path);
        return p.substring(p.lastIndexOf('/') + 1);
    }

    private String extract(AITexture texture) {
        if (embeddedDir == null) {
            log.warn("Embedded texture skipped: no output directory given");
            return null;
        }
        return extracted.computeIfAbsent(texture.address(), address -> {
            var name = "embedded_" + extracted.size();
            var file = texture.mHeight() == 0 ? writeCompressed(texture, name) : writeRaw(texture, name);
            return file.file().getPath().replace('\\', '/');
        });
    }

    private FileHandle writeCompressed(AITexture texture, String name) {
        var buffer = texture.pcDataCompressed();
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        var hint = texture.achFormatHintString();
        var file = embeddedDir.child(name + "." + (hint.isEmpty() ? "png" : hint));
        file.writeBytes(bytes, false);
        return file;
    }

    /** Raw textures are ARGB8888 texels; they are stored as PNG. */
    private FileHandle writeRaw(AITexture texture, String name) {
        int width = texture.mWidth();
        int height = texture.mHeight();
        var texels = texture.pcData();
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width * height; i++) {
            var t = texels.get(i);
            int argb = (t.a() & 0xff) << 24 | (t.r() & 0xff) << 16 | (t.g() & 0xff) << 8 | (t.b() & 0xff);
            image.setRGB(i % width, i / width, argb);
        }
        var file = embeddedDir.child(name + ".png");
        embeddedDir.mkdirs();
        try {
            ImageIO.write(image, "png", file.file());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    /** Keeps relative sub folders (e.g. {@code textures/a.png}) but normalizes separators. */
    static String normalizePath(String path) {
        var p = path.replace('\\', '/');
        while (p.startsWith("./")) {
            p = p.substring(2);
        }
        return p.replaceAll("/{2,}", "/");
    }
}
