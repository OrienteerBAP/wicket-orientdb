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
package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Locale;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

public class MyBeanPrototyper extends AbstractPrototyper<IMyBean> {

    private static final long serialVersionUID = 1L;

    @Override
    protected IMyBean createInstance(IMyBean proxy) {
        return new MyBean();
    }

    @Override
    protected Class<IMyBean> getMainInterface() {
        return IMyBean.class;
    }

    public static IMyBean newPrototype() {
        return newPrototype(new MyBeanPrototyper());
    }

    @Override
    protected PropertyResolverConverter getPropertyResolverConverter() {
        return new PropertyResolverConverter(new ConverterLocator(), Locale.getDefault());
    }

}
