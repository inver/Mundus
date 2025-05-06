package net.nevinsky.abyssus.lib.assets.gltf.texture;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import lombok.experimental.UtilityClass;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;

/**
 * Hack {@link Pixmap} loading from binary data via reflection in order to avoid GWT compilation issues.
 * <p>
 * TODO move this to texture loader
 */
@UtilityClass
public class PixmapBinaryLoaderHack {

    public static Pixmap load(byte[] encodedData, int offset, int len) {
        if (Gdx.app.getType() == ApplicationType.WebGL) {
            throw new GlTFException("load pixmap from bytes not supported for WebGL");
        } else {
            // call new Pixmap(encodedData, offset, len); via reflection to
            // avoid compilation error with GWT.
            try {
                return (Pixmap) ClassReflection.getConstructor(Pixmap.class, byte[].class, int.class, int.class)
                        .newInstance(encodedData, offset, len);
            } catch (ReflectionException e) {
                throw new GlTFException(e);
            }
        }
    }
}
