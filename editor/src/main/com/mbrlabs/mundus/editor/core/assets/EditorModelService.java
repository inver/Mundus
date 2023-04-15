package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.model.ModelMeta;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.importExport.AssimpExporter;
import com.mbrlabs.mundus.commons.loader.AssimpModelLoader;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EditorModelService extends ModelService {

    private final MetaService metaService;
    private final AssetsStorage assetsStorage;
    private final ModelImporter modelImporter;
    private final ModelAssetLoader modelAssetLoader;
    private final EditorCtx ctx;
    private final AssimpExporter modelExporter;

    public EditorModelService(AssimpModelLoader assimpModelLoader, MetaService metaService, AssetsStorage assetsStorage,
                              ModelImporter modelImporter, ModelAssetLoader modelAssetLoader, EditorCtx ctx,
                              AssimpExporter modelExporter) {
        super(assimpModelLoader);
        this.metaService = metaService;
        this.assetsStorage = assetsStorage;
        this.modelImporter = modelImporter;
        this.modelAssetLoader = modelAssetLoader;
        this.ctx = ctx;
        this.modelExporter = modelExporter;
    }

    @SneakyThrows
    public ModelAsset importAndSaveAsset(ImportedModel importedModel) {
        var meta = new Meta<ModelMeta>();

        var folderName = "model_" + meta.getUuid();
        var assetFolder = assetsStorage.getAssetsFolder().child(folderName);
        assetFolder.file().mkdirs();

        var modelFileName = "model.gltf";

        var modelMeta = new ModelMeta();
        modelMeta.setFile(modelFileName);
        modelMeta.setFormat(ModelMeta.Format.GLTF);
        modelMeta.setBinary(true);

        meta.setType(AssetType.MODEL);
        meta.setAdditional(modelMeta);
        meta.setFile(assetFolder);
        metaService.save(meta);

        //todo remove binary file if it needed
        var model = modelImporter.loadModel(importedModel.getMain());
        modelExporter.export(model, assetFolder.child(modelFileName));

        var res = modelAssetLoader.load(meta);
        return res;
    }

    public HierarchyNode createModelEntity(ImportedModel importedModel) {
        var world = ctx.getCurrentWorld();

        var id = world.create();
        var name = "Model " + id;

        var asset = importAndSaveAsset(importedModel);

        var terrain = createFromAsset(asset);

        world.edit(id)
                .add(new PositionComponent())
                .add(RenderComponent.of(new RenderableObjectDelegate(terrain, ShaderConstants.MODEL)));

        return new HierarchyNode(id, name);
    }

}
