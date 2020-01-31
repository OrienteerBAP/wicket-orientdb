package ru.ydn.wicket.wicketorientdb.service;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.components.IHookPosition;
import ru.ydn.wicket.wicketorientdb.utils.ODbUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        registerHooks(iDatabase);
    }

    @Override
    public void onCreate(ODatabaseInternal iDatabase) {
        registerHooks(iDatabase);
        //Fix for "feature" appeared in OrientDB 2.1.1
        //Issue: https://github.com/orientechnologies/orientdb/issues/4906
        ODbUtils.fixOrientDBRights(iDatabase);
    }

    public void registerHooks(ODatabaseInternal db) {
        List<Class<? extends ORecordHook>> hooksToRegister = getHooksToRegister(db);

        hooksToRegister.forEach(hookClass -> {
            ORecordHook hook = createHook(hookClass, db);
            if (hook != null) {
                if (hook instanceof IHookPosition) {
                    db.registerHook(hook,((IHookPosition) hook).getPosition());
                } else {
                    db.registerHook(hook);
                }
            }
        });
    }

    private List<Class<? extends ORecordHook>> getHooksToRegister(ODatabaseInternal<?> db) {
        Set<Class<? extends ORecordHook>> registeredHooks = db.getHooks().keySet().stream()
                .map(ORecordHook::getClass).collect(Collectors.toSet());

        return app.getOrientDbSettings()
                .getORecordHooks().stream()
                .filter(hook -> !registeredHooks.contains(hook))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private ORecordHook createHook(Class<? extends ORecordHook> clazz, ODatabaseInternal iDatabase) {
        if (!(iDatabase instanceof ODatabaseDocument)) {
            return null;
        }

        try {
            return clazz.getConstructor(ODatabaseDocument.class).newInstance(iDatabase);
        } catch (Exception e) {
            try {
                return clazz.newInstance();
            } catch (Exception e1) {
                throw new IllegalStateException("Can't initialize hook "+clazz.getName(), e);
            }
        }
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
