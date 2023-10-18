package com.mbrlabs.mundus.editor.core.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAsset;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxMeta;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetsStorage {

    private final AssetWriter assetWriter;
    private final ShaderAssetLoader shaderAssetLoader;
    private final EditorCtx ctx;

    public FileHandle getAssetsFolder() {
        return new FileHandle(ctx.getCurrent().getPath()).child("assets");
    }

    @SneakyThrows
    public ShaderAsset createShader() {
        var commonMeta = new Meta<ShaderMeta>();

        var path = getAssetsFolder().child("Shader_" + commonMeta.getUuid());
        FileUtils.forceMkdir(path.file());

        commonMeta.setFile(path);
        commonMeta.setType(AssetType.SHADER);
        commonMeta.setAdditional(new ShaderMeta());

        var asset = new ShaderAsset(commonMeta);
        asset.setFragmentShader("");
        asset.setVertexShader("");
        assetWriter.writeAsset(asset);

        return shaderAssetLoader.load(commonMeta);
    }

    @SneakyThrows
    public SkyboxAsset createSkybox() {
        var commonMeta = new Meta<SkyboxMeta>();

        var path = getAssetsFolder().child("Skybox_" + commonMeta.getUuid());
        FileUtils.forceMkdir(path.file());

        commonMeta.setFile(path);
        commonMeta.setType(AssetType.SKYBOX);
        commonMeta.setAdditional(new SkyboxMeta());

        var asset = new SkyboxAsset(commonMeta);
//        asset.setFragmentShader("");
//        asset.setVertexShader("");
        assetWriter.writeAsset(asset);

//        return shaderAssetLoader.load(commonMeta);
        return null;
    }
}
