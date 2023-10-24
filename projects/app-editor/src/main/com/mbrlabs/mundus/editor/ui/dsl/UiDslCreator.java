package com.mbrlabs.mundus.editor.ui.dsl;

import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UiDslCreator {

    private final ApplicationContext applicationContext;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T create(String file) {
        var cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        var shell = new GroovyShell(getClass().getClassLoader(), new Binding(), cc);
        var script = (DelegatingScript) shell.parse(getClass().getClassLoader().getResource(file).toURI());

        var dslProcessor = new UiDslProcessor(applicationContext);
        script.setDelegate(dslProcessor);

        return (T) script.run();

    }
}
