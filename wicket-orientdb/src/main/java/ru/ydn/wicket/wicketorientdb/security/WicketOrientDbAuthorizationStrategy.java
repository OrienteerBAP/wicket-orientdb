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
package ru.ydn.wicket.wicketorientdb.security;

import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;

/**
 * {@link CompoundAuthorizationStrategy} for OrientDB specific applications. It
 * uses:
 * <ul>
 * <li>{@link AnnotationsRoleAuthorizationStrategy}</li>
 * <li>{@link OrientResourceAuthorizationStrategy}</li>
 * </ul>
 */
public class WicketOrientDbAuthorizationStrategy extends
        CompoundAuthorizationStrategy {

    public WicketOrientDbAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
        add(new AnnotationsRoleAuthorizationStrategy(roleCheckingStrategy));
        add(new OrientResourceAuthorizationStrategy());
    }
}
