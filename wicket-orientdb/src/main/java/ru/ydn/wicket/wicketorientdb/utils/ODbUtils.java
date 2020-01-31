package ru.ydn.wicket.wicketorientdb.utils;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;

import static com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;

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
}
