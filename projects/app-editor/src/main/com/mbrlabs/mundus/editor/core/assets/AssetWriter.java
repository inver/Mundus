package com.mbrlabs.mundus.editor.core.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssetWriter {
    private final MetaService metaService;

    public void writeAsset(Asset<?> asset) {
        if (asset.getType() == AssetType.SHADER) {
            writeShader((ShaderAsset) asset);
            return;
        }

        throw new NotImplementedException("Not implemented");
    }

    protected void writeShader(ShaderAsset asset) {
        var meta = asset.getMeta();
        metaService.save(meta);

        save(meta.getFile().child(meta.getAdditional().getFragment()), asset.getFragmentShader());
        save(meta.getFile().child(meta.getAdditional().getVertex()), asset.getVertexShader());
    }

    private void save(FileHandle fileName, String content) {
        try (var fw = new FileWriter(fileName.file())) {
            IOUtils.write(content, fw);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }
}
