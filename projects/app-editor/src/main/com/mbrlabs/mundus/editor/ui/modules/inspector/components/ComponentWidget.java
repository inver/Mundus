package com.mbrlabs.mundus.editor.ui.modules.inspector.components;

import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class ComponentWidget extends BaseInspectorWidget {

    @Setter
    @Getter
    protected int entityId = -1;
    @Getter
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