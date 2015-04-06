/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.lang.Args;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Transformer for wrapping of {@link ODocument}
 *
 * @param <T>
 */
public class DocumentWrapperTransformer<T> implements Function<ODocument, T>, Serializable {

    private static final long serialVersionUID = 1L;
    private final Class<? extends T> wrapperClass;
    private transient Constructor<? extends T> constructor;

    /**
     * @param wrapperClass to wrap into
     */
    public DocumentWrapperTransformer(Class<? extends T> wrapperClass) {
        Args.notNull(wrapperClass, "wrapperClass");
        this.wrapperClass = wrapperClass;
        //To check that appropriate constructor exists
        getConstructor();
    }

    private Constructor<? extends T> getConstructor() {
        if (constructor == null) {
            try {
                constructor = wrapperClass.getConstructor(ODocument.class);
            } catch (NoSuchMethodException e) {
                throw new WicketRuntimeException("Approapriate constructor was not found. DocumentWrapper class: " + wrapperClass.getName());
            } catch (SecurityException e) {
                throw new WicketRuntimeException("Can't get access to constructor of class: " + wrapperClass.getName(), e);
            }
        }
        return constructor;
    }

    @Override
    public T apply(ODocument input) {
        try {
            return getConstructor().newInstance(input);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't create wrapper instance of class '" + wrapperClass.getName() + "' for document: " + input, e);
        }
    }

    public Class<? extends T> getWrapperClass() {
        return wrapperClass;
    }

}
