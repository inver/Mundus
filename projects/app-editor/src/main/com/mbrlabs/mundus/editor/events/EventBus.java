/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.events;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Event bus via reflection.
 * <p>
 * Subscribers need to provide a public method, annotated with @Subscribe and 1
 * parameter as event type.
 * <p>
 * Inspired by the Otto Event Bus for Android.
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
// TODO improve/test performance might not be that great
@Slf4j
@Component
public class EventBus {

    private final Map<Class<?>, List<Pair<Object, Method>>> methodsMap = new HashMap<>();

    public void register(Object subscriber) {
        var current = System.currentTimeMillis();
        if (subscriber == null) {
            return;
        }

        for (var method : subscriber.getClass().getDeclaredMethods()) {
            if (!checkMethod(method)) {
                continue;
            }
            //set method public. Show test case with SomeListenerClass
            method.setAccessible(true);

            var clazz = method.getParameterTypes()[0];
            var list = methodsMap.computeIfAbsent(clazz, k -> new ArrayList<>());
            list.add(Pair.of(subscriber, method));
        }
        log.debug("Register event executed in {}ms.", (System.currentTimeMillis() - current));
    }

    public void unregister(Object subscriber) {
        throw new NotImplementedException("TODO");
//        subscribers.remove(subscriber);
    }

    public void post(Event event) {
        var current = System.currentTimeMillis();

        var methods = methodsMap.get(event.getClass());
        if (CollectionUtils.isEmpty(methods)) {
            return;
        }

        for (var pair : new ArrayList<>(methods)) {
            try {
                pair.getRight().invoke(pair.getLeft(), event);
                log.debug("Event {} to {} delivered", event, pair.getLeft());
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("ERROR", e);
            }
        }

        log.debug("Post event executed in {}ms", (System.currentTimeMillis() - current));
    }

    private boolean checkMethod(Method method) {
        if (AnnotationUtils.findAnnotation(method, Subscribe.class) == null) {
            return false;
        }

        if (method.getParameterCount() != 1) {
            return false;
        }

        return Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }
}
