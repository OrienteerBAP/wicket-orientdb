package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class in collection filter
 * select from Parent where @class in ["Child", "Child2"] */
public class ClassInCollectionFilterCriteria extends AbstractFilterCriteria {

	private static final long serialVersionUID = 1L;

	public ClassInCollectionFilterCriteria(String field, IModel<Collection<String>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " IN " + buildQuery();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CLASS_IN_COLLECTION;
    }

    @SuppressWarnings("unchecked")
    private String buildQuery() {
        IModel<Collection<String>> model = (IModel<Collection<String>>) getModel();
        Collection<String> classes = model.getObject();
        if (classes == null || classes.isEmpty()) {
            return "[]";
        }
        List<String> classesList = new ArrayList<>(classes);
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < classesList.size(); i++) {
            sb.append("\"")
                    .append(classesList.get(i))
                    .append("\"");
            if (i < classesList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
