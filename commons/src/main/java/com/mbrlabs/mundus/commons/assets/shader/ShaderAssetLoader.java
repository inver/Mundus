package com.mbrlabs.mundus.commons.assets.shader;

import com.badlogic.gdx.Files;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class ShaderAssetLoader implements AssetLoader<ShaderAsset, ShaderMeta> {
    @Override
    public ShaderAsset load(Meta<ShaderMeta> meta) {
        var res = new ShaderAsset(meta);
        if (meta.getFile().type() == Files.FileType.Classpath) {
            try (
                    var vis = getClass().getClassLoader().getResourceAsStream(
                            meta.getFile().child(meta.getAdditional().getVertex()).path());
                    var fis = getClass().getClassLoader().getResourceAsStream(
                            meta.getFile().child(meta.getAdditional().getFragment()).path());
            ) {
                res.setVertexShader(String.join("\n", IOUtils.readLines(vis, Charset.defaultCharset())));
                res.setFragmentShader(String.join("\n", IOUtils.readLines(fis, Charset.defaultCharset())));
            } catch (Exception e) {
                throw new RuntimeException("Fail to load shader files", e);
            }
        } else {
            try (
                    var vfr = new FileReader(meta.getFile().child(meta.getAdditional().getVertex()).path());
                    var ffr = new FileReader(meta.getFile().child(meta.getAdditional().getFragment()).path());
            ) {
                res.setVertexShader(String.join("\n", IOUtils.readLines(vfr)));
                res.setFragmentShader(String.join("\n", IOUtils.readLines(ffr)));
            } catch (Exception e) {
                throw new RuntimeException("Fail to load shader files", e);
            }
        }
        return res;
    }
}
