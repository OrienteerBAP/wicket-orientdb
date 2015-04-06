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
package ru.ydn.wicket.wicketorientdb.model;

import java.io.Serializable;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

public class ModelUtils {

    @SuppressWarnings("unchecked")
    public static <K> IModel<K> model(K o) {
        if (o instanceof ODocument) {
            return (IModel<K>) new ODocumentModel((ODocument) o);
        } else if (o instanceof ODocumentWrapper) {
            return (IModel<K>) new ODocumentWrapperModel<ODocumentWrapper>((ODocumentWrapper) o);
        } else if (o instanceof Serializable) {
            return (IModel<K>) Model.of((Serializable) o);
        } else {
            throw new WicketRuntimeException(ModelUtils.class.getSimpleName() + " can't work with non serializable objects: " + o);
        }
    }
}
