package com.mbrlabs.mundus.editor.ui.dsl;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.widgets.FloatField;
import com.mbrlabs.mundus.editor.ui.widgets.RadioButtonGroup;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.asset.AssetChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.color.ColorChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiButtonComponent;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiComponent;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiFormTable;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiLabelComponent;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiSlider;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiTabs;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.grid.UiButtonGrid;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.grid.UiTextureGrid;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.util.DelegatingScript;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        var fields = getFieldsMap(closure);
        fields.clear();
        var res = delegateTo(closure, widget);
        widget.setFields(fields);
        widget.setVisible(false); // Set visibility from presenter on entity select event
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
        return new UiLabelComponent((String) args[0], (String) args[1]);
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

    public UiComponent<?> Tabs(Closure<?> closure) {
        return delegateTo(closure, new UiTabs(applicationContext));
    }

    public UiComponent<?> ButtonGrid(Closure<?> closure) {
        return delegateTo(closure, new UiButtonGrid());
    }

    public UiComponent<?> Slider(Closure<?> closure) {
        return delegateTo(closure, new UiSlider());
    }

    public UiComponent<?> FileChooserField(Closure<?> closure) {
        return delegateTo(closure, new UiComponent<>(new FileChooserField()));
    }

    public UiComponent<?> TextureGrid(Closure<?> closure) {
        return delegateTo(closure, new UiTextureGrid());
    }

    public UiComponent<?> RadioButton(Closure<?> closure) {
        return delegateTo(closure, new UiComponent<>(new RadioButtonGroup()));
    }

    private <T extends Actor> UiComponent<T> delegateTo(Closure<?> closure, Supplier<T> creator) {
        var value = new UiComponent<>(creator.get());
        delegateTo(closure, value);
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

    @SuppressWarnings("unchecked")
    public static Map<String, Actor> getFieldsMap(Closure<?> closure) {
        var script = (DelegatingScript) closure.getThisObject();
        var res = (Map<String, Actor>) script.getBinding().getProperty("fields");
        if (res == null) {
            res = new HashMap<>();
            script.getBinding().setProperty("fields", res);
        }
        return res;
    }
}
