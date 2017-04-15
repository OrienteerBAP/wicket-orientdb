package ru.ydn.wicket.wicketorientdb.filter.impl;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.filter.IODataFilter;
import ru.ydn.wicket.wicketorientdb.filter.IQueryBuilder;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.Map;

/**
 * @author Vitaliy Gonchar
 * @param <K> type of provide object
 */
public class DefaultDataFilter<K> implements IODataFilter<K, String> {

    private IQueryBuilder<K> queryBuilder;
    private OQueryModel<K> queryModel;
    private final Map<IModel<OProperty>, IModel<?>> filteredValues = Maps.newHashMap();

    public DefaultDataFilter(IModel<OClass> filteredClass) {
        this(filteredClass, null);
    }

    public DefaultDataFilter(IModel<OClass> filteredClass, IQueryBuilder<K> queryBuilder) {
        Args.notNull(filteredClass, "filteredClass");
        this.queryBuilder = queryBuilder;
        initPropertyFilters(filteredClass.getObject());
    }

    @Override
    public IODataFilter<K, String> setQueryModel(OQueryModel<K> queryModel) {
        this.queryModel = queryModel;
        return this;
    }

    @Override
    public OQueryModel<K> getQueryModel() {
        return queryModel;
    }

    @Override
    public OQueryModel<K> createQueryModel() {
        if (queryBuilder != null) {
            queryModel = queryBuilder.build(filteredValues);
        }
        return queryModel;
    }

    @Override
    public IModel<?> getFilteredValueByProperty(String filteredProperty) {
        for (IModel<OProperty> propertyModel : filteredValues.keySet()) {
            OProperty property = propertyModel.getObject();
            if (property.getName().equals(filteredProperty)) {
                return filteredValues.get(propertyModel);
            }
        }
        return Model.of();
    }

    @Override
    public void setQueryBuilder(IQueryBuilder<K> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    private void initPropertyFilters(OClass filteredClass) {
        for (OProperty property : filteredClass.properties()) {
            filteredValues.put(new OPropertyModel(property), Model.of());
        }
    }
}
