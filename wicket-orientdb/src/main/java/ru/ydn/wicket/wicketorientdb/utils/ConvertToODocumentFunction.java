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

import org.apache.wicket.WicketRuntimeException;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Function} for convertion of following types to {@link ODocument}
 * <ul>
 * <li>{@link ORID}</li>
 * <li>{@link CharSequence}</li>
 * </ul>
 *
 * @param <F>
 */
public class ConvertToODocumentFunction<F> implements Function<F, ODocument>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final ConvertToODocumentFunction<?> INSTANCE = new ConvertToODocumentFunction<Object>();

    @Override
    public ODocument apply(F input) {
        if (input == null) {
            return null;
        } else if (input instanceof ODocument) {
            return (ODocument) input;
        } else if (input instanceof ORID) {
            return ((ORID) input).getRecord();
        } else if (input instanceof CharSequence) {
            return new ORecordId(input.toString()).getRecord();
        } else {
            throw new WicketRuntimeException("Object '" + input + "' of type '" + input.getClass() + "' can't be converted to ODocument");
        }
    }

}
