package com.mbrlabs.mundus.editor.core.assets;

@ExtendWith(MockitoExtension.class)
public class EditorModelServiceTest {

    @Mock
    private MetaService metaService;

    @Mock
    private AssetsStorage assetsStorage;

    @Mock
    private ModelImporter modelImporter;

    @Mock
    private ModelAssetLoader modelAssetLoader;

    @Mock
    private EditorCtx ctx;

    @Mock
    private EcsService ecsService;

    @InjectMocks
    private EditorModelService editorModelService;

    @BeforeEach
    public void setUp() {
        // Set up the test environment before each test
    }

    @Test
    public void testImportAndSaveAssetWhenValidModelThenSaveModel() {
        // Arrange
        ImportedModel importedModel = mock(ImportedModel.class);
        ModelAsset modelAsset = mock(ModelAsset.class);
        Meta meta = new Meta();

        when(modelImporter.loadModelAndSaveForAsset(any(), any())).thenReturn(modelAsset);
        when(modelAssetLoader.load(any())).thenReturn(modelAsset);

        // Act
        ModelAsset result = editorModelService.importAndSaveAsset(importedModel);

        // Assert
        verify(metaService, times(1)).save(any(Meta.class));
        assertEquals(modelAsset, result);
    }

    @Test
    public void testImportAndSaveAssetWhenErrorDuringLoadingThenDoNotSaveModel() {
        // Arrange
        ImportedModel importedModel = mock(ImportedModel.class);
        when(modelImporter.loadModelAndSaveForAsset(any(), any())).thenThrow(IOException.class);

        // Act and Assert
        try {
            editorModelService.importAndSaveAsset(importedModel);
        } catch (Exception e) {
            verify(metaService, never()).save(any(Meta.class));
        }
    }
}