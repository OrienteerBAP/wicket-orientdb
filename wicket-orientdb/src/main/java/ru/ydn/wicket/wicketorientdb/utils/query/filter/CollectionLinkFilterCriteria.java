package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Link collection filter
 * SELECT FROM Class WHERE link IN [#21:0, #22:0, #23:0]
 */
public class CollectionLinkFilterCriteria extends CollectionFilterCriteria<ODocument> {

	private static final long serialVersionUID = 1L;
	private final boolean list;

    public CollectionLinkFilterCriteria(String field, IModel<Collection<ODocument>> model, boolean list, IModel<Boolean> join) {
        super(field, model, join);
        this.list = list;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected String apply(String field) {
        return field + " IN [:" + getName()+"]";
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return list ? FilterCriteriaType.LINKLIST : FilterCriteriaType.LINKSET;
    }
}
