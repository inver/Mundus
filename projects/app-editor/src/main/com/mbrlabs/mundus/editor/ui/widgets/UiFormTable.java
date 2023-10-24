package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.dsl.UiDslProcessor;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class UiFormTable extends UiComponent<VisTable> {

    private final Map<String, Actor> fields = new HashMap<>();

    private final ApplicationContext applicationContext;

    @Setter
    @Getter
    protected int entityId = -1;

    public UiFormTable(ApplicationContext applicationContext) {
        super(new VisTable());
        this.applicationContext = applicationContext;
//        actor.debugAll();
    }

    @SuppressWarnings("unchecked")
    public void content(Closure<?> closure) {
        closure.setDelegate(
                UiDslProcessor.createProxy(applicationContext, (obj, method, args, proxy) -> {
                    var res = proxy.invokeSuper(obj, args);
                    if ("row".equals(method.getName())) {
                        actor.row();
                    } else if (UiDslProcessor.hasMethod(method.getName())) {
                        addComponent((UiComponent<Actor>) res);
                    }
                    return res;
                }));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    public void addComponent(UiComponent<? extends Actor> component) {
        if (component.getId() != null) {
            fields.put(component.getId(), component.getActor());
        }

        Cell<?> cell = actor.add(component.getActor());
        cell.colspan(component.getColspan());
        component.applyStyles(cell);
    }

    @SuppressWarnings("unchecked")
    public <T extends Actor> T getField(String id, Class<T> clazz) {
        return (T) getField(id);
    }

    public Actor getField(String id) {
        return fields.get(id);
    }
}
