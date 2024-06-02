/*
 * Copyright 2024 - 2024 Blazebit.
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

package com.blazebit.query.connector.base;

import java.lang.reflect.Method;

/**
 * Accessor for a method based attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class MethodAccessor implements Accessor {
    private final String attributePath;
    private final Method method;

    /**
     * Creates a method accessor.
     *
     * @param attributePath The attribute path for this accessor.
     * @param method The method to obtain an attribute value.
     */
    public MethodAccessor(String attributePath, Method method) {
        this.attributePath = attributePath;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String getAttributePath() {
        return attributePath;
    }

    @Override
    public Object getValue(Object o) {
        try {
            return method.invoke( o );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }
}