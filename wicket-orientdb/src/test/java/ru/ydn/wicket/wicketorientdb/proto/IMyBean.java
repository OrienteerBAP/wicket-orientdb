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

public interface IMyBean {

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public Integer getInteger();

    public void setInteger(Integer integer);

    public String getSignature();

    public Object getCustom(String param);

    public void setCustom(String param, Object value);

    public Object getCustomString(String param);

    public IMyBean setCustomString(String param, String value);
}
