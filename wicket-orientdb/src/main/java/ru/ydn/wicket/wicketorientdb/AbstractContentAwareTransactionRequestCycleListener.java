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
package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.IRequestListener;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * {@link IRequestListener} for wrapping interesting content by methods start()
 * and end(). Usefull for transactions
 */
public abstract class AbstractContentAwareTransactionRequestCycleListener extends AbstractRequestCycleListener {

    private final static MetaDataKey<Boolean> IN_PROGRESS_KEY = new MetaDataKey<Boolean>() {
        private static final long serialVersionUID = 1L;
    };

    @Override
    public void onRequestHandlerResolved(RequestCycle cycle,
            IRequestHandler handler) {
        if (isInProgress(cycle)) {
            return;
        }
        if (isOurContent(cycle, handler)) {
            setInProgress(cycle, true);
            start(cycle);
        }
    }

    @Override
    public void onEndRequest(RequestCycle cycle) {
        if (isInProgress(cycle)) {
            end(cycle);
        }
    }

    public boolean isInProgress(RequestCycle cycle) {
        Boolean inProgress = cycle.getMetaData(IN_PROGRESS_KEY);
        return inProgress != null ? inProgress : false;
    }

    public void setInProgress(RequestCycle cycle, boolean inProgress) {
        cycle.setMetaData(IN_PROGRESS_KEY, inProgress);
    }

    /**
     * Request starts.
     *
     * @param cycle
     */
    public abstract void start(RequestCycle cycle);

    /**
     * Request ends.
     *
     * @param cycle
     */
    public abstract void end(RequestCycle cycle);

    /**
     * Predicate method to identify is that our content or not.
     *
     * @param cycle
     * @param handler
     * @return
     */
    public abstract boolean isOurContent(RequestCycle cycle, IRequestHandler handler);
}
