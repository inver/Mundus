package com.mbrlabs.mundus.editor.config;

@SpringBootTest(classes = MapperConfig.class)
public class MapperConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testMapperWhenCalledThenReturnConfiguredObjectMapper() {
        // Arrange
        ObjectMapper expectedMapper = new ObjectMapper();
        expectedMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        expectedMapper.configure(ALLOW_NON_NUMERIC_NUMBERS, true);

        // Act
        ObjectMapper actualMapper = context.getBean(ObjectMapper.class);

        // Assert
        assertThat(actualMapper).isNotNull();
        assertThat(actualMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isEqualTo(
                expectedMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertThat(actualMapper.isEnabled(ALLOW_NON_NUMERIC_NUMBERS)).isEqualTo(
                expectedMapper.isEnabled(ALLOW_NON_NUMERIC_NUMBERS));
    }
}