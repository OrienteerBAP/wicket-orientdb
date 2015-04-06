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
package ru.ydn.wicket.wicketorientdb.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Form component which stops transaction for models updates
 *
 * @param <T>
 */
public class TransactionlessForm<T> extends Form<T> {

    private static final long serialVersionUID = 1L;

    public TransactionlessForm(String id, IModel<T> model) {
        super(id, model);
    }

    public TransactionlessForm(String id) {
        super(id);
    }

    @Override
    protected void beforeUpdateFormComponentModels() {
        super.beforeUpdateFormComponentModels();
        OrientDbWebSession.get().getDatabase().commit();
    }

    @Override
    protected void onValidateModelObjects() {
        OrientDbWebSession.get().getDatabase().begin();
        super.onValidateModelObjects();
    }

}
