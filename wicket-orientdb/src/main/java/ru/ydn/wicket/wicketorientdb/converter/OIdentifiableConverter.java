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
package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;

/**
 * Converter for {@link OIdentifiable} &lt;-&gt; {@link String} Can be
 * overridden for subclasses of {@link OIdentifiable}
 *
 * @param <T>
 */
public class OIdentifiableConverter<T extends OIdentifiable> extends AbstractConverter<T> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public T convertToObject(String value, Locale locale)
            throws ConversionException {
        return (T) convertToOIdentifiable(value, locale);
    }

    public OIdentifiable convertToOIdentifiable(String value, Locale locale) {
        try {
            return new ORecordId(value);
        } catch (Exception e) {
            throw newConversionException("Cannot convert '" + value + "' to " + getTargetType().getSimpleName(), value, locale);
        }
    }

    @Override
    public String convertToString(T value, Locale locale) {
        return value.getIdentity().toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTargetType() {
        return (Class<T>) OIdentifiable.class;
    }

}
