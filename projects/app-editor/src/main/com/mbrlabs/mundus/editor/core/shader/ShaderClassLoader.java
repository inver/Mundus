package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.Files;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
@Slf4j
public class ShaderClassLoader {

    /**
     * Creates groovy class for creation instance
     *
     * @param asset shader asset with class
     * @return class
     */
    public Class createShaderClass(ShaderAsset asset) {
        var classPath = asset.getMeta().getFile().child(asset.getMeta().getAdditional().getShaderClass());

        try (var loader = new GroovyClassLoader(this.getClass().getClassLoader())) {
            if (classPath.type() == Files.FileType.Classpath) {
                //todo may be use recompile method from loader?
                try (var is = getClass().getClassLoader().getResourceAsStream(classPath.file().getPath());
                     var br = new BufferedReader(new InputStreamReader(is))) {
                    var name = classPath.file().getName();

                    return loader.parseClass(br, name);
                }
            }
            return loader.parseClass(classPath.file());
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }
}
