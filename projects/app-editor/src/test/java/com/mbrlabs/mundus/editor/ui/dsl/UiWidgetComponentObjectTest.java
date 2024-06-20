package com.mbrlabs.mundus.editor.ui.dsl;

import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class UiWidgetComponentObjectTest extends BaseCtxTest {
    @Autowired
    private UiDslCreator uiDslCreator;

    @Test
    @SneakyThrows
    void testCreationWidget() {
        var res = uiDslCreator.create("dsl/TestWidget.groovy");

        Assertions.assertNotNull(res);
    }
}
