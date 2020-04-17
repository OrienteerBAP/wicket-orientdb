package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.storage.OCluster;
import com.orientechnologies.orient.core.storage.OStorage;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link IModel} to list all {@link OCluster}s
 */
public class ListOClustersModel extends AbstractListModel<OCluster>{
	
	private static final long serialVersionUID = 1L;

	@Override
    protected Collection<OCluster> getData() {
        List<OCluster> cluster = new ArrayList<>();
        for(OCluster ocluster : getClusterInstances()) {
            cluster.add(ocluster);
        }
        return cluster;
    }

    private Collection<? extends OCluster> getClusterInstances() {
        return getStorage().getClusterInstances();
    }

    private OStorage getStorage() {
        return ODatabaseRecordThreadLocal.instance().get().getDatabaseOwner().getStorage();
    }
}
