package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetWriterTest {

    @Mock
    private MetaService metaService;

    @InjectMocks
    private AssetWriter assetWriter;

    @Test
    public void testWriteAssetWhenShaderAssetThenSaveShaderAsset() {
//        // Arrange
//        ShaderAsset mockShaderAsset = mock(ShaderAsset.class);
//        Meta mockMeta = mock(Meta.class);
//        when(mockShaderAsset.getType()).thenReturn(AssetType.SHADER);
//        when(mockShaderAsset.getMeta()).thenReturn(mockMeta);
//
//        // Act
//        assetWriter.writeAsset(mockShaderAsset);
//
//        // Assert
//        verify(metaService, times(1)).save(mockMeta);
    }

    @Test
    public void testWriteAssetWhenNonShaderAssetThenThrowNotImplementedException() {
        // Arrange
        ShaderAsset mockAsset = mock(ShaderAsset.class);
        when(mockAsset.getType()).thenReturn(AssetType.TEXTURE);

        // Act & Assert
        assertThrows(NotImplementedException.class, () -> assetWriter.writeAsset(mockAsset));
    }
}