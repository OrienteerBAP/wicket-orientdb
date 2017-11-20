package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Filter criteria for EMBEDDED collections
 * SELECT FROM Test WHERE embedded_collection.{field} CONTAINS value
 */
public class EmbeddedCollectionFieldFilterCriteria extends AbstractFilterCriteria {

    private final IModel<String> key;

    public <T> EmbeddedCollectionFieldFilterCriteria(String field, IModel<String> key, IModel<Collection<T>> model, IModel<Boolean> join) {
        super(field, model, join);
        this.key = key;
    }

    @Override
    protected String apply(String field) {
        return field + "." + key.getObject() + " IN :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_COLLECTION;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || Strings.isNullOrEmpty(key.getObject());
    }
}
