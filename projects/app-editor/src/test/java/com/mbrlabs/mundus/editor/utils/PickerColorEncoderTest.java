package com.mbrlabs.mundus.editor.utils;

import com.badlogic.gdx.graphics.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PickerColorEncoderTest {

    private static final int ID = 1;

    @Test
    void testEncodeAndDecode() {
        var encoded = PickerColorEncoder.encodeRayPickColorId(ID);
        var res = PickerColorEncoder.decode(Color.rgba8888(encoded.asColor()));
        Assertions.assertEquals(ID, res);
    }

    @Test
    void testWrongDecodeInput() {
        var res = PickerColorEncoder.decode(-123874628);
        Assertions.assertEquals(-1, res);
    }
}
