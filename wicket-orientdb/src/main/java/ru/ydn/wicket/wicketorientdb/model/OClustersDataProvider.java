package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * {@link SortableDataProvider} for listing of {@link OCluster}s
 */
public class OClustersDataProvider extends AbstractJavaSortableDataProvider<OCluster, String>{

	private static final long serialVersionUID = 1L;

	public OClustersDataProvider() {
        this(new ListOClustersModel());
    }

    public OClustersDataProvider(IModel<? extends Collection<OCluster>> dataModel) {
        super(dataModel);
    }

    @Override
    public IModel<OCluster> model(OCluster object) {
        return new OClusterModel(object);
    }
}
