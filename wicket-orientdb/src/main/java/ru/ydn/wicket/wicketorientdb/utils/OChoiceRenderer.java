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

import org.apache.wicket.markup.html.form.IChoiceRenderer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OChoiceRenderer implements IChoiceRenderer<ODocument> {

    private static final long serialVersionUID = 1L;

    private final String displayExpression;

    private final String idExpression;

    public OChoiceRenderer() {
        super();
        this.displayExpression = null;
        this.idExpression = null;
    }

    public OChoiceRenderer(OProperty displayProperty) {
        this(displayProperty.getName());
    }

    public OChoiceRenderer(OProperty displayProperty, OProperty idProperty) {
        this(displayProperty.getName(), idProperty.getName());
    }

    public OChoiceRenderer(String displayExpression) {
        super();
        this.displayExpression = displayExpression;
        this.idExpression = null;
    }

    public OChoiceRenderer(String displayExpression, String idExpression) {
        super();
        this.displayExpression = displayExpression;
        this.idExpression = idExpression;
    }

    /**
     * @see
     * org.apache.wicket.markup.html.form.IChoiceRenderer#getDisplayValue(java.lang.Object)
     */
    @Override
    public Object getDisplayValue(ODocument object) {
        Object returnValue = object;
        if ((displayExpression != null) && (object != null)) {
            returnValue = object.field(displayExpression);
        }

        if (returnValue == null) {
            return "";
        }

        return returnValue;
    }

    /**
     * @see
     * org.apache.wicket.markup.html.form.IChoiceRenderer#getIdValue(java.lang.Object,
     * int)
     */
    @Override
    public String getIdValue(ODocument object, int index) {
        if (idExpression == null) {
            return Integer.toString(index);
        }

        if (object == null) {
            return "";
        }

        Object returnValue = object.field(idExpression);
        if (returnValue == null) {
            return "";
        }

        return returnValue.toString();
    }
}
