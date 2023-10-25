package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.dsl.UiDslProcessor;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class UiFormTable extends UiComponent<VisTable> {

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
                        addComponent((UiComponent<Actor>) res, closure);
                    }
                    return res;
                }));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    public void addComponent(UiComponent<? extends Actor> component, Closure<?> closure) {
        if (component.getId() != null) {
            UiDslProcessor.getFieldsMap(closure).put(component.getId(), component.getActor());
        }

        Cell<?> cell = actor.add(component.getActor());
        cell.colspan(component.getColspan());
        component.applyStyles(cell);
    }
}
