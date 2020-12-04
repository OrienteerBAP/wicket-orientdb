package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Filter criteria for EMBEDDED collections
 * SELECT FROM Test WHERE embedded_collection.{field} CONTAINS value
 * @param <C> type of objects in collection for filtering
 */
public class EmbeddedCollectionFieldFilterCriteria<C> extends AbstractFilterCriteria<Collection<C>> {

	private static final long serialVersionUID = 1L;
	private final IModel<String> key;

    public EmbeddedCollectionFieldFilterCriteria(String field, IModel<String> key, IModel<Collection<C>> model, IModel<Boolean> join) {
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
