package com.mbrlabs.mundus.editor.ui.dsl;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiComponent;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class UiDslCreator {

    private final ApplicationContext applicationContext;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T extends UiComponent<?>> T create(String file) {
        var cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        var shell = new GroovyShell(getClass().getClassLoader(), new Binding(), cc);

        var dslProcessor = new UiDslProcessor(applicationContext);

        var script = (DelegatingScript) shell.parse(getClass().getClassLoader().getResource(file).toURI());
        script.setDelegate(dslProcessor);
        script.getBinding().setProperty("fields", new HashMap<String, Actor>());

        return (T) script.run();
    }
}
