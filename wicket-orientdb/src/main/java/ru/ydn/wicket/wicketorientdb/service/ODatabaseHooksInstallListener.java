package ru.ydn.wicket.wicketorientdb.service;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.utils.ODbUtils;

/**
 * Database listener which register OrientDB hooks
 */
public class ODatabaseHooksInstallListener implements ODatabaseLifecycleListener {

    private final OrientDbWebApplication app;

    public ODatabaseHooksInstallListener(OrientDbWebApplication app) {
        this.app = app;
    }

    @Override
    public void onOpen(ODatabaseInternal iDatabase) {
        ODbUtils.registerHooks(iDatabase, app.getOrientDbSettings().getORecordHooks());
    }

    @Override
    public void onCreate(ODatabaseInternal iDatabase) {
        ODbUtils.registerHooks(iDatabase, app.getOrientDbSettings().getORecordHooks());
        //Fix for "feature" appeared in OrientDB 2.1.1
        //Issue: https://github.com/orientechnologies/orientdb/issues/4906
        ODbUtils.fixOrientDBRights(iDatabase);
    }

    @Override
    public void onClose(ODatabaseInternal iDatabase) {/*NOP*/}

    @Override
    public void onDrop(ODatabaseInternal iDatabase) {/*NOP*/}


    public PRIORITY getPriority() {
        return PRIORITY.REGULAR;
    }

    @Override
    public void onLocalNodeConfigurationRequest(ODocument arg0) {/*NOP*/}
}
