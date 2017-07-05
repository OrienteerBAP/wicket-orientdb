package ru.ydn.wicket.wicketorientdb.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.Map;

/**
 * Build new OQueryModel for filter data from OrientDB
 * @param <K> The query gets object type
 */
public interface IQueryBuilder<K> extends IClusterable {
    public OQueryModel<K> build(Map<IModel<OProperty>, IModel<?>> filteredValues);
}
