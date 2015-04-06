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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.command.OCommandOutputListener;

/**
 * Utility {@link OCommandOutputListener} for logging of messages to log
 */
public class LoggerOCommandOutputListener implements OCommandOutputListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoggerOCommandOutputListener.class);
    public static final LoggerOCommandOutputListener INSTANCE = new LoggerOCommandOutputListener();

    private final Logger logger;

    public LoggerOCommandOutputListener() {
        this(LOG);
    }

    public LoggerOCommandOutputListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onMessage(String iText) {
        logger.info(iText);
    }

}
