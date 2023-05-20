package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.Files;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import groovy.lang.GroovyClassLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.ShaderHolder;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class ShaderClassLoader {
    /**
     * @param asset actual loaded shader asset
     * @return new instance of wrapper with full reloaded shader
     */
    @SneakyThrows
    public EditorShaderHolder reloadShader(ShaderAsset asset, ShaderHolder holder) {
        var classPath = asset.getMeta().getFile().child(asset.getMeta().getAdditional().getShaderClass());

        try (var loader = new GroovyClassLoader(this.getClass().getClassLoader())) {
            if (holder instanceof EditorShaderHolder) {
                try {
                    ((EditorShaderHolder) holder).getShaderClassLoader().close();
                } catch (Exception e) {
                    log.error("ERROR", e);
                }
            }

            Class<?> clazz;
            if (classPath.type() == Files.FileType.Classpath) {
                //todo may be use recompile method from loader?
                clazz = loader.parseClass(new File(
                        getClass().getClassLoader().getResource(classPath.file().getPath()).getFile()
                ));
            } else {
                clazz = loader.parseClass(classPath.file());
            }


            var constructor = clazz.getDeclaredConstructor(String.class, String.class);
            var shader = (BaseShader) constructor.newInstance(asset.getVertexShader(), asset.getFragmentShader());
            log.info("Loaded shader for {}", asset.getName());

            return new EditorShaderHolder(loader, shader);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }
}
