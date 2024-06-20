package com.mbrlabs.mundus.editor.ui.modules.inspector;

import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

@Getter
public class ComponentWidget extends BaseInspectorWidget {

    @Setter
    protected int entityId = -1;
    @Setter
    private Class<UiComponentPresenter<ComponentWidget>> presenter;

    public ComponentWidget(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void onDelete() {
        //todo component.remove();
        remove();
    }

    @Override
    public void setValues(int entityId) {
        this.entityId = entityId;
    }

    public void content(Closure<?> closure) {
        // delegate to inner table
        getFormTable().content(closure);
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String id, Class<T> clazz) {
        return (T) getFormTable().getField(id);
    }
}