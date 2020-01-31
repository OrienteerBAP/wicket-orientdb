package ru.ydn.wicket.wicketorientdb.utils;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import ru.ydn.wicket.wicketorientdb.components.IHookPosition;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;

/**
 * Database utils
 */
public final class ODbUtils {

    private ODbUtils() {}

    /**
     * Required for explicit update of rights due to changes in OrientDB 2.2.23
     * Related issue: https://github.com/orientechnologies/orientdb/issues/7549
     * @param db - database to apply fix on
     */
    public static void fixOrientDBRights(ODatabase<?> db) {
        OSecurity security = db.getMetadata().getSecurity();
        ORole readerRole = security.getRole("reader");
        readerRole.grant(ResourceGeneric.CLUSTER, "orole", ORole.PERMISSION_READ);
        readerRole.grant(ResourceGeneric.CLUSTER, "ouser", ORole.PERMISSION_READ);
        readerRole.grant(ResourceGeneric.CLASS, "orole", ORole.PERMISSION_READ);
        readerRole.grant(ResourceGeneric.CLASS, "ouser", ORole.PERMISSION_READ);
        readerRole.grant(ResourceGeneric.SYSTEM_CLUSTERS, null, ORole.PERMISSION_READ);
        readerRole.save();
        ORole writerRole = security.getRole("writer");
        writerRole.grant(ResourceGeneric.CLUSTER, "orole", ORole.PERMISSION_READ);
        writerRole.grant(ResourceGeneric.CLUSTER, "ouser", ORole.PERMISSION_READ);
        writerRole.grant(ResourceGeneric.CLASS, "orole", ORole.PERMISSION_READ);
        writerRole.grant(ResourceGeneric.CLASS, "ouser", ORole.PERMISSION_READ);
        writerRole.grant(ResourceGeneric.SYSTEM_CLUSTERS, null, ORole.PERMISSION_READ);
        writerRole.save();
    }

    /**
     * Register hooks in database
     * @param db database
     * @param candidates list of hooks for register
     */
    public static void registerHooks(ODatabaseInternal<?> db, List<Class<? extends ORecordHook>> candidates) {
        List<Class<? extends ORecordHook>> hooksToRegister = getHooksToRegister(db, candidates);

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

    /**
     * Unregister hooks from database
     * @param db database
     * @param candidates hooks for unregister
     */
    public static void unregisterHooks(ODatabaseInternal<?> db, List<Class<? extends ORecordHook>> candidates) {
        Set<ORecordHook> hooksToUnregister = getHooksToUnregister(db, candidates);
        hooksToUnregister.forEach(db::unregisterHook);
    }

    private static List<Class<? extends ORecordHook>> getHooksToRegister(ODatabaseInternal<?> db, List<Class<? extends ORecordHook>> candidates) {
        Set<Class<? extends ORecordHook>> registeredHooks = db.getHooks().keySet().stream()
                .map(ORecordHook::getClass).collect(Collectors.toSet());

        return candidates.stream()
                .filter(hook -> !registeredHooks.contains(hook))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static Set<ORecordHook> getHooksToUnregister(ODatabaseInternal<?> db, List<Class<? extends ORecordHook>> candidates) {
        return db.getHooks().keySet().stream()
                .filter(hook -> candidates.contains(hook.getClass()))
                .collect(Collectors.toSet());
    }

    private static ORecordHook createHook(Class<? extends ORecordHook> clazz, ODatabaseInternal<?> iDatabase) {
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
}
