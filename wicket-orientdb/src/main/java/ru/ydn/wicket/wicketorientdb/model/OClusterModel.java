package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.storage.OCluster;
import com.orientechnologies.orient.core.storage.OStorage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Model for storing {@link OCluster} instance
 */
public class OClusterModel extends PrototypeLoadableDetachableModel<OCluster>{

    private IModel<String> clusterNameModel;

    public OClusterModel(OCluster oCluster) {
        super(oCluster);
    }

    public OClusterModel(String clusterName) {
        this.clusterNameModel = Model.of(clusterName);
    }


    @Override
    protected OCluster loadInstance() {
        if(clusterNameModel != null && clusterNameModel.getObject() != null) {
            return getStorage().getClusterByName(clusterNameModel.getObject());
        }
        return null;
    }

    private OStorage getStorage() {
        return ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner().getStorage();
    }

    @Override
    protected void handleObject(OCluster object) {
        String name = object!=null ? object.getName():null;
        if(clusterNameModel!=null)
        {
            clusterNameModel.setObject(name);
        }
        else
        {
            clusterNameModel = Model.of(name);
        }
    }

    @Override
    public Class<OCluster> getObjectClass() {
        return OCluster.class;
    }

    @Override
    protected void onDetach() {
        if(clusterNameModel!=null)
        {
            OCluster cluster = getObject();
            if(cluster!=null && !cluster.getName().equals(clusterNameModel.getObject()))
            {
                clusterNameModel.setObject(cluster.getName());
            }
            clusterNameModel.detach();
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OClusterModel that = (OClusterModel) o;

        if (clusterNameModel != null ? !clusterNameModel.equals(that.clusterNameModel) : that.clusterNameModel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clusterNameModel != null ? clusterNameModel.hashCode() : 0);
        return result;
    }
}
