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

import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Model for storing of {@link ODocument}
 */
public class ODocumentModel extends LoadableDetachableModel<ODocument> implements IObjectClassAwareModel<ODocument> {

    private static final long serialVersionUID = 1L;
    private ORID orid;
    private ODocument savedDocument;

    public ODocumentModel(ODocument iDocument) {
        super(iDocument);
        if (iDocument != null) {
            orid = iDocument.getIdentity();
        }
    }

    public ODocumentModel(ORID iRID) {
        this.orid = iRID;
    }

    @Override
    public Class<ODocument> getObjectClass() {
        return ODocument.class;
    }

    @Override
    protected ODocument load() {
        if (orid != null && orid.isValid()) {
            try {
                ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
                return db.load(orid);
            } catch (ORecordNotFoundException e) {
                return null;
            }
        } else {
            return savedDocument;
        }
    }

    @Override
    public void detach() {
        if (isAttached()) {
            ODocument doc = getObject();
            if (doc != null) {
                this.orid = doc.getIdentity();
                if (orid != null && orid.isValid()) {
                    savedDocument = null;
                } else {
                    orid = null;
                    savedDocument = doc;
                }
            } else {
                orid = null;
                savedDocument = null;
            }
        }
        super.detach();
    }

    @Override
    public String toString() {
        return "ODocumentModel [orid=" + orid + "]";
    }

}
