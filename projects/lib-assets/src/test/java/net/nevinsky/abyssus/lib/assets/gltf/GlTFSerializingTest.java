package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GlTFSerializingTest {

    private static final List<String> files = Arrays.asList(
            "aBeautifulGame",
            "animatedMorphCube",
            "boxAnimated"
    );

    private final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();


    @TestFactory
    public Stream<DynamicTest> dynamicTestsForEmployeeWorkflows() {
        return files.stream()
                .map(f -> DynamicTest.dynamicTest(
                        "Check json model for file: " + f, () -> testJsonReadAndWrite(f)
                ));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void testJsonReadAndWrite(String file) {
        var content = String.join("", IOUtils.readLines(
                getClass().getClassLoader().getResourceAsStream("gltf/" + file + "/file.gltf"),
                Charset.defaultCharset()
        ));


        var res = mapper.readValue(content, GlTFDto.class);
        var serialized = mapper.writeValueAsString(res);

        var inputMap = mapper.readValue(content, Map.class);
        var resMap = mapper.readValue(serialized, Map.class);

        var leftFlatMap = FlatMapUtil.flatten(inputMap);
        var rightFlatMap = FlatMapUtil.flatten(resMap);

        var difference = Maps.difference(leftFlatMap, rightFlatMap);

        if (difference.entriesOnlyOnLeft().size() > 0) {
            System.out.println("Entries only on the left\n--------------------------");
            difference.entriesOnlyOnLeft().forEach((key, value) -> System.out.println(key + ": " + value));
        }

        if (difference.entriesOnlyOnRight().size() > 0) {
            System.out.println("\n\nEntries only on the right\n--------------------------");
            difference.entriesOnlyOnRight().forEach((key, value) -> System.out.println(key + ": " + value));
        }

        if (difference.entriesDiffering().size() > 0) {
            System.out.println("\n\nEntries differing\n--------------------------");
            difference.entriesDiffering().forEach((key, value) -> System.out.println(key + ": " + value));
        }

        Assertions.assertEquals(0, difference.entriesOnlyOnLeft().size());
        Assertions.assertEquals(0, difference.entriesOnlyOnRight().size());
        Assertions.assertEquals(0, difference.entriesDiffering().size());
    }
}
