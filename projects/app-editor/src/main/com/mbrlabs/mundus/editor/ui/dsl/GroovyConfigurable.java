package com.mbrlabs.mundus.editor.ui.dsl;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import lombok.SneakyThrows;

public class GroovyConfigurable extends GroovyObjectSupport {
    @SneakyThrows
    public void methodMissing(String name, Object args) {
        var metaProperty = getMetaClass().getMetaProperty(name);
        if (metaProperty != null) {
            var closure = (Closure<?>) ((Object[]) args)[0];
            Object value = getProperty(name) == null ?
                    metaProperty.getType().getConstructor().newInstance() :
                    getProperty(name);
            closure.setDelegate(value);
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
            setProperty(name, value);
        } else {
            throw new IllegalArgumentException("No such field: " + name);
        }
    }
}
