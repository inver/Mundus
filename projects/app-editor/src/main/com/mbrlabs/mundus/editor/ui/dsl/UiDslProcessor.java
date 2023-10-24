package com.mbrlabs.mundus.editor.ui.dsl;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.widgets.AssetChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.FloatField;
import com.mbrlabs.mundus.editor.ui.widgets.UiButtonComponent;
import com.mbrlabs.mundus.editor.ui.widgets.UiComponent;
import com.mbrlabs.mundus.editor.ui.widgets.UiFormTable;
import com.mbrlabs.mundus.editor.ui.widgets.UiLabelComponent;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorChooserField;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class UiDslProcessor extends GroovyObjectSupport {
    private final ApplicationContext applicationContext;

    @SneakyThrows
    public void methodMissing(String name, Object args) {
        throw new IllegalArgumentException("No such method: '" + name + "' with args: " + args);
    }

    public UiComponent<?> ComponentWidget(Closure<?> closure) {
        var widget = new UiComponentWidget(applicationContext);
        var res = delegateTo(closure, widget);
        var instance = applicationContext.getBean(widget.getPresenter());
        instance.init(widget);
        return res;
    }

    public UiComponent<?> FloatField(Closure<?> closure) {
        return delegateTo(closure, FloatField::new);
    }

    public UiComponent<?> Label(Closure<?> closure) {
        return delegateTo(closure, new UiLabelComponent());
    }

    /**
     * @param args: text and layouttypes
     * @return label instance
     */
    public UiComponent<?> Label(Object[] args) {
        var res = new UiLabelComponent();
        res.setText((String) args[0]);
        res.setLayoutTypes((String) args[1]);
        return res;
    }

    public UiComponent<?> Table(Closure<?> closure) {
        return delegateTo(closure, new UiFormTable(applicationContext));
    }

    public UiComponent<VisTextField> TextField(Closure<?> closure) {
        return delegateTo(closure, VisTextField::new);
    }

    /**
     * Marker method
     */
    public Object row(Closure<?> closure) {
        return closure.call();
    }

    public UiComponent<?> CheckBox(Closure<?> closure) {
        return delegateTo(closure, () -> new VisCheckBox(""));
    }

    public UiComponent<?> ColorChooserField(Closure<?> closure) {
        return delegateTo(closure, ColorChooserField::new);
    }

    public UiComponent<?> AssetChooserField(Closure<?> closure) {
        return delegateTo(closure, AssetChooserField::new);
    }

    public UiComponent<?> Button(Closure<?> closure) {
        return delegateTo(closure, new UiButtonComponent());
    }

    private <T extends Actor> UiComponent<T> delegateTo(Closure<?> closure, Supplier<T> creator) {
        var value = new UiComponent<T>(creator.get());
        closure.setDelegate(value);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        return value;
    }

    private UiComponent<?> delegateTo(Closure<?> closure, UiComponent<?> component) {
        closure.setDelegate(component);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        return component;
    }

    public static Object createProxy(ApplicationContext applicationContext, MethodInterceptor methodInterceptor) {
        Enhancer e = new Enhancer();
        e.setClassLoader(UiDslProcessor.class.getClassLoader());
        e.setSuperclass(UiDslProcessor.class);
        e.setCallback(methodInterceptor);
        return e.create(new Class<?>[]{ApplicationContext.class}, new Object[]{applicationContext});
    }

    public static boolean hasMethod(String name) {
        return Arrays.stream(UiDslProcessor.class.getDeclaredMethods())
                .anyMatch(m -> name.equals(m.getName()));
    }
}
